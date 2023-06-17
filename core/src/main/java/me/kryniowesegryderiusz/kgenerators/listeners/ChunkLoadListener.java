package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class ChunkLoadListener implements Listener {

	@EventHandler
	public void onChunkLoad(final ChunkLoadEvent e) {

		try {

			if (Main.getDatabases().isMigratorRunning())
				return;

			Main.getPlacedGenerators().loadChunk(e.getChunk());

		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
}
