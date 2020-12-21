package me.kryniowesegryderiusz.KGenerators.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.GenerateBlockFunction;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManager;
import me.kryniowesegryderiusz.KGenerators.KGenerators;
import me.kryniowesegryderiusz.KGenerators.PerPlayerGenerators;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.EnumsManager.EnumWGFlags;
import me.kryniowesegryderiusz.KGenerators.EnumsManager.Message;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;

public class onBlockBreakEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void BlockBreakEvent(final BlockBreakEvent e)
	{
		
		if (e.isCancelled()) {
			return;
		}
		
		final ItemStack block = KGenerators.getBlocksUtils().getItemStackByBlock(e.getBlock());
		
		final Player player = e.getPlayer();
		
		final Location location = e.getBlock().getLocation();
		final Location bLocation = location.clone().add(0, -1, 0);
		
		final GeneratorLocation gLocation = KGenerators.generatorsLocations.get(location);
		final GeneratorLocation bgLocation = KGenerators.generatorsLocations.get(bLocation);
		
		Generator generator = null;
		Generator bGenerator = null;
		if (gLocation != null){generator = gLocation.getGenerator();}
		if (bgLocation != null){bGenerator = bgLocation.getGenerator();}
		
		
		if (bGenerator != null && bGenerator.getType().equals("double"))
		{	
			if (bGenerator.getChances().containsKey(block) || block.equals(bGenerator.getPlaceholder())) {
				
				if (block.equals(bGenerator.getPlaceholder()) || !PerPlayerGenerators.canUse(player, bgLocation)) {
					e.setCancelled(true);
					return;
				}
				else
				{
					GenerateBlockFunction.generateBlock(location, bGenerator, 1);
					return;
				}
			}
		}

		if (generator != null && generator.getType().equals("double"))
		{
			if (generator.getGeneratorBlock().equals(block)) {

				checkIfPickingUp(player, location, gLocation);
				
				e.setCancelled(true);
				return;
			}
			
		}
		

		if (generator != null && generator.getType().equals("single"))
		{
			if (generator.getChances().containsKey(block) || generator.getGeneratorBlock().equals(block) || block.equals(generator.getPlaceholder())) {
				
				if (checkIfPickingUp(player, location, gLocation) || block.equals(generator.getPlaceholder()) || !PerPlayerGenerators.canUse(player, gLocation)) {
					e.setCancelled(true);
					return;
				}
				
				if (block.equals(generator.getGeneratorBlock()) && !generator.getChances().containsKey(generator.getGeneratorBlock())) {
					return;
				}
			
				GenerateBlockFunction.generateBlock(location, generator, 1);
				return;
			}
		}
		
		if (KGenerators.dependencies.contains("WorldGuard") && !player.hasPermission("kgenerators.bypass.worldguard") && KGenerators.getWorldGuardUtils().worldGuardFlagCheck(location, player, EnumWGFlags.ONLY_GEN_BREAK))
		{
			LangUtils.sendMessage(player, Message.GeneratorsOnlyGenBreakHere);
			e.setCancelled(true);
			return;
		}
		
	}
	/* 
	 * Check if pick ups generator
	 * True cancells event
	 */
	boolean checkIfPickingUp(Player p, Location location, GeneratorLocation gLocation) {
		Generator generator = gLocation.getGenerator();
		if (!p.isSneaking()){
			if (generator.getType().equals("double")) {
				LangUtils.sendMessage(p, Message.GeneratorsCantBreak);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if (KGenerators.dependencies.contains("WorldGuard") && !p.hasPermission("kgenerators.bypass.worldguard") && !KGenerators.getWorldGuardUtils().worldGuardFlagCheck(location, p, EnumWGFlags.PICK_IP))
			{
				LangUtils.sendMessage(p, Message.GeneratorsCantPickUpHere);
				return true;
			}
			else if (!PerPlayerGenerators.canPickUp(p, gLocation))
			{
				return true;
			}
			else
			{
				GeneratorsManager.removeGenerator(gLocation, location, true);
				LangUtils.addReplecable("<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
				LangUtils.sendMessage(p, Message.GeneratorsPickedUp);
				return true;
			}
		}
	}
}
