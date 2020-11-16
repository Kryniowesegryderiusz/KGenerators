package me.kryniowesegryderiusz.KGenerators.Listeners;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.kryniowesegryderiusz.KGenerators.GenerateBlockFunction;
import me.kryniowesegryderiusz.KGenerators.Generator;
import me.kryniowesegryderiusz.KGenerators.GeneratorsFileManger;
import me.kryniowesegryderiusz.KGenerators.KGenerators;

public class onBlockPlaceEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void BlockPlaceEvent(final BlockPlaceEvent e){
		
		if (e.isCancelled()) {
			return;
		}
		
		//Pierwszy check, czy blok w ogole jest w generatorach
		if (!KGenerators.generatorsItemStacks.contains(KGenerators.getBlocksUtils().getItemStackByBlock(e.getBlockPlaced()))) {
			return;
		}
		
		//loop po dostepnych generatorach
		for (Entry<String, Generator> set : KGenerators.generators.entrySet()) {
		    String generatorID = set.getKey();
		    Generator generator = set.getValue();
		    if (generator.getGeneratorItem().getItemMeta().equals(e.getItemInHand().getItemMeta())) {
		    	Location location = e.getBlockPlaced().getLocation();
		    	KGenerators.generatorsLocations.put(location, generatorID);
		    	GeneratorsFileManger.saveGeneratorToFile(location, generatorID);
		    	
		    	Location locationAdjusted;
		    	
		    	if (generator.getType().equals("double")) {
		    		locationAdjusted = new Location(location.getWorld(),location.getX(),location.getY()+1,location.getZ());
		    		
		    	}
		    	else
		    	{
		    		locationAdjusted = location;
		    	}
		    	
		    	GenerateBlockFunction.generateBlock(locationAdjusted, generator, generator.getAfterPlaceWaitModifier());
		    	
		    	break;
		    }
		}
	}
}
