package me.kryniowesegryderiusz.KGenerators.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumWGFlags;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.GenerateBlock;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.PerPlayerGenerators;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.PickUp;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.Remove;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumPickUpMode;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;

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
		
		final GeneratorLocation gLocation = Main.generatorsLocations.get(location);
		final GeneratorLocation bgLocation = Main.generatorsLocations.get(bLocation);
		
		Generator generator = null;
		Generator bGenerator = null;
		if (gLocation != null){generator = gLocation.getGenerator();}
		if (bgLocation != null){bGenerator = bgLocation.getGenerator();}
		
		
		if (bGenerator != null && bGenerator.getType().equals("double"))
		{	
			if (bGenerator.getChances().containsKey(block) 
					|| block.equals(bGenerator.getPlaceholder())) {
				
				if (block.equals(bGenerator.getPlaceholder()) 
						|| !PerPlayerGenerators.canUse(player, bgLocation) 
						|| !hasPermissionToMineCheck(player, bGenerator)) {
					e.setCancelled(true);
					return;
				}
				else
				{
					GenerateBlock.generateBlock(location, bGenerator, 1);
					return;
				}
			}
		}

		if (generator != null && generator.getType().equals("double"))
		{
			if (generator.getGeneratorBlock().equals(block)) {

				PickUp.isPickingUpCheck(EnumPickUpMode.BREAK, player, location, gLocation);
				e.setCancelled(true);
				return;
			}
			
		}
		

		if (generator != null && generator.getType().equals("single"))
		{
			if (generator.getChances().containsKey(block) || generator.getGeneratorBlock().equals(block) || block.equals(generator.getPlaceholder())) {
				
				if (PickUp.isPickingUpCheck(EnumPickUpMode.BREAK, player, location, gLocation)
						|| block.equals(generator.getPlaceholder()) 
						|| !PerPlayerGenerators.canUse(player, gLocation) 
						|| !hasPermissionToMineCheck(player, generator)) {
					e.setCancelled(true);
					return;
				}
				
				if (block.equals(generator.getGeneratorBlock()) && !generator.getChances().containsKey(generator.getGeneratorBlock())) {
					return;
				}
				
				GenerateBlock.generateBlock(location, generator, 1);
				return;
			}
		}
		
		if (Main.enableWorldGuardChecks && Main.dependencies.contains("WorldGuard") && !player.hasPermission("kgenerators.bypass.worldguard") && Main.getWorldGuardUtils().worldGuardFlagCheck(location, player, EnumWGFlags.ONLY_GEN_BREAK))
		{
			LangUtils.sendMessage(player, EnumMessage.GeneratorsDiggingOnlyGen);
			e.setCancelled(true);
			return;
		}
		
	}
	
	boolean hasPermissionToMineCheck (Player player, Generator generator)
	{
		String permission = "kgenerators.mine." + generator.getId();
		if (Main.restrictMiningByPermission && !player.hasPermission(permission))
		{
			LangUtils.addReplecable("<permission>", permission);
			LangUtils.addReplecable("<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
			LangUtils.sendMessage(player, EnumMessage.GeneratorsDiggingNoPermission);
			return false;
		}
		return true;
	}
}
