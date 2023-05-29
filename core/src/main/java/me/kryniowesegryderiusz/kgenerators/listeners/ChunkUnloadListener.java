package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.kryniowesegryderiusz.kgenerators.Main;

public class ChunkUnloadListener implements Listener {

	@EventHandler
	public void onChunkUnload(final ChunkUnloadEvent e) {
		
		if (Main.getDatabases().isMigratorRunning()) return;
		
		Main.getPlacedGenerators().unloadChunkAsync(e.getChunk());
		

	}

}
