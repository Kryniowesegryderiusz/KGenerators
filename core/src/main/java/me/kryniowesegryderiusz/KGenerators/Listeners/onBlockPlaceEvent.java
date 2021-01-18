package me.kryniowesegryderiusz.KGenerators.Listeners;

import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.KGenerators.Files;
import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.GenerateBlock;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.PerPlayerGenerators;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;

public class onBlockPlaceEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void BlockPlaceEvent(final BlockPlaceEvent e){
		
		if (e.isCancelled()) {
			return;
		}
		
		if ((Main.generatorsLocations.containsKey(e.getBlock().getLocation())
				&& placeBlockCancelEventCheck(e.getPlayer(), e.getItemInHand(), e.getBlock().getLocation()))
			|| (Main.generatorsLocations.containsKey(e.getBlock().getLocation().clone().add(0,-1,0)) 
				&& Main.generatorsLocations.get(e.getBlock().getLocation().clone().add(0,-1,0)).getGenerator().getType().equals("double")
				&& placeBlockCancelEventCheck(e.getPlayer(), e.getItemInHand(), e.getBlock().getLocation().clone().add(0,-1,0)))
			)
		{
			e.setCancelled(true);
			return;
		}
		
		if (Main.generatorsItemStacks.contains(Main.getBlocksUtils().getItemStackByBlock(e.getBlockPlaced()))) {
			for (Entry<String, Generator> set : Main.generators.entrySet()) {
			    String generatorID = set.getKey();
			    Generator generator = set.getValue();
			    Player player = e.getPlayer();
			    
			    if (generator.getGeneratorItem().getItemMeta().equals(e.getItemInHand().getItemMeta())) {
			    	
			    	Location location = e.getBlockPlaced().getLocation();
			    	Location aLocation = location.clone().add(0,1,0);
			    	
			    	if (!PerPlayerGenerators.canPlace(player, generatorID))
				    {
			    		e.setCancelled(true);
				    	return;
				    }
			    	
			    	if (generator.getType().equals("double") && !Main.getBlocksUtils().isAir(aLocation.getBlock()))
			    	{
			    		LangUtils.sendMessage(player, EnumMessage.GeneratorsPlaceDoubleBelowBlock);
			    		e.setCancelled(true);
						return;
			    	}
			    	
			    	if (generator.getType().equals("double") && Main.generatorsLocations.containsKey(aLocation))
			    	{
			    		LangUtils.sendMessage(player, EnumMessage.GeneratorsPlaceDoubleBelowGenerator);
			    		e.setCancelled(true);
						return;
			    	}
			    	
			    	Main.generatorsLocations.put(location, new GeneratorLocation(generatorID, player));
			    	Files.saveGeneratorToFile(location, player, generatorID);
			    	PerPlayerGenerators.addGeneratorToPlayer(player, generatorID);
			    	
			    	Location locationAdjusted; //for double
			    	if (generator.getType().equals("double")) {locationAdjusted = location.clone().add(0,1,0);}
			    	else {locationAdjusted = location;}
			    	
			    	if (generator.getAfterPlaceWaitModifier() == 0)
			    	{
			    		GenerateBlock.now(locationAdjusted, generator);
			    	}
			    	else
			    	{
			    		GenerateBlock.schedule(locationAdjusted, generator);
			    	}
			    	break;
			    }
			}
		}
		
		
	}
	
	private boolean placeBlockCancelEventCheck(Player p, ItemStack item, Location location)
	{
		Generator generator = Main.generatorsLocations.get(location).getGenerator();
		
		if (generator.doesChancesContain(item))
		{
			return false;
		}

		LangUtils.sendMessage(p, EnumMessage.GeneratorsPlaceCantPlaceBlock);
		return true;
	}
}
