package me.kryniowesegryderiusz.kgenerators.data.databases;

import java.sql.Connection;
import java.util.ArrayList;

import javax.annotation.Nullable;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;

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
	
	public void updateTable();
	
	/*
	 * Placed generators table
	 */
	
	/**
	 * Gets saved generators according to owner
	 */
	@Nullable public GeneratorLocation getGenerator(Location location);
	
	/**
	 * Gets all saved generators
	 */
	public ArrayList<GeneratorLocation> getGenerators();
	
	/**
	 * Gets saved generators according to ranges
	 */
	public ArrayList<GeneratorLocation> getGenerators(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
	
	/**
	 * Gets saved generators according to owner
	 */
	public ArrayList<GeneratorLocation> getGenerators(String owner);
	
	/**
	 * Gets saved generators according to owner
	 */
	public ArrayList<GeneratorLocation> getGenerators(Chunk chunk);
	
	/**
	 * Gets saved generators amount
	 */
	public int getGeneratorsAmount();
	
	/**
	 * Saves generator location info to database
	 * @param GeneratorLocation
	 */
	public void saveGenerator(GeneratorLocation gl);
	
	/**
	 * Removes location from database
	 * @param Location
	 */
	public void removeGenerator(Location l);
	
	/*
	 * Scheduled generators table
	 */
	
	public void addSchedule(GeneratorLocation gl, Schedule schedule);
	
	public Schedule getSchedule(GeneratorLocation gl);
	
	public void removeSchedule(GeneratorLocation gl);
}
