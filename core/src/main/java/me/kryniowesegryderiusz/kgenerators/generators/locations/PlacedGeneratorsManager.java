package me.kryniowesegryderiusz.kgenerators.generators.locations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.GeneratorLoadEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.GeneratorUnloadEvent;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;
import me.kryniowesegryderiusz.kgenerators.listeners.ChunkLoadListener;
import me.kryniowesegryderiusz.kgenerators.listeners.ChunkUnloadListener;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class PlacedGeneratorsManager {
	
	@Getter private HashMap<Chunk, ChunkGeneratorLocations> loadedGenerators = new HashMap<Chunk, ChunkGeneratorLocations>();
	
	private BukkitTask managementTask;
	@Getter private LinkedBlockingQueue<ManagementTask> managementQueue = new LinkedBlockingQueue<ManagementTask>();
	
	public PlacedGeneratorsManager() {
		
		Logger.debugPluginLoad("PlacedGenerators: Starting chunk management task");
		
		nextManagementTick();
	
		Logger.debugPluginLoad("PlacedGenerators: Loading generators from already loaded chunks");

		HashMap<World, Chunk[]> loadedChunks = new HashMap<World, Chunk[]>();
		
		Main.getInstance().getServer().getPluginManager().registerEvents(new ChunkLoadListener(), Main.getInstance());
		Main.getInstance().getServer().getPluginManager().registerEvents(new ChunkUnloadListener(), Main.getInstance());
		
		for (World w : Bukkit.getWorlds()) {
			loadedChunks.put(w, w.getLoadedChunks());
		}
					
		for (Entry<World, Chunk[]> e : loadedChunks.entrySet()) {
			int amount = 0;
			int chunks = 0;
			for (Chunk c : e.getValue()) {
				loadChunk(c);
				chunks++;
			}
			Logger.debugPluginLoad("PlacedGenerators: Loaded " + amount + " generators from world " + e.getKey().getName() + " (" + chunks + " chunks)");
		}

	}
	
	private void nextManagementTick() {
		managementTask = Main.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			while(managementQueue.size() > 0) {
				ManagementTask r = managementQueue.poll();
				if (r != null) {
					r.doTask();
				} else break;
			}
			nextManagementTick();
		}, 5L);
	}
	
	public void onDisable() {
		managementTask.cancel();
		Main.getSchedules().unloadAllSchedules();
		for (GeneratorLocation gl : this.getAll()) {
			Main.getDatabases().getDb().saveGenerator(gl);
		}
	}
	
	public void loadChunk(Chunk c) {
		managementQueue.add(new ChunkLoadTask(c));
	}
	
	public void unloadChunk(Chunk c) {
		managementQueue.add(new ChunkUnloadTask(c));
	}
	

	public abstract class ManagementTask {
		public abstract void doTask();
	}
	
	public class ChunkLoadTask extends ManagementTask {
		
		private Chunk c;
		
		public ChunkLoadTask(Chunk c) {
			this.c = c;
		}

		@Override
		public void doTask() {
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Loading chunk: " + c.getWorld().getName() + " " + c.toString());
			
			ArrayList<GeneratorLocation> generators = Main.getDatabases().getDb().getGenerators(c);
			
			if (generators == null) {
				Logger.error("PlacedGeneratorsManager: Cant load chunk " + c.getWorld().getName() + " " + c.getX() + " " + c.getZ() + "! Trying again!");
				managementQueue.add(this);
				return;
			}
			
			Main.getPlacedGenerators().getLoadedGenerators().putIfAbsent(c, new ChunkGeneratorLocations());
			
			for (GeneratorLocation gl : generators) {
				
				Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Loading generator: " + gl.toString());

				Schedule schedule = Main.getDatabases().getDb().getSchedule(gl);
				
				CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
				
				Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {

					Main.getPlacedGenerators().addLoaded(gl);
					
					if (schedule != null) {
						Logger.debugSchedulesManager("PlacedGeneratorsManager: Loading schedule " + gl.toString() + "| isNull: " + (schedule == null));					
						if (gl.getGenerator().isHologram())
							Main.getHolograms().createRemainingTimeHologram(gl);
						Main.getSchedules().getSchedules().put(gl, schedule);
					}
					
					Main.getInstance().getServer().getPluginManager().callEvent(new GeneratorLoadEvent(gl));
					future.complete(true);
				});
				
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					Logger.error("PlacedGeneratorsManager: Load: " + gl.toString() + " CompleteFuture interrupted");
					Logger.error(c);
				}
				
				if (schedule != null) Main.getDatabases().getDb().removeSchedule(gl);
			}
			
			loadedGenerators.get(c).setFullyLoaded(true);
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Chunk loaded: " + c.getWorld().getName() + " " + c.toString());
		}
		
	}
	
	public class ChunkUnloadTask extends ManagementTask {
		
		private Chunk c;
		
		public ChunkUnloadTask(Chunk c) {
			this.c = c;
		}

		@Override
		public void doTask() {
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Unloading chunk: " + c.getWorld().getName() + " " + c.toString());
			ArrayList<GeneratorLocation> generatorsToUnload = getLoaded(c);
			
			for (GeneratorLocation gl : generatorsToUnload) {
				
				Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Unloading generator: " + gl.toString());
				
				Main.getDatabases().getDb().saveGenerator(gl);
				
				Schedule schedule = Main.getSchedules().getSchedule(gl);
				if (schedule != null) {
					Logger.debugSchedulesManager("PlacedGeneratorsManager: Unloading schedule " + gl.toString());	
					Main.getDatabases().getDb().addSchedule(gl, schedule);
				}
				
				CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
				
				Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
					
					removeLoaded(gl);

					if (schedule != null)
						Main.getSchedules().remove(gl);
					
					Main.getInstance().getServer().getPluginManager().callEvent(new GeneratorUnloadEvent(gl));
					future.complete(true);
				});
				
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					Logger.error("PlacedGeneratorsManager: Unload: " + gl.toString() + " CompleteFuture interrupted");
					Logger.error(c);
				}
				
			}
			
			if (loadedGenerators.get(c) != null && loadedGenerators.get(c).isEmpty())
				loadedGenerators.remove(c);
			
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Chunk unloaded: " + c.getWorld().getName() + " " + c.toString() + " | isInManager: " + loadedGenerators.containsKey(c));

		}
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
	
	public void removeLoaded(GeneratorLocation gLocation) {
		if (loadedGenerators.containsKey(gLocation.getChunk()))
			loadedGenerators.get(gLocation.getChunk()).removeLocation(gLocation);
	}
	
	public void addLoaded(GeneratorLocation gLocation) {
		loadedGenerators.putIfAbsent(gLocation.getChunk(), new ChunkGeneratorLocations());
		loadedGenerators.get(gLocation.getChunk()).addLocation(gLocation);
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
	public ArrayList<GeneratorLocation> getLoaded(Chunk c) {
		ChunkGeneratorLocations cgl = this.loadedGenerators.get(c);
		if (cgl != null) {
			return cgl.getAll();
		}
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
	
	public int getLoadedGeneratorsAmount() {
		int amount = 0;
		for (ChunkGeneratorLocations chl : loadedGenerators.values()) {
			amount += chl.locations.size();
		}
		return amount;
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
		private HashMap<Location, GeneratorLocation> locations = new HashMap<Location, GeneratorLocation>();
		
		public void addLocation(GeneratorLocation gLocation) {
			this.locations.put(gLocation.getLocation(), gLocation);
		}
		
		public void removeLocation(GeneratorLocation gLocation) {
			if (this.locations.containsKey(gLocation.getLocation()))
				this.locations.remove(gLocation.getLocation());
		}
		
		public GeneratorLocation get(Location location) {
			
			if (location == null) return null;
			
			GeneratorLocation gl = locations.get(location);
			if (gl != null)
				return gl;
			
			gl = locations.get(location.clone().add(0,-1,0));
			if (gl != null && gl.getGenerator().getType() == GeneratorType.DOUBLE)
				return gl;
				
			return null;
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

