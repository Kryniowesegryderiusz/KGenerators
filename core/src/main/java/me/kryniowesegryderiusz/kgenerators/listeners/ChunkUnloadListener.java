package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class ChunkUnloadListener implements Listener {

	@EventHandler
	public void onChunkUnload(final ChunkUnloadEvent e) {

		try {

			if (Main.getDatabases().isMigratorRunning())
				return;

			Main.getPlacedGenerators().unloadChunk(e.getChunk());

		} catch (Exception exception) {
			Logger.error(exception);
		}

	}

}
