package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.InteractionType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class PlayerInteractListener implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if(Main.getMultiVersion().isHigher(9) && e.getHand() != EquipmentSlot.HAND) return;
		
		if (e.isCancelled() || e.getClickedBlock() == null) return;
		
		Location location = e.getClickedBlock().getLocation();
		Location upperLocation = location.clone().add(0,1,0);
				
		GeneratorLocation gLocation = null;
		
		if (Main.getLocations().get(upperLocation) != null) gLocation = Main.getLocations().get(upperLocation);
		if (Main.getLocations().get(location) != null) gLocation = Main.getLocations().get(location);
		
		if (gLocation == null) return;
		
		if (e.getAction() == Action.LEFT_CLICK_BLOCK) e.setCancelled(gLocation.handleAction(InteractionType.LEFT_CLICK, e.getPlayer()));
		else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			gLocation.handleAction(InteractionType.RIGHT_CLICK, e.getPlayer());
			e.setCancelled(true);
		}
	}
}
