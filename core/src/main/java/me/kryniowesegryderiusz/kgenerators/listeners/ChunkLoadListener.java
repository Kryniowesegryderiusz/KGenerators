package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class ChunkLoadListener implements Listener {
	
	@EventHandler
	public void onChunkLoad(final ChunkLoadEvent e) {
		for (GeneratorLocation gl : Main.getDatabases().getDb().getGenerators(e.getChunk())) {
			Main.getPlacedGenerators().loadGenerator(gl);
		}
	}

}
