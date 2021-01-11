package me.kryniowesegryderiusz.KGenerators.Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.kryniowesegryderiusz.KGenerators.Enums.EnumPickUpMode;
import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.PickUp;

public class onPlayerInteractEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void playerInteract(PlayerInteractEvent  e)
	{
		if(e.getHand() != EquipmentSlot.HAND) return;
		
		if (e.isCancelled()) return;
		
		Location location = e.getClickedBlock().getLocation();
		if (!Main.generatorsLocations.containsKey(location)) return;
		
		if (Main.pickUpMode == EnumPickUpMode.ANY_CLICK) e.setCancelled(PickUp.isPickingUpCheck(EnumPickUpMode.ANY_CLICK, e.getPlayer(), location, Main.generatorsLocations.get(location), e.getAction()));
		else if (e.getAction() == Action.LEFT_CLICK_BLOCK) e.setCancelled(PickUp.isPickingUpCheck(EnumPickUpMode.LEFT_CLICK, e.getPlayer(), location, Main.generatorsLocations.get(location)));
		else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) e.setCancelled(PickUp.isPickingUpCheck(EnumPickUpMode.RIGHT_CLICK, e.getPlayer(), location, Main.generatorsLocations.get(location)));
	}
}
