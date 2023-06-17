package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class LeavesDecayListener implements Listener {

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		try {
			if (Main.getPlacedGenerators().isLoaded(e.getBlock().getLocation()))
				e.setCancelled(true);
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
}
