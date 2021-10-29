package me.kryniowesegryderiusz.kgenerators.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Location;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IDatabase;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.PlacedGeneratorsLoader;
import me.kryniowesegryderiusz.kgenerators.classes.SQLConfig;
import me.kryniowesegryderiusz.kgenerators.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.files.FilesFunctions;

public class SQL implements IDatabase {
	
	private Connection conn;
	
	private final String placedTable = "`kgen_placed`";
	
	public SQL(DatabaseType dbType, SQLConfig sqlconfig)
	{
		try {
			if (dbType == DatabaseType.SQLITE)
			{
				FilesFunctions.mkdir("data");
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:"+Main.getInstance().getDataFolder().getPath()+"/data/database.db");
				Statement stat = conn.createStatement();
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS "+this.placedTable+" (id INTEGER NOT NULL PRIMARY KEY, world VARCHAR(32), x INT(8), y INT(8), z INT(8), generator_id VARCHAR(64), owner VARCHAR(16))");
				stat.close();
			}
			else if (dbType == DatabaseType.MYSQL)
			{
		        String DB_URL = "jdbc:mysql://" + sqlconfig.getDbHost() + ":" + sqlconfig.getDbPort() + "/" + sqlconfig.getDbName() +
		    	        "?characterEncoding=utf8&autoReconnect=true&user=" + sqlconfig.getDbUser() + "&password=" + sqlconfig.getDbPass();        
    	        conn = DriverManager.getConnection(DB_URL);
    			Statement stat = conn.createStatement();
    			stat.executeUpdate("CREATE TABLE IF NOT EXISTS "+this.placedTable+" (id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), generator_id VARCHAR(64), owner VARCHAR(16))");
    			stat.close();
			}
			else
			{
				Logger.error("Database: SQL: Error during initialisation SQL class - wrong database type!");
			}
			
			Logger.info("Database: Connected to " + dbType.toString() + " database");
			
		} catch (Exception e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error("Database: Cannot initialise SQL connection. Disabling plugin for safety reasons.");
			Logger.error(e);
		}
	}

	@Override
	public Connection getConnection() {
		return this.conn;
	}

	@Override
	public void loadGenerators() {
		try {
			PlacedGeneratorsLoader loader = new PlacedGeneratorsLoader();
			
			PreparedStatement stat;
			stat = conn.prepareStatement("SELECT * FROM "+this.placedTable);
	        ResultSet res = stat.executeQuery();
	    	while (res.next())
	    	{
	    		loader.loadNext(res.getString("generator_id"), res.getString("world")+","+res.getString("x")+","+res.getString("y")+","+res.getString("z"), res.getString("owner"));
	    	}
	    	loader.finish();
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot load placed generators. Disabling plugin for safety reasons");
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error(e);
		}
	}

	@Override
	public void savePlacedGenerator(GeneratorLocation gl) {
        try {
    		PreparedStatement stat;
    		stat = conn.prepareStatement("SELECT * FROM " + this.placedTable + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, gl.getLocation().getWorld().getName());
			stat.setInt(2, gl.getLocation().getBlockX());
			stat.setInt(3, gl.getLocation().getBlockY());
			stat.setInt(4, gl.getLocation().getBlockZ());
			ResultSet res = stat.executeQuery();
			
			if (!res.next())
			{
				PreparedStatement stat2;
	    		stat2 = conn.prepareStatement("INSERT INTO " + this.placedTable + " (world,x,y,z, generator_id, owner) VALUES (?, ?, ?, ?, ?, ?)");
	    		stat2.setString(1, gl.getLocation().getWorld().getName());
	    		stat2.setInt(2, gl.getLocation().getBlockX());
	    		stat2.setInt(3, gl.getLocation().getBlockY());
	    		stat2.setInt(4, gl.getLocation().getBlockZ());
	    		stat2.setString(5, gl.getGeneratorId());
	    		stat2.setString(6, gl.getOwner().getName());
				stat2.executeUpdate();
			}
			else
			{
				PreparedStatement stat2;
	    		stat2 = conn.prepareStatement("UPDATE " + this.placedTable + " SET `generator_id` = ?, `owner` = ? WHERE `world` = ? AND `x` = ? AND `y` = ? AND  `z` = ?");
	    		stat2.setString(1, gl.getGeneratorId());
	    		stat2.setString(2, gl.getOwner().getName());
	    		stat2.setString(3, gl.getLocation().getWorld().getName());
	    		stat2.setInt(4, gl.getLocation().getBlockX());
	    		stat2.setInt(5, gl.getLocation().getBlockY());
	    		stat2.setInt(6, gl.getLocation().getBlockZ());
				stat2.executeUpdate();
			}
		} catch (SQLException e) {
			Logger.error("Database: Cannot save generator to database");
			Logger.error(e);
		}
		
	}

	@Override
	public void removePlacedGenerator(Location l) {
		try {
			PreparedStatement stat;

			stat = conn.prepareStatement("DELETE FROM "+this.placedTable+" WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, l.getWorld().getName());
			stat.setInt(2, l.getBlockX());
			stat.setInt(3, l.getBlockY());
			stat.setInt(4, l.getBlockZ());
	        stat.executeUpdate();
	        stat.close();
		} catch (SQLException e) {
			Logger.error("Database: Cannot remove generator " + l.toString() + " from database");
			Logger.error(e);
		}
		
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

}
