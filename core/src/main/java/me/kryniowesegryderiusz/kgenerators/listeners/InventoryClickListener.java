package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.gui.ChancesMenu;
import me.kryniowesegryderiusz.kgenerators.gui.GeneratorMenu;
import me.kryniowesegryderiusz.kgenerators.gui.LimitsMenu;
import me.kryniowesegryderiusz.kgenerators.gui.MainMenu;
import me.kryniowesegryderiusz.kgenerators.gui.Menus;
import me.kryniowesegryderiusz.kgenerators.gui.RecipeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.UpgradeMenu;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.CustomMenuClickEvent;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.multiversion.MultiVersion;

public class InventoryClickListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void inventoryClickEvent(InventoryClickEvent e)
	{
		if (e.isCancelled())
			return;
		
		Player p = (Player) e.getWhoClicked();
		int slot = e.getSlot();
		
		/*
		 * Grindstone and anvil change generator item protection
		 */
		 if (((MultiVersion.isHigher(13) && e.getInventory() != null && e.getInventory().getType() == InventoryType.GRINDSTONE) || (e.getInventory() != null && e.getInventory().getType() == InventoryType.ANVIL)) 
				 && e.getCurrentItem() != null && Generators.get(e.getCurrentItem()) != null ) 
		 {
			 
			 Lang.getMessageStorage().send(e.getWhoClicked(), Message.GENERATOR_PROTECTION_CANT_CHANGE_GENERATOR_ITEM);
			 e.setCancelled(true);
			 Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					@Override
		            public void run() {
						p.closeInventory();
		            }
				});
		 }
		 
		 /*
		  * Menus
		  */
		 
		 if (Menus.getVieving(p) != null)
		 {
			 Enum<?> m = Menus.getVieving(p);
			 
			 try {
				 if (m == MenuInventoryType.GENERATOR)
					 GeneratorMenu.onClick(p, slot);
				 else if (m == MenuInventoryType.MAIN)
					 MainMenu.onClick(p, slot, e.getClick());
				 else if (m == MenuInventoryType.CHANCES)
					 ChancesMenu.onClick(p, slot);
				 else if (m == MenuInventoryType.LIMITS)
					 LimitsMenu.onClick(p, slot, e.getCurrentItem());
				 else if (m == MenuInventoryType.RECIPE)
					 RecipeMenu.onClick(p, slot);
				 else if (m == MenuInventoryType.UPGRADE)
					 UpgradeMenu.onClick(p, slot);
				 
				 Main.getInstance().getServer().getPluginManager().callEvent(new CustomMenuClickEvent(p, m, slot, e.getClick(), e.getCurrentItem()));
				 
			} catch (Exception ex) {
				Lang.getMessageStorage().send(p, Message.COMMANDS_MENU_ERROR);
				Logger.error("An error occured while using menus!");
				Logger.error(ex);
				Menus.closeInv(p);
			}
			e.setCancelled(true);
		 }
		 
		 
	}

}
