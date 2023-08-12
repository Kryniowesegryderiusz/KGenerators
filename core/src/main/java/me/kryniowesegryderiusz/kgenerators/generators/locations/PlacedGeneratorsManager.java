package me.kryniowesegryderiusz.kgenerators.generators.locations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
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
	
	@Getter private ConcurrentHashMap<ChunkInfo, ChunkGeneratorLocations> loadedGenerators = new ConcurrentHashMap<ChunkInfo, ChunkGeneratorLocations>();
	
	private BukkitTask managementTask;
	@Getter private LinkedBlockingQueue<ManagementTask> managementQueue = new LinkedBlockingQueue<ManagementTask>();
	
	private boolean stopping = false;
	
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
				if (stopping) break;
				ManagementTask r = managementQueue.poll();
				if (r != null) {
					try {
						r.doTask();
					} catch (Exception e) {
						Logger.error(e);
					}
				} else break;
			}
			if (!stopping)
				nextManagementTick();
		}, 1L);
	}
	
	public void onDisable() {
		stopping = true;
		managementTask.cancel();
		while(managementQueue.size() > 0) {
			ManagementTask r = managementQueue.poll();
			if (r != null) {
				r.doTask();
			} else break;
		}
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
		
		private ChunkInfo ci;
		private long taskScheduleTime = System.currentTimeMillis();
		
		public ChunkLoadTask(Chunk c) {
			this.ci = getChunkInfo(c);
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Scheduling chunk load: " + ci.toString());
		}

		@Override
		public void doTask() {
			long timeTaken = System.currentTimeMillis()-taskScheduleTime;
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Loading chunk: " + ci.toString() + " (After " + timeTaken + "ms)");
			if (timeTaken > 500) {
				Logger.debug("PlacedGeneratorsManager: Waiting for chunk load " + ci.toString() + " took more than 500ms! ("+timeTaken+"ms) Is the server overloaded?");
			}
			
			ArrayList<GeneratorLocation> generators = Main.getDatabases().getDb().getGenerators(ci);
			
			if (generators == null) {
				Logger.error("PlacedGeneratorsManager: Cant load chunk " + ci.toString() + "! Trying again!");
				managementQueue.add(this);
				return;
			}
			
			Main.getPlacedGenerators().getLoadedGenerators().putIfAbsent(ci, new ChunkGeneratorLocations());
			
			for (GeneratorLocation gl : generators) {
				
				Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Loading generator: " + gl.toString());

				Schedule finalSchedule = Main.getDatabases().getDb().getSchedule(gl);
				
				Main.getPlacedGenerators().addLoaded(gl);
				
				if (finalSchedule != null) {
					Logger.debugSchedulesManager("PlacedGeneratorsManager: Loading schedule " + gl.toString() + "| isNull: " + (finalSchedule == null));	
					Main.getSchedules().getSchedules().put(gl, finalSchedule);
					if (gl.getGenerator().isHologram())
						Main.getHolograms().createRemainingTimeHologram(gl);
					Main.getDatabases().getDb().removeSchedule(gl);
				} else {
					if (gl.isBroken()) {
						gl.scheduleGeneratorRegeneration();
						Logger.debug("PlacedGeneratorsManager: Broken generator found on chunk load. Automatically fixing it: " + gl.toString());
					}
				}
					
				Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
					Main.getInstance().getServer().getPluginManager().callEvent(new GeneratorLoadEvent(gl));
				});

			}
			
			loadedGenerators.get(ci).setFullyLoaded(true);
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Chunk loaded: " + ci.toString());
		}
		
	}
	
	public class ChunkUnloadTask extends ManagementTask {
		
		private ChunkInfo ci;
		private long taskScheduleTime = System.currentTimeMillis();
		
		public ChunkUnloadTask(Chunk c) {
			this.ci = getChunkInfo(c);
			
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Scheduling chunk unload: " + ci.toString());
		}

		@Override
		public void doTask() {
			long timeTaken = System.currentTimeMillis()-taskScheduleTime;
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Unloading chunk: " + ci.toString() + " (After " + timeTaken + "ms)");
			if (timeTaken > 500) {
				Logger.debug("PlacedGeneratorsManager: Waiting for chunk unload " + ci.toString() + " took more than 500ms! ("+timeTaken+"ms) Is the server overloaded?");
			}
			
			ArrayList<GeneratorLocation> generatorsToUnload = getLoaded(ci);
			
			for (GeneratorLocation gl : generatorsToUnload) {

				Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Unloading generator: " + gl.toString());
				
				/*
				 * Database
				 */
				
				Main.getDatabases().getDb().saveGenerator(gl);
				
				Schedule schedule = Main.getSchedules().getSchedule(gl);
				if (schedule != null) {
					Logger.debugSchedulesManager("PlacedGeneratorsManager: Unloading schedule " + gl.toString());	
					Main.getDatabases().getDb().addSchedule(gl, schedule);
				}
				
				/*
				 * Game
				 */

				removeLoaded(gl);

				if (schedule != null)
					Main.getSchedules().remove(gl);
					
				Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
					Main.getInstance().getServer().getPluginManager().callEvent(new GeneratorUnloadEvent(gl));
				});
				
			}

			if (loadedGenerators.get(ci) != null && loadedGenerators.get(ci).isEmpty())
				loadedGenerators.remove(ci);
				
			Logger.debugPlacedGeneratorsManager("PlacedGeneratorsManager: Chunk unloaded: " + ci.toString() + " | isInManager: " + loadedGenerators.containsKey(ci));

		}
	}
	
	/**
	 * This will load chunk
	 * @param loc
	 * @return
	 */
	public boolean isChunkFullyLoaded(Location loc) {
		boolean result = loadedGenerators.get(getChunkInfo(loc.getChunk())) != null && loadedGenerators.get(getChunkInfo(loc.getChunk())).isFullyLoaded();
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
		if (loadedGenerators.containsKey(gLocation.getChunkInfo()))
			loadedGenerators.get(gLocation.getChunkInfo()).removeLocation(gLocation);
	}
	
	public void addLoaded(GeneratorLocation gLocation) {
		loadedGenerators.putIfAbsent(gLocation.getChunkInfo(), new ChunkGeneratorLocations());
		loadedGenerators.get(gLocation.getChunkInfo()).addLocation(gLocation);
	}
	
	/*
	 * Manager related methods
	 */
	
	/**
	 * This will load chunk if its not loaded
	 * @return loaded GeneratorLocation related to location or null if none
	 */
	@Nullable public GeneratorLocation getLoaded(Location location) {
		ChunkGeneratorLocations cgl = loadedGenerators.get(this.getChunkInfo(location.getChunk()));
		if (cgl != null)
			return cgl.get(location);
		else return null;
	}
	
	/**
	 * @return loaded GeneratorLocation list related to location or null if none
	 */
	public ArrayList<GeneratorLocation> getLoaded(ChunkInfo ci) {
		ChunkGeneratorLocations cgl = this.loadedGenerators.get(ci);
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
	
	public ChunkInfo stringToChunkInfo (String string) {
		String[] parts = string.split(",");

        final World w = Bukkit.getServer().getWorld(parts[0]);
        final int x = Integer.parseInt(parts[1]);
        final int z = Integer.parseInt(parts[2]);

        return new ChunkInfo(w, x, z);
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
	
	public ChunkInfo getChunkInfo(Chunk c) {
		ChunkInfo ci = new ChunkInfo(c);
		/*
		for (ChunkInfo cii : this.loadedGenerators.keySet()) {
			if (ci.equals(cii)) {
				ci = cii;
				break;
			}
		}*/
		return ci;
	}
	
	@AllArgsConstructor
	public class ChunkInfo {
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(world, x, z);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ChunkInfo other = (ChunkInfo) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return Objects.equals(world, other.world) && x == other.x && z == other.z;
		}

		@Getter World world;
		@Getter int x;
		@Getter int z;
		
		public ChunkInfo(Chunk c) {
			this.x = c.getX();
			this.z = c.getZ();
			this.world = c.getWorld();
		}
		
		@Override
		public String toString() {
			return "ChunkInfo: "+world.getName()+", "+x+", "+z;
		}

		private PlacedGeneratorsManager getEnclosingInstance() {
			return PlacedGeneratorsManager.this;
		}
	}

}

