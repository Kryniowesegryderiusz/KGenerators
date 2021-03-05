package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.kryniowesegryderiusz.kgenerators.Enums.EnumInteraction;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.ActionHandler;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.multiversion.MultiVersion;

public class onPlayerInteractEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void playerInteract(PlayerInteractEvent e)
	{
		if(!MultiVersion.isHigher(9) && e.getHand() != EquipmentSlot.HAND) return;
		
		if (e.isCancelled()) return;
		
		Location location = e.getClickedBlock().getLocation();
		Location upperLocation = location.clone().add(0,1,0);
				
		GeneratorLocation gLocation = null;
		
		if (Locations.exists(upperLocation)) gLocation = Locations.get(upperLocation);
		if (Locations.exists(location)) gLocation = Locations.get(location);
		
		if (gLocation == null) return;
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) e.setCancelled(ActionHandler.handler(EnumInteraction.LEFT_CLICK, gLocation, e.getPlayer()));
		else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) e.setCancelled(ActionHandler.handler(EnumInteraction.RIGHT_CLICK, gLocation, e.getPlayer()));
	}
}
