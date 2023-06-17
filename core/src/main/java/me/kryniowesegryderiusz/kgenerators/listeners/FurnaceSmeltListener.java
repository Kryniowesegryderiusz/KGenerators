package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class FurnaceSmeltListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFurnaceSmelt(final FurnaceSmeltEvent e) {
		try {
			if (Main.getGenerators().get(e.getSource()) != null) {
				e.setCancelled(true);
			}
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
}
