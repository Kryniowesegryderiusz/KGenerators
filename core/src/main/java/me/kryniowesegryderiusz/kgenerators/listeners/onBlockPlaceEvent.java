package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.handlers.Place;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;

public class onBlockPlaceEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void BlockPlaceEvent(final BlockPlaceEvent e){
		
		if (e.isCancelled()) {
			return;
		}
		
		if ((Locations.exists(e.getBlock().getLocation())
				&& placeBlockCancelEventCheck(e.getPlayer(), e.getItemInHand(), e.getBlock().getLocation()))
			|| (Locations.exists(e.getBlock().getLocation().clone().add(0,-1,0)) 
				&& Locations.get(e.getBlock().getLocation().clone().add(0,-1,0)).getGenerator().getType() == GeneratorType.DOUBLE
				&& placeBlockCancelEventCheck(e.getPlayer(), e.getItemInHand(), e.getBlock().getLocation().clone().add(0,-1,0)))
			)
		{
			e.setCancelled(true);
			return;
		}
		
		Generator generator = Generators.get(e.getItemInHand());
		if (generator != null)
		{
		    Player player = e.getPlayer();
	    	Location location = e.getBlockPlaced().getLocation();
	    	e.setCancelled(!Place.place(location, generator, player));
		}
		
		
	}
	
	private boolean placeBlockCancelEventCheck(Player p, ItemStack item, Location location)
	{
		Generator generator = Locations.get(location).getGenerator();
		
		if (generator.doesChancesContain(item))
		{
			return false;
		}

		Lang.sendMessage(p, EnumMessage.GeneratorsPlaceCantPlaceBlock);
		return true;
	}
}
