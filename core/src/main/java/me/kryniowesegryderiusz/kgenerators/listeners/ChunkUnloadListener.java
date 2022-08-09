package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class ChunkUnloadListener implements Listener {

	@EventHandler
	public void onChunkUnload(final ChunkUnloadEvent e) {
		Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			for (GeneratorLocation gl : Main.getPlacedGenerators().getLoaded(e.getChunk())) {
				Main.getPlacedGenerators().unloadGenerator(gl);
			}
		});
	}

}
