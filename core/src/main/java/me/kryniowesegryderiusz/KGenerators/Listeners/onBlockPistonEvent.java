package me.kryniowesegryderiusz.KGenerators.Listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import me.kryniowesegryderiusz.KGenerators.Generator;
import me.kryniowesegryderiusz.KGenerators.KGenerators;

public class onBlockPistonEvent implements Listener {

	@EventHandler
	public void BlockPistonExtendEvent (final BlockPistonExtendEvent e){
		e.setCancelled(pistonEvent(e.getBlocks()));
	}
	
	@EventHandler
	public void BlockPistonRetractEvent (final BlockPistonRetractEvent e){
		e.setCancelled(pistonEvent(e.getBlocks()));
	}
	
	Boolean pistonEvent(List<Block> blocks) {
		for (Block block : blocks) {
			
			Location blockLocation = block.getLocation();
			Location belowBlockLocation = blockLocation.clone().add(0,-1,0);
			
			//generator w tym miejscu
			if (KGenerators.generatorsLocations.containsKey(blockLocation)) {
				return true;
			}
			
			//blok generowany przez generator double
			if (KGenerators.generatorsLocations.containsKey(belowBlockLocation)) {
				String generatorString = KGenerators.generatorsLocations.get(belowBlockLocation);
				Generator generator = KGenerators.generators.get(generatorString);
				
				if (generator.getType().equals("double")) {
					return true;
				}
			}
			
		}
		
		return false;
	}
}
