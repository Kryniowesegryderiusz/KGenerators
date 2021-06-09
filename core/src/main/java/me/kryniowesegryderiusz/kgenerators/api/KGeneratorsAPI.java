package me.kryniowesegryderiusz.kgenerators.api;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Settings;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.Upgrade;
import me.kryniowesegryderiusz.kgenerators.handlers.Place;
import me.kryniowesegryderiusz.kgenerators.handlers.Remove;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;

public class KGeneratorsAPI
{
	public KGeneratorsAPI() {}
	
	/**
	 * Get GeneratorLocation (Location, Generator, Owner) by location.
	 * 
	 * @param location Location
	 * @return GeneratorLocation or null if doesn't exist
	 */
	public static @Nullable GeneratorLocation getGenerator(Location location)
	{
		return Locations.get(location);
	}
	
	/**
	 * Places generator in world and generates block
	 * 
	 * @param id Generator id
	 * @param location Location of new generator
	 * @param owner Owner of generator
	 * @return false, if generator couldn't be placed
	 */
	public static boolean placeGenerator(String id, Location location, @Nullable Player owner)
	{
		return Place.place(location, Generators.get(id), owner);
	}
	
	/**
	 * Places generator in world and generates block
	 * Be careful, it doesn't check if anything is in location
	 * 
	 * @param id Generator id
	 * @param location Location of new generator
	 * @return false, if generator couldn't be placed
	 */
	public static boolean placeGenerator(String id, Location location)
	{
		return placeGenerator(id, location, null);
	}
	
	/**
	 * Removes generator with all required checks
	 * Just fire it
	 * 
	 * @param location Generator to remove location
	 * @param drop Determines if generator item should drop
	 */
	public static void removeGenerator(Location location, boolean drop)
	{
		Remove.removeGenerator(location, drop);
	}
	
	/**
	 * Removes generator with all required checks
	 * Just fire it
	 * 
	 * @param location Generator to remove location
	 * @param drop Determines if generator item should drop
	 */
	public static void removeGenerator(GeneratorLocation generatorLocation, boolean drop)
	{
		Remove.removeGenerator(generatorLocation, drop);
	}
	
	/**
	 * Removes generator with all required checks and gives it to player if pick-up-to-eq is enabled
	 * Just fire it
	 * 
	 * @param location Generator to remove location
	 * @param drop Determines if generator item should drop
	 */
	public static void removeGenerator(Location location, Player toWho)
	{
		Remove.removeGenerator(location, toWho);
	}
	
	/**
	 * Removes generator with all required checks and gives it to player if pick-up-to-eq is enabled
	 * Just fire it
	 * 
	 * @param location Generator to remove location
	 * @param drop Determines if generator item should drop
	 */
	public static void removeGenerator(GeneratorLocation generatorLocation, Player toWho)
	{
		Remove.removeGenerator(generatorLocation, toWho);
	}
	
	/**
	 * Regenerates generator block immediately;
	 * 
	 * @param location Generator location
	 */
	public static void regenerateForceNow(Location location)
	{
		Schedules.scheduleNow(Locations.get(location));
	}
	
	/**
	 * Regenerates generator block after delay;
	 * 
	 * @param location Generator location
	 */
	public static void regenerateSchedule(Location location)
	{
		Schedules.schedule(Locations.get(location));
	}
	
	/**
	 * Get upgrade by generatorId
	 * 
	 * @return Upgrade or null if generator doesnt have one
	 */
	public static Upgrade getUpgrade(String generatorId)
	{
		return Upgrades.getUpgrade(generatorId);
	}
	
	/**
	 * Get global settings
	 * 
	 * @return Settings interface
	 */
	public static Settings getGlobalSettings()
	{
		return Main.getSettings();
	}
	
}
