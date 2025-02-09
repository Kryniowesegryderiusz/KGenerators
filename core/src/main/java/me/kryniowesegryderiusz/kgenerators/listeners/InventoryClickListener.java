package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.addons.events.CustomMenuClickEvent;
import me.kryniowesegryderiusz.kgenerators.gui.menus.GeneratedObjectsMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.GeneratorMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.LimitsMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.MainMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.RecipeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.menus.UpgradeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuPlayer;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class InventoryClickListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent e) {

		try {

			if (e.isCancelled())
				return;

			Player p = (Player) e.getWhoClicked();
			int slot = e.getSlot();

			/*
			 * Grindstone and anvil change generator item protection
			 */
			if (((Main.getMultiVersion().isHigher(13) && e.getInventory() != null
					&& e.getInventory().getType() == InventoryType.GRINDSTONE)
					|| (e.getInventory() != null && e.getInventory().getType() == InventoryType.ANVIL))
					&& e.getCurrentItem() != null && Main.getGenerators().get(e.getCurrentItem()) != null) {

				Lang.getMessageStorage().send(e.getWhoClicked(),
						Message.GENERATOR_PROTECTION_CANT_CHANGE_GENERATOR_ITEM);
				e.setCancelled(true);
				Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(),
						new Runnable() {
							@Override
							public void run() {
								p.closeInventory();
							}
						});
			}

			/*
			 * Menus
			 */

			if (Main.getMenus().getVieving(p) != null) {
				Enum<?> m = Main.getMenus().getVieving(p);

				try {
					if (m == MenuInventoryType.GENERATOR)
						GeneratorMenu.onClick(p, slot);
					else if (m == MenuInventoryType.MAIN)
						MainMenu.onClick(p, slot, e.getClick());
					else if (m == MenuInventoryType.GENERATED_OBJECTS)
						GeneratedObjectsMenu.onClick(p, slot);
					else if (m == MenuInventoryType.LIMITS)
						LimitsMenu.onClick(p, slot, e.getCurrentItem());
					else if (m == MenuInventoryType.RECIPE)
						RecipeMenu.onClick(p, slot);
					else if (m == MenuInventoryType.UPGRADE)
						UpgradeMenu.onClick(p, slot);

					Main.getInstance().getServer().getPluginManager()
							.callEvent(new CustomMenuClickEvent(p, m, slot, e.getClick(), e.getCurrentItem()));

				} catch (Exception ex) {
					Lang.getMessageStorage().send(p, Message.COMMANDS_MENU_ERROR);
					Logger.error("An error occured while using menus!");
					Logger.error(ex);
					Main.getMenus().closeInv(p);
				}
				e.setCancelled(true);
			}

		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
	
	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent e) {
		
		MenuPlayer mp =  Main.getMenus().getMenuPlayer((Player) e.getPlayer());
		if (mp != null && mp.getInventory() == e.getInventory()) {
			Main.getMenus().closeInv((Player) e.getPlayer());
		}

	}

}
