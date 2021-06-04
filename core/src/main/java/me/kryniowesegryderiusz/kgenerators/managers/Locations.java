package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.handlers.Remove;

public class Locations {
	
	private static HashMap<Location, GeneratorLocation> locations = new HashMap<Location, GeneratorLocation>();
	
	public static boolean exists(Location location)
	{
		if (locations.containsKey(location)) return true;
		return false;
	}
	
	@Nullable
	public static GeneratorLocation get(Location location)
	{
		if (exists(location)) return locations.get(location);
		else return null;
	}
	
	public static void add(String id, Location location, GeneratorPlayer owner)
	{
		locations.put(location, new GeneratorLocation(id, location, owner));
	}
	
	public static void add(GeneratorLocation gLocation)
	{
		locations.put(gLocation.getLocation(), gLocation);
	}
	
	public static void remove(Location location)
	{
		if (exists(location)) locations.remove(location);
	}

	public static void clear() {
		locations.clear();
	}
	
	public static Set<Entry<Location, GeneratorLocation>> getEntrySet()
	{
		return locations.entrySet();
	}
	
	/*
	 * Bulk updates
	 */
	
	public static void bulkRemoveGenerators(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean dropGenerator)
	{
		Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				ArrayList<GeneratorLocation> generatorLocationsToRemove = new ArrayList<GeneratorLocation>();
				for (Entry<Location, GeneratorLocation> e : locations.entrySet())
				{
					Location l = e.getKey();
					if (l.getWorld() == world
							&& l.getX() >= minX && l.getX() <= maxX
							&& l.getY() >= minY && l.getY() <= maxY
							&& l.getX() >= minZ && l.getZ() <= maxZ)
					{
						generatorLocationsToRemove.add(e.getValue());
					}
				}
				Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (GeneratorLocation gloc : generatorLocationsToRemove)
						{
							Logger.info("Bulk generator removal: " + gloc.toString());
							Remove.removeGenerator(gloc, dropGenerator);
						}
					}
				});
			}
		});
	}
	
	/*
	 * Converters
	 */
	
	public static String locationToString(Location location)
	{
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world  = location.getWorld().getName();
		String string = world + "," + x + "," + y + "," + z;
		
		return string;
	}
	
	public static Location stringToLocation (String string)
	{
		String[] parts = string.split(",");

        final World w = Bukkit.getServer().getWorld(parts[0]);
        final int x = Integer.parseInt(parts[1]);
        final int y = Integer.parseInt(parts[2]);
        final int z = Integer.parseInt(parts[3]);

        return new Location(w, x, y, z);
	}
}
