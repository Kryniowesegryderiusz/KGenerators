package me.kryniowesegryderiusz.KGenerators.Listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.GenerateBlockFunction;
import me.kryniowesegryderiusz.KGenerators.KGenerators;
import me.kryniowesegryderiusz.KGenerators.PerPlayerGenerators;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

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
			
			final GeneratorLocation gLocation = KGenerators.generatorsLocations.get(location);
			final GeneratorLocation bgLocation = KGenerators.generatorsLocations.get(bLocation);
			
			Generator generator = null;
			Generator bGenerator = null;
			if (gLocation != null){generator = gLocation.getGenerator();}
			if (bgLocation != null){bGenerator = bgLocation.getGenerator();}
			
			if (generator != null) {
				if (generator.getType().equals("single") && generator.isPistonPushAllowed()) {
					if (generator.getPlaceholder() == null || !generator.getPlaceholder().equals(KGenerators.getBlocksUtils().getItemStackByBlock(block))) {
						if (PerPlayerGenerators.canPush(generator))
						{
							GenerateBlockFunction.generateBlock(location, generator, 1);
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
				if (bGenerator.getType().equals("double") && bGenerator.isPistonPushAllowed())
				{
					if (bGenerator.getPlaceholder() == null || !bGenerator.getPlaceholder().equals(KGenerators.getBlocksUtils().getItemStackByBlock(block))) {
						if (PerPlayerGenerators.canPush(generator))
						{
							GenerateBlockFunction.generateBlock(location, generator, 1);
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
