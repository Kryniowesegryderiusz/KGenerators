package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

import me.kryniowesegryderiusz.kgenerators.Main;

public class LeavesDecayListener implements Listener {
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		if (Main.getPlacedGenerators().isLoaded(e.getBlock().getLocation()))
			e.setCancelled(true);
	}

}
