package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumDependency;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumInteraction;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumWGFlags;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.ActionHandler;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Players;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;

public class onBlockBreakEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void BlockBreakEvent(final BlockBreakEvent e)
	{
		
		if (e.isCancelled()) {
			return;
		}
		
		final ItemStack block = Main.getBlocksUtils().getItemStackByBlock(e.getBlock());
		
		final Player player = e.getPlayer();
		
		final Location location = e.getBlock().getLocation();
		final Location bLocation = location.clone().add(0, -1, 0);
		
		final GeneratorLocation gLocation = Locations.get(location);
		final GeneratorLocation bgLocation = Locations.get(bLocation);
		
		Generator generator = null;
		Generator bGenerator = null;
		if (gLocation != null){generator = gLocation.getGenerator();}
		if (bgLocation != null){bGenerator = bgLocation.getGenerator();}
		
		if (bGenerator != null && bGenerator.getType() == GeneratorType.DOUBLE)
		{	
			if (bGenerator.getChances().containsKey(block) 
					|| block.equals(bGenerator.getPlaceholder())) {
				
				if (block.equals(bGenerator.getPlaceholder()) 
						|| !Players.getPlayer(player).canUse(bgLocation)
						|| !hasPermissionToMineCheck(player, bGenerator)) {
					e.setCancelled(true);
					return;
				}
				else
				{
					Schedules.schedule(bgLocation);
					return;
				}
			}
		}

		if (generator != null && generator.getType() == GeneratorType.DOUBLE)
		{
			if (generator.getGeneratorBlock().equals(block)) {
				ActionHandler.handler(EnumInteraction.BREAK, gLocation, player);
				e.setCancelled(true);
				return;
			}
			
		}
		

		if (generator != null && generator.getType() == GeneratorType.SINGLE)
		{
			if (generator.getChances().containsKey(block) || generator.getGeneratorBlock().equals(block) || block.equals(generator.getPlaceholder())) {
				
				if (ActionHandler.handler(EnumInteraction.BREAK, gLocation, player)
						|| block.equals(generator.getPlaceholder()) 
						|| !Players.getPlayer(player).canUse(gLocation)
						|| !hasPermissionToMineCheck(player, generator)) {
					e.setCancelled(true);
					return;
				}
				
				if (block.equals(generator.getGeneratorBlock()) && !generator.getChances().containsKey(generator.getGeneratorBlock())) {
					return;
				}
				
				Schedules.schedule(gLocation);
				return;
			}
		}
		
		if (Main.dependencies.contains(EnumDependency.WorldGuard) && !player.hasPermission("kgenerators.bypass.worldguard") && Main.getWorldGuardUtils().worldGuardFlagCheck(location, player, EnumWGFlags.ONLY_GEN_BREAK))
		{
			Lang.sendMessage(player, EnumMessage.GeneratorsDiggingOnlyGen);
			e.setCancelled(true);
			return;
		}
		
	}
	
	boolean hasPermissionToMineCheck (Player player, Generator generator)
	{
		String permission = "kgenerators.mine." + generator.getId();
		if (!player.hasPermission(permission))
		{
			Lang.addReplecable("<permission>", permission);
			Lang.addReplecable("<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
			Lang.sendMessage(player, EnumMessage.GeneratorsDiggingNoPermission);
			return false;
		}
		return true;
	}
}
