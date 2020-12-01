package me.kryniowesegryderiusz.KGenerators.Listeners;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.kryniowesegryderiusz.KGenerators.GenerateBlockFunction;
import me.kryniowesegryderiusz.KGenerators.GeneratorsFileManger;
import me.kryniowesegryderiusz.KGenerators.KGenerators;
import me.kryniowesegryderiusz.KGenerators.PerPlayerGenerators;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;

public class onBlockPlaceEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void BlockPlaceEvent(final BlockPlaceEvent e){
		
		if (e.isCancelled()) {
			return;
		}
		
		if (!KGenerators.generatorsItemStacks.contains(KGenerators.getBlocksUtils().getItemStackByBlock(e.getBlockPlaced()))) {
			return;
		}
		
		for (Entry<String, Generator> set : KGenerators.generators.entrySet()) {
		    String generatorID = set.getKey();
		    Generator generator = set.getValue();
		    Player player = e.getPlayer();
		    
		    if (generator.getGeneratorItem().getItemMeta().equals(e.getItemInHand().getItemMeta())) {
		    	
		    	if (!PerPlayerGenerators.canPlace(player, generatorID))
			    {
		    		e.setCancelled(true);
			    	return;
			    }
		    	
		    	Location location = e.getBlockPlaced().getLocation();
		    	
		    	KGenerators.generatorsLocations.put(location, new GeneratorLocation(generatorID, player));
		    	GeneratorsFileManger.saveGeneratorToFile(location, player, generatorID);
		    	PerPlayerGenerators.addGeneratorToPlayer(player, generatorID);
		    	
		    	
		    	Location locationAdjusted; //for double
		    	if (generator.getType().equals("double")) {locationAdjusted = location.clone().add(0,1,0);}
		    	else {locationAdjusted = location;}
		    	
		    	GenerateBlockFunction.generateBlock(locationAdjusted, generator, generator.getAfterPlaceWaitModifier());
		    	
		    	break;
		    }
		}
	}
}
