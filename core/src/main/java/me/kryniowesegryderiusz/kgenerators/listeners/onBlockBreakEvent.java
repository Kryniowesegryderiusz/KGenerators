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
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.Interaction;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.enums.WGFlag;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.ActionHandler;
import me.kryniowesegryderiusz.kgenerators.hooks.BentoBoxHook;
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
				
				if ((block.equals(bGenerator.getPlaceholder()) && Schedules.getSchedules().containsKey(bgLocation))
						|| !Players.getPlayer(player).canUse(bgLocation)
						|| !hasPermissionToMineCheck(player, bGenerator)
						|| !hasDependenciesCheck(player, location)) {
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
				ActionHandler.handler(Interaction.BREAK, gLocation, player);
				e.setCancelled(true);
				return;
			}
			
		}
		

		if (generator != null && generator.getType() == GeneratorType.SINGLE)
		{
			if (generator.getChances().containsKey(block) || generator.getGeneratorBlock().equals(block) || block.equals(generator.getPlaceholder())) {
				
				if (ActionHandler.handler(Interaction.BREAK, gLocation, player)
						|| (block.equals(generator.getPlaceholder()) && Schedules.getSchedules().containsKey(gLocation)) 
						|| !Players.getPlayer(player).canUse(gLocation)
						|| !hasPermissionToMineCheck(player, generator)
						|| !hasDependenciesCheck(player, location)) {
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
		
		/*
		 * 
		 */
		
		if (Main.dependencies.contains(Dependency.WorldGuard) && !player.hasPermission("kgenerators.bypass.worldguard") && Main.getWorldGuardUtils().worldGuardFlagCheck(location, player, WGFlag.ONLY_GEN_BREAK))
		{
			Lang.sendMessage(player, Message.GENERATORS_DIGGING_ONLY_GEN);
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
			Lang.sendMessage(player, Message.GENERATORS_DIGGING_NO_PERMISSION);
			return false;
		}
		return true;
	}
	
	/**
	 * Check if could break
	 * @return
	 */
	boolean hasDependenciesCheck(Player player, Location location)
	{
		if (Main.dependencies.contains(Dependency.BentoBox) && !player.hasPermission("kgenerators.bypass.bentobox") && !BentoBoxHook.isAllowed(player, BentoBoxHook.Type.USE_FLAG))
		{
			Lang.sendMessage(player, Message.GENERATORS_DIGGING_CANT_HERE);
			return false;
		}
		return true;
	}
}
