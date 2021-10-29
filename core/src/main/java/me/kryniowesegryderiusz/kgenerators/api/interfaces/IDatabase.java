package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import java.sql.Connection;

import javax.annotation.Nullable;

import org.bukkit.Location;

import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;

public interface IDatabase {
	
	/**
	 * Gets database connection
	 * @return connection or null if not SQL
	 */
	@Nullable public Connection getConnection();
	
	/**
	 * Closes database connection
	 */
	public void closeConnection();
	
	/**
	 * Loads generators from database
	 * **WARNING!** This method should be run ONLY ON SERVER STARTUP
	 */
	public void loadGenerators();
	
	/**
	 * Saves generator location info to database
	 * @param GeneratorLocation
	 */
	public void savePlacedGenerator(GeneratorLocation gl);
	
	/**
	 * Removes location from database
	 * @param Location
	 */
	public void removePlacedGenerator(Location l);
}
