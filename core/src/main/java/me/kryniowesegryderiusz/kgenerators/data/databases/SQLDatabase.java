package me.kryniowesegryderiusz.kgenerators.data.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.data.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.data.objects.GeneratorsLoader;
import me.kryniowesegryderiusz.kgenerators.data.objects.SQLConfig;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;

public class SQLDatabase implements IDatabase {
	
	private Connection conn;
	
	private DatabaseType dbType;
	
	static String PLACED_TABLE = "`kgen_placed`";
	static String SCHEDULED_TABLE = "`kgen_scheduled`";
	
	public SQLDatabase(DatabaseType dbType, SQLConfig sqlconfig)
	{
		this.dbType = dbType;
		
		try {
			if (dbType == DatabaseType.SQLITE) {
				FilesUtils.mkdir("data");
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:"+Main.getInstance().getDataFolder().getPath()+"/data/database.db");
				Statement stat = conn.createStatement();
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS "+PLACED_TABLE+" (id INTEGER NOT NULL PRIMARY KEY, world VARCHAR(32), x INT(8), y INT(8), z INT(8), chunk_x INT(8), chunk_z INT(8), generator_id VARCHAR(64), owner VARCHAR(16), last_generated_object INT(8))");
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS "+SCHEDULED_TABLE+" (id INTEGER NOT NULL PRIMARY KEY, world VARCHAR(32), x INT(8), y INT(8), z INT(8), creation_timestamp INT(8), delay_left INT(8))");
				stat.close();
			} else if (dbType == DatabaseType.MYSQL) {
		        String DB_URL = "jdbc:mysql://" + sqlconfig.getDbHost() + ":" + sqlconfig.getDbPort() + "/" + sqlconfig.getDbName() +
		    	        "?characterEncoding=utf8&autoReconnect=true";        
    	        conn = DriverManager.getConnection(DB_URL, sqlconfig.getDbUser(), sqlconfig.getDbPass());
    	        
    			Statement stat = conn.createStatement();
    			stat.executeUpdate("CREATE TABLE IF NOT EXISTS "+PLACED_TABLE+" (id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), chunk_x INT(8), chunk_z INT(8), generator_id VARCHAR(64), owner VARCHAR(16), last_generated_object INT(8))");
    			stat.executeUpdate("CREATE TABLE IF NOT EXISTS "+SCHEDULED_TABLE+" (id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), creation_timestamp INT(8), delay_left INT(8))");
    			stat.close();
			} else {
				Logger.error("Database: SQL: Error during initialisation SQL class - wrong database type!");
				return;
			}
			
			Logger.info("Database: Connected to " + dbType.toString() + " database");
			
		} catch (Exception e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error("Database: Cannot initialise SQL connection. Disabling plugin for safety reasons.");
			Logger.error(e);
		}
	}
	
	@Override
	public void updateTable() {
		
		/*
		 * Add chunks columns
		 */
		
		try {
			PreparedStatement stat;
			if (dbType == DatabaseType.MYSQL)
				stat = conn.prepareStatement("SHOW COLUMNS FROM "+PLACED_TABLE+" LIKE 'chunk_x'");
			else
				stat = conn.prepareStatement("PRAGMA TABLE_INFO("+PLACED_TABLE+")");
			ResultSet res = stat.executeQuery();
			boolean found = false;
			while(res.next()) {
				if ((dbType == DatabaseType.MYSQL && res.getString(1).equals("chunk_x"))
						|| (dbType == DatabaseType.SQLITE && res.getString("name").equals("chunk_x"))) {
					found = true;
					break;
				}
	        }
	        stat.close();
	        
	        if(!found) {
				Logger.warn("Database: Updating database to V7! That can take a while! Do not stop the server!");
				
				Statement stat2 = conn.createStatement();
	        	stat2.executeUpdate("ALTER TABLE "+PLACED_TABLE+" ADD chunk_x INT(8)");
	            stat2.executeUpdate("ALTER TABLE "+PLACED_TABLE+" ADD chunk_z INT(8)");
	            stat2.close();
	            
	            for (GeneratorLocation gl : this.getGenerators()) {
	    			PreparedStatement stat3;
	    			stat3 = conn.prepareStatement("UPDATE " + PLACED_TABLE + " SET `chunk_x` = ?, `chunk_z` = ? WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
	    			stat3.setInt(1, gl.getChunk().getX());
	        		stat3.setInt(2, gl.getChunk().getZ());
	        		stat3.setString(3, gl.getLocation().getWorld().getName());
	        		stat3.setInt(4, gl.getLocation().getBlockX());
	        		stat3.setInt(5, gl.getLocation().getBlockY());
	        		stat3.setInt(6, gl.getLocation().getBlockZ());
	        		stat3.executeUpdate();
	            }
	            
	            Logger.warn("Database: Updated database with chunks!");
	        }
	        
		} catch (Exception e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error("Database: Cannot update database to KGenV7. Disabling plugin.");
			Logger.error(e);
		}
		
		
		/*
		 * Add last generated object columns
		 */
		
		try {
			PreparedStatement stat;
			if (dbType == DatabaseType.MYSQL)
				stat = conn.prepareStatement("SHOW COLUMNS FROM "+PLACED_TABLE+" LIKE 'last_generated_object'");
			else
				stat = conn.prepareStatement("PRAGMA TABLE_INFO("+PLACED_TABLE+")");
			ResultSet res = stat.executeQuery();
			boolean found = false;
			while(res.next()) {
				if ((dbType == DatabaseType.MYSQL && res.getString(1).equals("last_generated_object"))
						|| (dbType == DatabaseType.SQLITE && res.getString("name").equals("last_generated_object"))) {
					found = true;
					break;
				}
	        }
	        stat.close();
	        
	        if(!found) {
				Logger.warn("Database: Updating database to V7.3! That can take a while! Do not stop the server!");
				
				Statement stat2 = conn.createStatement();
	        	stat2.executeUpdate("ALTER TABLE "+PLACED_TABLE+" ADD last_generated_object INT(8)");
	            stat2.close();
	            
	            for (GeneratorLocation gl : this.getGenerators()) {
	    			PreparedStatement stat3;
	    			stat3 = conn.prepareStatement("UPDATE " + PLACED_TABLE + " SET `chunk_x` = ?, `chunk_z` = ? WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ? AND `last_generated_object` = -1");
	    			stat3.setInt(1, gl.getChunk().getX());
	        		stat3.setInt(2, gl.getChunk().getZ());
	        		stat3.setString(3, gl.getLocation().getWorld().getName());
	        		stat3.setInt(4, gl.getLocation().getBlockX());
	        		stat3.setInt(5, gl.getLocation().getBlockY());
	        		stat3.setInt(6, gl.getLocation().getBlockZ());
	        		stat3.executeUpdate();
	            }
	            
	            Logger.warn("Database: Updated database with last generated objects!");
	        }
	        
		} catch (Exception e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error("Database: Cannot update database to KGenV7.3. Disabling plugin.");
			Logger.error(e);
		}
	}

	@Override
	public Connection getConnection() {
		return this.conn;
	}
	
	@Override
	public void closeConnection() {
		try {
			this.conn.close();
			Logger.info("Database: Connection closed");
		} catch (SQLException e) {
			Logger.error("Database: Cannot close connection");
			Logger.error(e);
		}
	}

	@Override
	public void saveGenerator(GeneratorLocation gl) {
		
        try {
    		if (this.isPlacedGeneratorInDatabase(gl)) {
    			PreparedStatement stat2;
        		stat2 = conn.prepareStatement("UPDATE " + PLACED_TABLE + " SET `generator_id` = ?, `owner` = ?, `last_generated_object` = ? WHERE `world` = ? AND `x` = ? AND `y` = ? AND  `z` = ?");
        		stat2.setString(1, gl.getGenerator().getId());
        		stat2.setString(2, gl.getOwner().getName());
        		stat2.setInt(3, gl.getLastGeneratedObjectId());
        		stat2.setString(4, gl.getLocation().getWorld().getName());
        		stat2.setInt(5, gl.getLocation().getBlockX());
        		stat2.setInt(6, gl.getLocation().getBlockY());
        		stat2.setInt(7, gl.getLocation().getBlockZ());
    			stat2.executeUpdate();
    		} else {
    			PreparedStatement stat2;
        		stat2 = conn.prepareStatement("INSERT INTO " + PLACED_TABLE + " (world,x,y,z,generator_id,owner,chunk_x,chunk_z,last_generated_object) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        		stat2.setString(1, gl.getLocation().getWorld().getName());
        		stat2.setInt(2, gl.getLocation().getBlockX());
        		stat2.setInt(3, gl.getLocation().getBlockY());
        		stat2.setInt(4, gl.getLocation().getBlockZ());
        		stat2.setString(5, gl.getGenerator().getId());
        		stat2.setString(6, gl.getOwner().getName());
        		stat2.setInt(7, gl.getChunk().getX());
        		stat2.setInt(8, gl.getChunk().getZ());
        		stat2.setInt(9, gl.getLastGeneratedObjectId());
    			stat2.executeUpdate();
    		}
		} catch (SQLException e) {
			Logger.error("Database: Cannot save generator " + gl.toString() + " to database");
			Logger.error(e);
		}
		
	}
	
	private boolean isPlacedGeneratorInDatabase(GeneratorLocation gl) {
		boolean exists = false;
        try {
    		PreparedStatement stat;
    		stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, gl.getLocation().getWorld().getName());
			stat.setInt(2, gl.getLocation().getBlockX());
			stat.setInt(3, gl.getLocation().getBlockY());
			stat.setInt(4, gl.getLocation().getBlockZ());
			ResultSet res = stat.executeQuery();
			if (res.next())
				exists = true;
			stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot check if generator " + gl.toString() + " exists");
			Logger.error(e);
		}
        return exists;
	}

	@Override
	public GeneratorLocation getGenerator(Location location) {
		GeneratorLocation gl = null;
		try {
			GeneratorsLoader loader = new GeneratorsLoader();
			
			PreparedStatement stat;
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, location.getWorld().getName());
			stat.setInt(2, location.getBlockX());
			stat.setInt(3, location.getBlockY());
			stat.setInt(4, location.getBlockZ());
	        ResultSet res = stat.executeQuery();
	    	while (res.next()) {
	    		loader.loadNext(res);
	    	}
	    	if (loader.getAmount() > 0)
	    		gl = loader.finish().get(0);
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot get generator: " + Main.getPlacedGenerators().locationToString(location));
			Logger.error(e);
		}
		return gl;
	}

	@Override
	public ArrayList<GeneratorLocation> getGenerators() {
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();
			
			PreparedStatement stat;
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE);
	        ResultSet res = stat.executeQuery();
	    	while (res.next()) {
	    		loader.loadNext(res);
	    	}
	    	gl = loader.finish();
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot get generators");
			Logger.error(e);
		}
		return gl;
	}

	@Override
	public ArrayList<GeneratorLocation> getGenerators(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();
			
			PreparedStatement stat;
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE 
					+ " WHERE `world` = ? AND `x` >= ? AND `x` <= ? AND `y` >= ? AND `y` <= ? AND `z` >= ? AND `z` <= ?");
			stat.setString(1, world.getName());
			stat.setInt(2, minX);
			stat.setInt(3, maxX);
			stat.setInt(4, minY);
			stat.setInt(5, maxY);
			stat.setInt(6, minZ);
			stat.setInt(7, maxZ);
	        ResultSet res = stat.executeQuery();
	    	while (res.next()) {
	    		loader.loadNext(res);
	    	}
	    	gl = loader.finish();
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot get generators in range: X:" + minX+"-"+maxX+ " Y:"+minY+"-"+maxY+" Z:"+minZ+"-"+maxZ);
			Logger.error(e);
		}
		return gl;
	}

	@Override
	public ArrayList<GeneratorLocation> getGenerators(String owner) {
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();
			
			PreparedStatement stat;
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE + " WHERE `owner` = ?");
			stat.setString(1, owner);
	        ResultSet res = stat.executeQuery();
	    	while (res.next()) {
	    		loader.loadNext(res);
	    	}
	    	gl = loader.finish();
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot get generators by owner: " + owner);
			Logger.error(e);
		}
		return gl;
	}

	@Override
	public ArrayList<GeneratorLocation> getGenerators(Chunk chunk) {
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();
			
			PreparedStatement stat;
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE + " WHERE `world` = ? AND `chunk_x` = ? AND `chunk_z` = ?");
			stat.setString(1, chunk.getWorld().getName());
			stat.setInt(2, chunk.getX());
			stat.setInt(3, chunk.getZ());
	        ResultSet res = stat.executeQuery();
	    	while (res.next()) {
	    		loader.loadNext(res);
	    	}
	    	gl = loader.finish();
	        stat.close();
	        
		} catch (SQLException e) {
			Logger.error("Database: Cannot get generators by chunk: " + chunk.toString());
			Logger.error(e);
		}
		return gl;
	}

	@Override
	public int getGeneratorsAmount() {
		int amount = 0;
		try {
			PreparedStatement stat;
			stat = conn.prepareStatement("SELECT COUNT(*) FROM " + PLACED_TABLE);
	        ResultSet res = stat.executeQuery();
	    	while (res.next()) {
	    		amount = res.getInt(1);
	    	}
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot get generators amount");
			Logger.error(e);
		}
		return amount;
	}

	@Override
	public void removeGenerator(Location l) {
		try {
			PreparedStatement stat;
			
			stat = conn.prepareStatement("DELETE FROM " + PLACED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, l.getWorld().getName());
			stat.setInt(2, l.getBlockX());
			stat.setInt(3, l.getBlockY());
			stat.setInt(4, l.getBlockZ());
	        stat.executeUpdate();
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot remove generator " + Main.getPlacedGenerators().locationToString(l) + " from database");
			Logger.error(e);
		}
	}

	@Override
	public void addSchedule(GeneratorLocation gl, Schedule schedule) {
		try {
			PreparedStatement stat;
			stat = conn.prepareStatement("INSERT INTO " + SCHEDULED_TABLE + " (world, x, y, z, creation_timestamp, delay_left) VALUES (?, ?, ?, ?, ?, ?)");
			stat.setString(1, gl.getLocation().getWorld().getName());
			stat.setInt(2, gl.getLocation().getBlockX());
			stat.setInt(3, gl.getLocation().getBlockY());
			stat.setInt(4, gl.getLocation().getBlockZ());
			stat.setLong(5, Instant.now().getEpochSecond());
			stat.setInt(6, schedule.getTimeLeft());
			stat.executeUpdate();
		} catch (SQLException e) {
			Logger.error("Database: Cannot remove schedule " + gl.toString() + " from database");
			Logger.error(e);
		}
	}

	@Override
	public Schedule getSchedule(GeneratorLocation gl) {
		Schedule schedule = null;
		try {
			PreparedStatement stat;
			stat = conn.prepareStatement("SELECT * FROM " + SCHEDULED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, gl.getLocation().getWorld().getName());
			stat.setInt(2, gl.getLocation().getBlockX());
			stat.setInt(3, gl.getLocation().getBlockY());
			stat.setInt(4, gl.getLocation().getBlockZ());
	        ResultSet res = stat.executeQuery();
	    	while (res.next()) {
	    		schedule = new Schedule(res.getInt("delay_left"), res.getInt("creation_timestamp"));
	    	}
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot get schedule: " + gl.toString());
			Logger.error(e);
		}
		return schedule;
	}

	@Override
	public void removeSchedule(GeneratorLocation gl) {
		Location l = gl.getLocation();
		try {
			PreparedStatement stat;
			
			stat = conn.prepareStatement("DELETE FROM " + SCHEDULED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, l.getWorld().getName());
			stat.setInt(2, l.getBlockX());
			stat.setInt(3, l.getBlockY());
			stat.setInt(4, l.getBlockZ());
	        stat.executeUpdate();
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot remove schedule " + gl.toString() + " from  database");
			Logger.error(e);
		}
	}
}
