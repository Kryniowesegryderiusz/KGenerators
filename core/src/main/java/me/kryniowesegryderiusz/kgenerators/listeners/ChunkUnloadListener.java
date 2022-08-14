package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class ChunkUnloadListener implements Listener {

	@EventHandler
	public void onChunkUnload(final ChunkUnloadEvent e) {
		
		if (Main.getDatabases().isMigratorRunning()) return;
		
		Chunk c = e.getChunk();
		
		ArrayList<GeneratorLocation> generatorsToUnload = Main.getPlacedGenerators().getLoaded(c);
		
		Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (GeneratorLocation gl : generatorsToUnload) {
					Main.getPlacedGenerators().unloadGenerator(gl);
				}
			}
		});
		

	}

}
