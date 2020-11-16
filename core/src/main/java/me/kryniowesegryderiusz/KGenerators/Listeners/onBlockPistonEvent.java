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
import me.kryniowesegryderiusz.KGenerators.Generator;
import me.kryniowesegryderiusz.KGenerators.KGenerators;
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
			
			Generator generator = KGenerators.generators.get(KGenerators.generatorsLocations.get(location));
			Generator bGenerator = KGenerators.generators.get(KGenerators.generatorsLocations.get(bLocation));
			
			//generator w tym miejscu
			if (generator != null) {
				if (generator.getType().equals("single") && generator.isPistonPushAllowed()) {
					if (generator.getPlaceholder() == null || !generator.getPlaceholder().equals(KGenerators.getBlocksUtils().getItemStackByBlock(block))) {
						GenerateBlockFunction.generateBlock(location, generator, 1);
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
			
			//blok generowany przez generator double
			if (bGenerator != null) {
				if (bGenerator.getType().equals("double") && bGenerator.isPistonPushAllowed())
				{
					if (bGenerator.getPlaceholder() == null || !bGenerator.getPlaceholder().equals(KGenerators.getBlocksUtils().getItemStackByBlock(block))) {
						GenerateBlockFunction.generateBlock(location, bGenerator, 1);
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
