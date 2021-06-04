package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.multiversion.MultiVersion;

public class InventoryClickEventListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void inventoryClickEvent(InventoryClickEvent e)
	{
		 if (((MultiVersion.isHigher(13) && e.getInventory() != null && e.getInventory().getType() == InventoryType.GRINDSTONE) || (e.getInventory() != null && e.getInventory().getType() == InventoryType.ANVIL)) 
				 && e.getCurrentItem() != null && Generators.get(e.getCurrentItem()) != null ) {
			 
			 Lang.sendMessage(e.getWhoClicked(), Message.GENERATOR_PROTECTION_CANT_CHANGE_GENERATOR_ITEM);
			 e.setCancelled(true);
			 Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					@Override
		            public void run() {
						e.getWhoClicked().closeInventory();
		            }
				});
		 }
	}

}
