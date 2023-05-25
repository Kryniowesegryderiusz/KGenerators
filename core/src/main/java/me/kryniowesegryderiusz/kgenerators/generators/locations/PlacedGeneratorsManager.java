package me.kryniowesegryderiusz.kgenerators.generators.locations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.GeneratorLoadEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.GeneratorUnloadEvent;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.listeners.ChunkLoadListener;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class PlacedGeneratorsManager {
	
	private ConcurrentHashMap<Chunk, ChunkGeneratorLocations> loadedGenerators = new ConcurrentHashMap<Chunk, ChunkGeneratorLocations>();
	
	/*
	 * Chunk management related methods
	 */
	
	public void loadGenerator(GeneratorLocation gl) {
		if (gl != null) {
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Loading generator: " + gl.toString());
			this.addLoaded(gl);
			Main.getSchedules().loadSchedule(gl);
			Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
				Main.getInstance().getServer().getPluginManager().callEvent(new GeneratorLoadEvent(gl));
			});
		}
	}
	
	public void unloadGenerator(GeneratorLocation gLocation) {
		this.unloadGenerator(gLocation, false);
	}
	
	public void unloadGenerator(GeneratorLocation gLocation, boolean serverStop) {
		
		Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Unloading generator: " + gLocation.toString() + " | serverStop: " + serverStop);
		
		Main.getDatabases().getDb().saveGenerator(gLocation);
		this.removeLoaded(gLocation);
		Main.getSchedules().unloadSchedule(gLocation);
		if (!serverStop)
			Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
				Main.getInstance().getServer().getPluginManager().callEvent(new GeneratorUnloadEvent(gLocation));
			});
	}
	
	
	public void loadChunk(Chunk c, int delay) {
		Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Loading chunk: " + c.toString());
		Main.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				
				ArrayList<GeneratorLocation> generators = Main.getDatabases().getDb().getGenerators(c);
				
				if (generators == null) {
					Logger.error("PlacedGeneratorsManager: Cant load chunk " + c.getX() + " " + c.getZ() + "! Trying again!");
					loadChunk(c, 1*20);
					return;
				}
				
				for (GeneratorLocation gl : generators) {
					Main.getPlacedGenerators().loadGenerator(gl);
				}
				
				loadedGenerators.putIfAbsent(c, new ChunkGeneratorLocations());
				loadedGenerators.get(c).setFullyLoaded(true);
				Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Chunk loaded: " + c.toString());

			}
		}, delay);
	}
	
	public void unloadChunk(Chunk c) {
		Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Unloading chunk: " + c.toString());
		ArrayList<GeneratorLocation> generatorsToUnload = this.getLoaded(c);
		
		Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			for (GeneratorLocation gl : generatorsToUnload) {
				Main.getPlacedGenerators().unloadGenerator(gl);
			}
			
			if (this.loadedGenerators.get(c) != null && this.loadedGenerators.get(c).isEmpty())
				this.loadedGenerators.remove(c);
			
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Chunk unloaded: " + c.toString() + " | isInManager: " + loadedGenerators.containsKey(c));
		});
	}
	
	public boolean isChunkFullyLoaded(Location loc) {
		boolean result = loadedGenerators.get(loc.getChunk()) != null && loadedGenerators.get(loc.getChunk()).isFullyLoaded();
		Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: isChunkFullyLoaded fired with " + result);
		return result;
	}
	
	public ArrayList<GeneratorLocation> getAll() {	
		ArrayList<GeneratorLocation> all = new ArrayList<GeneratorLocation>();
		for (ChunkGeneratorLocations c : this.loadedGenerators.values())
			all.addAll(c.getAll());
		return all;
	}
	
	/**
	 * Return generatorLocation, whether is loaded or not
	 * Loads generator if unloaded;
	 * @param location
	 * @return
	 */
	@Nullable public GeneratorLocation getUnknown(Location location) {
		GeneratorLocation gl = this.getLoaded(location);
		if (gl == null) {
			this.loadGenerator(Main.getDatabases().getDb().getGenerator(location));
			return this.getLoaded(location);
		}		
		return gl;
	}
	
	/*
	 * Manager related methods
	 */
	
	/**
	 * @return loaded GeneratorLocation related to location or null if none
	 */
	@Nullable public GeneratorLocation getLoaded(Location location) {
		ChunkGeneratorLocations cgl = loadedGenerators.get(location.getChunk());
		if (cgl != null)
			return cgl.get(location);
		else return null;
	}
	
	/**
	 * @return loaded GeneratorLocation list related to location or null if none
	 */
	public ArrayList<GeneratorLocation> getLoaded(Chunk c){
		if (this.loadedGenerators.containsKey(c))
			return this.loadedGenerators.get(c).getAll();
		else return new ArrayList<GeneratorLocation>();
	}
	
	/**
	 * @return whether GeneratorLocation related to location is loaded
	 */
	public boolean isLoaded(Location loc) {
		return this.getLoaded(loc) != null;
	}
	
	/**
	 * @return whether GeneratorLocation is loaded
	 */
	public boolean isLoaded(GeneratorLocation gLoc) {
		return isLoaded(gLoc.getLocation());
	}
	
	public void removeLoaded(GeneratorLocation gLocation) {
		if (loadedGenerators.containsKey(gLocation.getChunk()))
			loadedGenerators.get(gLocation.getChunk()).removeLocation(gLocation);
	}
	
	public int getLoadedGeneratorsAmount() {
		int amount = 0;
		for (ChunkGeneratorLocations chl : loadedGenerators.values()) {
			amount += chl.locations.size();
		}
		return amount;
	}
	
	/*
	 * Internal methods
	 */
	
	private void addLoaded(GeneratorLocation gLocation) {
		loadedGenerators.putIfAbsent(gLocation.getChunk(), new ChunkGeneratorLocations());
		loadedGenerators.get(gLocation.getChunk()).addLocation(gLocation);
	}
	
	/*
	 * Bulk removal
	 */
	
	public void bulkRemoveGenerators(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean dropGenerator) {
		Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				ArrayList<GeneratorLocation> generatorLocationsToRemove = Main.getDatabases().getDb().getGenerators(world, minX, minY, minZ, maxX, maxY, maxZ);
				Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (GeneratorLocation gloc : generatorLocationsToRemove) {
							Logger.info("Bulk generator removal: " + gloc.toString());
							gloc.removeGenerator(dropGenerator, null);
						}
					}
				});
			}
		});
	}
	
	/*
	 * Startup method
	 */
	
	/**
	 * On startup
	 */
	public void loadFromLoadedChunks() {

		Logger.debugPluginLoad("PlacedGenerators: Loading generators from already loaded chunks");

		HashMap<World, Chunk[]> loadedChunks = new HashMap<World, Chunk[]>();
		
		for (World w : Bukkit.getWorlds()) {
			loadedChunks.put(w, w.getLoadedChunks());
		}
		
		Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (Entry<World, Chunk[]> e : loadedChunks.entrySet()) {
					int amount = 0;
					int chunks = 0;
					for (Chunk c : e.getValue()) {
						loadChunk(c, 0);
						chunks++;
					}
					Logger.debugPluginLoad("PlacedGenerators: Loaded " + amount + " generators from world " + e.getKey().getName() + " (" + chunks + " chunks)");
				}
			}
		});
	}
	
	/*
	 * Converters
	 */
	
	public String locationToString(Location location) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world  = location.getWorld().getName();
		String string = world + "," + x + "," + y + "," + z;
		
		return string;
	}
	
	public Location stringToLocation (String string) {
		String[] parts = string.split(",");

        final World w = Bukkit.getServer().getWorld(parts[0]);
        final int x = Integer.parseInt(parts[1]);
        final int y = Integer.parseInt(parts[2]);
        final int z = Integer.parseInt(parts[3]);

        return new Location(w, x, y, z);
	}
	
	public Chunk stringToChunk (String string) {
		String[] parts = string.split(",");

        final World w = Bukkit.getServer().getWorld(parts[0]);
        final int x = Integer.parseInt(parts[1]);
        final int z = Integer.parseInt(parts[2]);

        return w.getChunkAt(x, z);
	}
	
	/*
	 * ChunkGeneratorLocations class
	 */

	public class ChunkGeneratorLocations {
		
		@Getter @Setter private boolean fullyLoaded = false;
		private ConcurrentHashMap<Location, GeneratorLocation> locations = new ConcurrentHashMap<Location, GeneratorLocation>();
		
		public void addLocation(GeneratorLocation gLocation) {
			this.locations.put(gLocation.getLocation(), gLocation);
		}
		
		public void removeLocation(GeneratorLocation gLocation) {
			if (this.locations.containsKey(gLocation.getLocation()))
				this.locations.remove(gLocation.getLocation());
		}
		
		public GeneratorLocation get(Location location) {
			if (location == null) return null;
			if (locations.containsKey(location))
				return locations.get(location);
			else if (locations.containsKey(location.clone().add(0,-1,0))
					&& locations.get(location.clone().add(0,-1,0)).getGenerator().getType() == GeneratorType.DOUBLE)
				return locations.get(location.clone().add(0,-1,0));
			else return null;
		}
		
		public ArrayList<GeneratorLocation> getAll() {
			ArrayList<GeneratorLocation> all = new ArrayList<GeneratorLocation>();
			all.addAll(this.locations.values());
			return all;
		}
		
		public boolean isEmpty() {
			return locations.isEmpty();
		}
	}
	
	@AllArgsConstructor
	public class ChunkInfo {
		@Getter int x;
		@Getter int z;
		
		public ChunkInfo(Chunk c) {
			this.x = c.getX();
			this.z = c.getZ();
		}
		
		@Override
		public boolean equals(Object obj) {
	        if(obj == null || obj.getClass()!= this.getClass())
	            return false;
	        
	        ChunkInfo ci = (ChunkInfo) obj;	        
			return x == ci.getX() && z == ci.getZ();
		}
		
		@Override
		public String toString() {
			return "ChunkInfo:"+x+"-"+z;
		}
	}

}

