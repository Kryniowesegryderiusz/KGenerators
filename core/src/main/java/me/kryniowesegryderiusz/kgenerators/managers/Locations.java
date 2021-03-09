package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.bukkit.Location;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;

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
}
