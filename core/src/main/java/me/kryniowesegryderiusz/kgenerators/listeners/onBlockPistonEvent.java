package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.GenerateBlock;
import me.kryniowesegryderiusz.kgenerators.handlers.PerPlayerGenerators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class onBlockPistonEvent implements Listener {

	@EventHandler
	public void BlockPistonExtendEvent (final BlockPistonExtendEvent e){
		e.setCancelled(pistonEvent(e.getBlocks()));
	}
	
	@EventHandler
	public void BlockPistonRetractEvent (final BlockPistonRetractEvent e){
		e.setCancelled(pistonEvent(e.getBlocks()));
	}
	
	/* Returns true if cancelled */
	Boolean pistonEvent(List<Block> blocks) {
		for (Block block : blocks) {
			
			Location location = block.getLocation();
			Location bLocation = location.clone().add(0,-1,0);
			
			final GeneratorLocation gLocation = Locations.get(location);
			final GeneratorLocation bgLocation = Locations.get(bLocation);
			
			Generator generator = null;
			Generator bGenerator = null;
			if (gLocation != null){generator = gLocation.getGenerator();}
			if (bgLocation != null){bGenerator = bgLocation.getGenerator();}
			
			if (generator != null) {
				if (generator.getType() == GeneratorType.SINGLE && generator.isAllowPistonPush()) {
					if (generator.getPlaceholder() == null || !generator.getPlaceholder().equals(Main.getBlocksUtils().getItemStackByBlock(block))) {
						if (PerPlayerGenerators.canPush(generator))
						{
							Schedules.schedule(gLocation);
						}
					}
					else
					{
						return true;
					}
				}
				else
				{
					return true;
				}
			}
			
			if (bGenerator != null) {
				if (bGenerator.getType() == GeneratorType.DOUBLE && bGenerator.isAllowPistonPush())
				{
					if (bGenerator.getPlaceholder() == null || !bGenerator.getPlaceholder().equals(Main.getBlocksUtils().getItemStackByBlock(block))) {
						if (PerPlayerGenerators.canPush(bGenerator))
						{
							Schedules.schedule(bgLocation);
						}
					}
					else
					{
						return true;
					}
				}
				else
				{
					return true;
				}
			}
		}
		return false;
	}
}
