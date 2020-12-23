package me.kryniowesegryderiusz.KGenerators.Listeners;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.kryniowesegryderiusz.KGenerators.Files;
import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.GenerateBlock;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.PerPlayerGenerators;

public class onBlockPlaceEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void BlockPlaceEvent(final BlockPlaceEvent e){
		
		if (e.isCancelled()) {
			return;
		}
		
		if (!Main.generatorsItemStacks.contains(Main.getBlocksUtils().getItemStackByBlock(e.getBlockPlaced()))) {
			return;
		}
		
		for (Entry<String, Generator> set : Main.generators.entrySet()) {
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
		    	
		    	Main.generatorsLocations.put(location, new GeneratorLocation(generatorID, player));
		    	Files.saveGeneratorToFile(location, player, generatorID);
		    	PerPlayerGenerators.addGeneratorToPlayer(player, generatorID);
		    	
		    	Location locationAdjusted; //for double
		    	if (generator.getType().equals("double")) {locationAdjusted = location.clone().add(0,1,0);}
		    	else {locationAdjusted = location;}
		    	
		    	GenerateBlock.generateBlock(locationAdjusted, generator, generator.getAfterPlaceWaitModifier());
		    	
		    	break;
		    }
		}
	}
}
