package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IGeneratorLocation {
	
	/**
	 * @return location, where generated object would appear
	 */
	public Location getGeneratedBlockLocation();
	
	/**
	 * Checks if block at location is physically possible to mine.
	 * Should be checked with every mining action
	 * @param location - Crucial for double generators. 
	 */
	public boolean isBlockPossibleToMine(Location location);
	
	/**
	 * Checks if player is permitted to mine this generator
	 * Should be checked with every player-related mining action
	 * @param player
	 * @return true if player is permitted
	 */
	public boolean isPermittedToMine(Player player);
	
	/**
	 * Checks whether this generatorLocation is corrupted.
	 * @return true if generator is not scheduled and there is nothing to mine
	 */
	public boolean isBroken();
	
	/**
	 * Instantly regenerates generator
	 */
	public void regenerateGenerator();
	
	/**
	 * Schedules generator regeneration based on generator settings
	 */
	public void scheduleGeneratorRegeneration();
	
	/**
	 * Removes generator from world and database
	 * @param drop - whether generator item should be spawned
	 * @param player - player removing generator
	 */
	public void removeGenerator(boolean drop, @Nullable Player player);
	
	/**
	 * Pick up generator
	 * @param player - player, who is picking up generator
	 */
	public void pickUpGenerator(Player player);
}
