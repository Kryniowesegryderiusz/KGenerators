package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class ChunkLoadListener implements Listener {

	@EventHandler
	public void onChunkLoad(final ChunkLoadEvent e) {
		
		if (Main.getDatabases().isMigratorRunning()) return;
		
		Chunk c = e.getChunk();
		
		loadChunk(c, 0);
	}
	
	public static void loadChunk(Chunk c, int delay) {
		Main.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				
				ArrayList<GeneratorLocation> generators = Main.getDatabases().getDb().getGenerators(c);
				
				if (generators == null) {
					Logger.error("ChunkManagement: Cant load chunk " + c.getX() + " " + c.getZ() + "! Postponing for 5 seconds!");
					loadChunk(c, 5*20);
					return;
				}
				
				for (GeneratorLocation gl : generators) {
					Main.getPlacedGenerators().loadGenerator(gl);
				}
				Main.getPlacedGenerators().getLoadedChunksManager().addChunk(c);
			}
		}, delay);
	}

}
