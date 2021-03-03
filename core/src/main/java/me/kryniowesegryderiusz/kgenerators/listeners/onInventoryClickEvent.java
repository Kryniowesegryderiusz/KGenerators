package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.multiversion.MultiVersion;

public class onInventoryClickEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void inventoryClickEvent(InventoryClickEvent e)
	{
		 if (((MultiVersion.isHigher(13) && e.getInventory() != null && e.getInventory().getType() == InventoryType.GRINDSTONE) || (e.getInventory() != null && e.getInventory().getType() == InventoryType.ANVIL)) 
				 && Generators.get(e.getCurrentItem()) != null ) {
			 
			 Lang.sendMessage(e.getWhoClicked(), EnumMessage.GeneratorsProtectionCantChangeGeneratorItem);
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
