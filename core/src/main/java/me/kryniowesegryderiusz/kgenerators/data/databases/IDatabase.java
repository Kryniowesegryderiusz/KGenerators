package me.kryniowesegryderiusz.kgenerators.data.databases;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.World;

import com.zaxxer.hikari.HikariDataSource;

import me.kryniowesegryderiusz.kgenerators.generators.locations.PlacedGeneratorsManager.ChunkInfo;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;

public interface IDatabase {
	
	/**
	 * Closes database connection
	 */
	public void closeConnection();
	
	public void updateTable();
	
	/*
	 * Closes connection objects;
	 */
	public void close(Statement stat, Connection conn, ResultSet res);
	
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
	 * Gets all saved generators
	 */
	public ArrayList<GeneratorLocation> getGenerators(int firstRow, int rowAmount);
	
	/**
	 * Gets saved generators according to ranges
	 */
	public ArrayList<GeneratorLocation> getGenerators(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ);
	
	/**
	 * Gets saved generators according to owner
	 */
	public ArrayList<GeneratorLocation> getGenerators(String owner);
	
	/**
	 * Gets saved generators according to chunk
	 * return null if wasnt properly loaded
	 */
	@Nullable public ArrayList<GeneratorLocation> getGenerators(ChunkInfo chunkInfo);
	
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
	 * Saves generator location info to database
	 * @param GeneratorLocation
	 */
	public void saveGenerator(GeneratorLocation gl, boolean migrate);
	
	/**
	 * Removes location from database
	 * @param Location
	 */
	public void removeGenerator(Location l);
	
	public HikariDataSource getDataSource();
	
	/*
	 * Scheduled generators table
	 */
	
	public void addSchedule(GeneratorLocation gl, Schedule schedule);
	
	public Schedule getSchedule(GeneratorLocation gl);
	
	public void removeSchedule(GeneratorLocation gl);
}
