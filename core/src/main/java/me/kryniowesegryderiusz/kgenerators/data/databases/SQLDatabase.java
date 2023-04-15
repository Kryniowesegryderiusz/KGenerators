package me.kryniowesegryderiusz.kgenerators.data.databases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.data.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.data.objects.GeneratorsLoader;
import me.kryniowesegryderiusz.kgenerators.data.objects.SQLConfig;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;

public class SQLDatabase implements IDatabase {

	private DatabaseType dbType;
	
	@Getter private HikariDataSource dataSource;

	private static String PLACED_TABLE = "`kgen_placed`";
	private static String SCHEDULED_TABLE = "`kgen_scheduled`";

	public SQLDatabase(DatabaseType dbType, SQLConfig sqlconfig) {
		this.dbType = dbType;

		try {
			if (dbType == DatabaseType.SQLITE) {
				/*
				FilesUtils.mkdir("data");
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection(
						"jdbc:sqlite:" + Main.getInstance().getDataFolder().getPath() + "/data/database.db");
				Statement stat = conn.createStatement();
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS " + PLACED_TABLE
						+ " (id INTEGER NOT NULL PRIMARY KEY, world VARCHAR(32), x INT(8), y INT(8), z INT(8), chunk_x INT(8), chunk_z INT(8), generator_id VARCHAR(64), owner VARCHAR(16), last_generated_object INT(8))");
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS " + SCHEDULED_TABLE
						+ " (id INTEGER NOT NULL PRIMARY KEY, world VARCHAR(32), x INT(8), y INT(8), z INT(8), creation_timestamp INT(8), delay_left INT(8))");
				stat.close();
				*/
				
				FilesUtils.mkdir("data");
				
				HikariConfig config = new HikariConfig();
				
				config.setPoolName("KGeneratorsSQLitePool");
				config.setDriverClassName("org.sqlite.JDBC");
				config.setJdbcUrl("jdbc:sqlite:" + Main.getInstance().getDataFolder().getPath() + "/data/database.db");
				config.setConnectionTestQuery("SELECT 1");
				config.setMaxLifetime(60000); // 60 Sec
				config.setMaximumPoolSize(sqlconfig.getPoolSize());
				if (sqlconfig.getPoolSize() > 1) {
					Logger.warn("Database " + dbType.name() + ": SQL: The pool size is higher than one! If you dont know what you're doing change database.pool-size in config.yml to one!");
				}
				config.setLeakDetectionThreshold(20000);
				
				dataSource = new HikariDataSource(config);
				
				Connection conn = dataSource.getConnection();
				Statement stat = conn.createStatement();
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS " + PLACED_TABLE
						+ " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), chunk_x INT(8), chunk_z INT(8), generator_id VARCHAR(64), owner VARCHAR(16), last_generated_object INT(8))");
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS " + SCHEDULED_TABLE
						+ " (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), creation_timestamp INT(8), delay_left INT(8))");
				stat.close();
				conn.close();
				
			} else if (dbType == DatabaseType.MYSQL) {
				/*
				String DB_URL = "jdbc:mysql://" + sqlconfig.getDbHost() + ":" + sqlconfig.getDbPort() + "/"
						+ sqlconfig.getDbName() + "?characterEncoding=utf8&autoReconnect=true&useSSL="
						+ sqlconfig.isSsl();
				conn = DriverManager.getConnection(DB_URL, sqlconfig.getDbUser(), sqlconfig.getDbPass());

				Statement stat = conn.createStatement();
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS " + PLACED_TABLE
						+ " (id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), chunk_x INT(8), chunk_z INT(8), generator_id VARCHAR(64), owner VARCHAR(16), last_generated_object INT(8))");
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS " + SCHEDULED_TABLE
						+ " (id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), creation_timestamp INT(8), delay_left INT(8))");
				stat.close();
				*/
				
				HikariConfig config = new HikariConfig();
				
				
				config.setPoolName("KGeneratorsMySQLPool");
				config.setJdbcUrl("jdbc:mysql://" + sqlconfig.getDbHost() + ":" + sqlconfig.getDbPort() + "/" + sqlconfig.getDbName() + "?characterEncoding=utf8&autoReconnect=true&useSSL=" + sqlconfig.isSsl());
				config.setUsername(sqlconfig.getDbUser());
				config.setPassword(sqlconfig.getDbPass());
				config.setConnectionTestQuery("SELECT 1");
				config.setMaxLifetime(60000); // 60 Sec
				config.setMaximumPoolSize(sqlconfig.getPoolSize());
				if (sqlconfig.getPoolSize() <= 1)
					Logger.warn("Database " + dbType.name() + ": SQL: The pool size is set to one! You probably want to change database.pool-size in config.yml to 3 or higher!");
				config.setLeakDetectionThreshold(20000);
				
				dataSource = new HikariDataSource(config);
				
				Connection conn = dataSource.getConnection();
				Statement stat = conn.createStatement();
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS " + PLACED_TABLE
						+ " (id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), chunk_x INT(8), chunk_z INT(8), generator_id VARCHAR(64), owner VARCHAR(16), last_generated_object INT(8))");
				stat.executeUpdate("CREATE TABLE IF NOT EXISTS " + SCHEDULED_TABLE
						+ " (id INT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT, world VARCHAR(32), x INT(8), y INT(8), z INT(8), creation_timestamp INT(8), delay_left INT(8))");
				stat.close();
				conn.close();
				
			} else {
				Logger.error("Database " + dbType.name() + ": SQL: Error during initialisation SQL class - wrong database type!");
				return;
			}


			Logger.info("Database " + dbType.name() + ": Connected to " + dbType.toString() + " database");

		} catch (Exception e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error("Database " + dbType.name() + ": Cannot initialise SQL connection. Disabling plugin for safety reasons.");
			Logger.error(e);
		}
	}

	@Override
	public void closeConnection() {
		if (dataSource != null && !dataSource.isClosed()) dataSource.close();
		Logger.info("Database " + dbType.name() + ": Connection closed");
	}

	@Override
	public void updateTable() {

		/*
		 * Add chunks columns
		 */
		
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
		} catch (Exception e) {
			Logger.error(e);
		}
		
		PreparedStatement stat = null;
		
		try {
			
			if (dbType == DatabaseType.MYSQL)
				stat = conn.prepareStatement("SHOW COLUMNS FROM " + PLACED_TABLE + " LIKE 'chunk_x'");
			else
				stat = conn.prepareStatement("PRAGMA TABLE_INFO(" + PLACED_TABLE + ")");
			ResultSet res = stat.executeQuery();
			boolean found = false;
			while (res.next()) {
				if ((dbType == DatabaseType.MYSQL && res.getString(1).equals("chunk_x"))
						|| (dbType == DatabaseType.SQLITE && res.getString("name").equals("chunk_x"))) {
					found = true;
					break;
				}
			}
			stat.close();
			res.close();

			if (!found) {
				Logger.warn("Database " + dbType.name() + ": Updating database to V7! That can take a while! Do not stop the server!");

				stat = conn.prepareStatement("ALTER TABLE " + PLACED_TABLE + " ADD chunk_x INT(8)");
				stat.executeUpdate();
				stat.close();
				stat = conn.prepareStatement("ALTER TABLE " + PLACED_TABLE + " ADD chunk_z INT(8)");
				stat.executeUpdate();
				stat.close();

				
				stat = dataSource.getConnection().prepareStatement("UPDATE " + PLACED_TABLE
						+ " SET `chunk_x` = ?, `chunk_z` = ? WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
				for (GeneratorLocation gl : this.getGenerators()) {
					stat.setInt(1, gl.getChunk().getX());
					stat.setInt(2, gl.getChunk().getZ());
					stat.setString(3, gl.getLocation().getWorld().getName());
					stat.setInt(4, gl.getLocation().getBlockX());
					stat.setInt(5, gl.getLocation().getBlockY());
					stat.setInt(6, gl.getLocation().getBlockZ());
					stat.executeUpdate();
				}
				stat.close();

				Logger.warn("Database " + dbType.name() + ": Updated database with chunks!");
			}

		} catch (Exception e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error("Database " + dbType.name() + ": Cannot update database to KGenV7. Disabling plugin.");
			Logger.error(e);
		}

		/*
		 * Add last generated object columns
		 */

		try {
			if (dbType == DatabaseType.MYSQL)
				stat = conn.prepareStatement("SHOW COLUMNS FROM " + PLACED_TABLE + " LIKE 'last_generated_object'");
			else
				stat = conn.prepareStatement("PRAGMA TABLE_INFO(" + PLACED_TABLE + ")");
			
			ResultSet res = stat.executeQuery();
			boolean found = false;
			while (res.next()) {
				if ((dbType == DatabaseType.MYSQL && res.getString(1).equals("last_generated_object"))
						|| (dbType == DatabaseType.SQLITE && res.getString("name").equals("last_generated_object"))) {
					found = true;
					break;
				}
			}
			stat.close();
			res.close();
			
			if (!found) {
				Logger.warn("Database " + dbType.name() + ": Updating database to V7.3! That can take a while! Do not stop the server!");

				stat = conn.prepareStatement("ALTER TABLE " + PLACED_TABLE + " ADD last_generated_object INT(8)");
				stat.executeUpdate();
				stat.close();

				stat = conn.prepareStatement("UPDATE " + PLACED_TABLE
						+ " SET `chunk_x` = ?, `chunk_z` = ? WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ? AND `last_generated_object` = -1");
				for (GeneratorLocation gl : this.getGenerators()) {
					stat.setInt(1, gl.getChunk().getX());
					stat.setInt(2, gl.getChunk().getZ());
					stat.setString(3, gl.getLocation().getWorld().getName());
					stat.setInt(4, gl.getLocation().getBlockX());
					stat.setInt(5, gl.getLocation().getBlockY());
					stat.setInt(6, gl.getLocation().getBlockZ());
					stat.executeUpdate();
				}

				Logger.warn("Database " + dbType.name() + ": Updated database with last generated objects!");
			}

		} catch (Exception e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error("Database " + dbType.name() + ": Cannot update database to KGenV7.3. Disabling plugin.");
			Logger.error(e);
		}
		
		try {
			this.close(stat, conn, null);
		} catch (Exception e) {
			Logger.error(e);
		}
	}

	@Override
	public void saveGenerator(GeneratorLocation gl) {
		
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		try {
			//No id - new generator
			if (gl.getId() == -1) {
					conn = dataSource.getConnection();
					stat = conn.prepareStatement("INSERT INTO " + PLACED_TABLE
							+ " (world,x,y,z,generator_id,owner,chunk_x,chunk_z,last_generated_object) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
					stat.setString(1, gl.getLocation().getWorld().getName());
					stat.setInt(2, gl.getLocation().getBlockX());
					stat.setInt(3, gl.getLocation().getBlockY());
					stat.setInt(4, gl.getLocation().getBlockZ());
					stat.setString(5, gl.getGenerator().getId());
					stat.setString(6, gl.getOwner().getName());
					stat.setInt(7, gl.getChunk().getX());
					stat.setInt(8, gl.getChunk().getZ());
					stat.setInt(9, gl.getLastGeneratedObjectId());
					stat.executeUpdate();
					
			        try (ResultSet generatedKeys = stat.getGeneratedKeys()) {
			            if (generatedKeys.next()) {
			                gl.setId(generatedKeys.getInt(1));
			            }
			            else {
			                throw new SQLException("Creating user failed, no ID obtained.");
			            }
			        }

					
			//has id - just save
			} else {
				conn = dataSource.getConnection();
				stat = conn.prepareStatement("UPDATE " + PLACED_TABLE
						+ " SET `generator_id` = ?, `owner` = ?, `last_generated_object` = ? WHERE `id` = ?");
				stat.setString(1, gl.getGenerator().getId());
				stat.setString(2, gl.getOwner().getName());
				stat.setInt(3, gl.getLastGeneratedObjectId());
				stat.setInt(4, gl.getId());
				stat.executeUpdate();
			}
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot save generator " + gl.toString() + " to database");
			Logger.error(e);
		} finally {
			this.close(stat, conn, res);
		}

	}

	@Override
	public GeneratorLocation getGenerator(Location location) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		GeneratorLocation gl = null;
		try {
			GeneratorsLoader loader = new GeneratorsLoader();

			conn = dataSource.getConnection();
			stat = conn.prepareStatement(
					"SELECT * FROM " + PLACED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, location.getWorld().getName());
			stat.setInt(2, location.getBlockX());
			stat.setInt(3, location.getBlockY());
			stat.setInt(4, location.getBlockZ());
			res = stat.executeQuery();
			while (res.next()) {
				loader.loadNext(res);
			}
			if (loader.getAmount() > 0)
				gl = loader.finish().get(0);
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot get generator: " + Main.getPlacedGenerators().locationToString(location));
			Logger.error(e);
		} finally {
			this.close(stat, conn, res);
		}
		return gl;
	}

	@Override
	public ArrayList<GeneratorLocation> getGenerators() {
		
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();

			conn = dataSource.getConnection();
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE);
			res = stat.executeQuery();
			while (res.next()) {
				loader.loadNext(res);
			}
			gl = loader.finish();
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot get generators");
			Logger.error(e);
		} finally {
			this.close(stat, conn, res);
		}
		return gl;
	}
	

	@Override
	public ArrayList<GeneratorLocation> getGenerators(int firstRow, int rowAmount) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();

			conn = dataSource.getConnection();
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE + " LIMIT " + rowAmount + " OFFSET " + firstRow);
			res = stat.executeQuery();
			while (res.next()) {
				loader.loadNext(res);
			}
			gl = loader.finish();
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot get generators");
			Logger.error(e);
			gl = null;
		} finally {
			this.close(stat, conn, res);
		}
		return gl;
	}

	@Override
	public ArrayList<GeneratorLocation> getGenerators(World world, int minX, int minY, int minZ, int maxX, int maxY,
			int maxZ) {
		
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();

			conn = dataSource.getConnection();
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE
					+ " WHERE `world` = ? AND `x` >= ? AND `x` <= ? AND `y` >= ? AND `y` <= ? AND `z` >= ? AND `z` <= ?");
			stat.setString(1, world.getName());
			stat.setInt(2, minX);
			stat.setInt(3, maxX);
			stat.setInt(4, minY);
			stat.setInt(5, maxY);
			stat.setInt(6, minZ);
			stat.setInt(7, maxZ);
			res = stat.executeQuery();
			while (res.next()) {
				loader.loadNext(res);
			}
			gl = loader.finish();
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot get generators in range: X:" + minX + "-" + maxX + " Y:" + minY + "-" + maxY
					+ " Z:" + minZ + "-" + maxZ);
			Logger.error(e);
		} finally {
			this.close(stat, conn, res);
		}
		return gl;
	}
	
	@Override
	public ArrayList<GeneratorLocation> getGenerators(String owner) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();

			conn = dataSource.getConnection();
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE + " WHERE `owner` = ?");
			stat.setString(1, owner);
			res = stat.executeQuery();
			while (res.next()) {
				loader.loadNext(res);
			}
			gl = loader.finish();
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot get generators by owner: " + owner);
			Logger.error(e);
		} finally {
			this.close(stat, conn, res);
		}
		return gl;
	}

	@Override
	public ArrayList<GeneratorLocation> getGenerators(Chunk chunk) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		ArrayList<GeneratorLocation> gl = new ArrayList<GeneratorLocation>();
		try {
			GeneratorsLoader loader = new GeneratorsLoader();

			conn = dataSource.getConnection();
			stat = conn.prepareStatement("SELECT * FROM " + PLACED_TABLE + " WHERE `world` = ? AND `chunk_x` = ? AND `chunk_z` = ?");
			stat.setString(1, chunk.getWorld().getName());
			stat.setInt(2, chunk.getX());
			stat.setInt(3, chunk.getZ());
			res = stat.executeQuery();
			while (res.next()) {
				loader.loadNext(res);
			}
			gl = loader.finish();
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot get generators by chunk: " + chunk.toString());
			Logger.error(e);
		} finally {
			this.close(stat, conn, res);
		}
		return gl;
	}

	@Override
	public int getGeneratorsAmount() {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		int amount = 0;
		try {
			conn = dataSource.getConnection();
			stat = conn.prepareStatement("SELECT COUNT(*) FROM " + PLACED_TABLE);
			res = stat.executeQuery();
			while (res.next()) {
				amount = res.getInt(1);
			}
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot get generators amount");
			Logger.error(e);
		} finally {
			this.close(stat, conn, res);
		}
		return amount;
	}

	@Override
	public void removeGenerator(Location l) {
		Connection conn = null;
		PreparedStatement stat = null;
		
		try {
			conn = dataSource.getConnection();
			stat = conn.prepareStatement(
					"DELETE FROM " + PLACED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, l.getWorld().getName());
			stat.setInt(2, l.getBlockX());
			stat.setInt(3, l.getBlockY());
			stat.setInt(4, l.getBlockZ());
			stat.executeUpdate();
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot remove generator " + Main.getPlacedGenerators().locationToString(l)
					+ " from database");
			Logger.error(e);
		} finally {
			this.close(stat, conn, null);
		}
	}

	@Override
	public void addSchedule(GeneratorLocation gl, Schedule schedule) {
		Connection conn = null;
		PreparedStatement stat = null;
		
		try {
			conn = dataSource.getConnection();
			stat = conn.prepareStatement("INSERT INTO " + SCHEDULED_TABLE
					+ " (world, x, y, z, creation_timestamp, delay_left) VALUES (?, ?, ?, ?, ?, ?)");
			stat.setString(1, gl.getLocation().getWorld().getName());
			stat.setInt(2, gl.getLocation().getBlockX());
			stat.setInt(3, gl.getLocation().getBlockY());
			stat.setInt(4, gl.getLocation().getBlockZ());
			stat.setLong(5, Instant.now().getEpochSecond());
			stat.setInt(6, schedule.getTimeLeft());
			stat.executeUpdate();
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot remove schedule " + gl.toString() + " from database");
			Logger.error(e);
		} finally {
			this.close(stat, conn, null);
		}
	}

	@Override
	public Schedule getSchedule(GeneratorLocation gl) {
		Connection conn = null;
		PreparedStatement stat = null;
		ResultSet res = null;
		
		Schedule schedule = null;
		try {
			conn = dataSource.getConnection();
			stat = conn.prepareStatement(
					"SELECT * FROM " + SCHEDULED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, gl.getLocation().getWorld().getName());
			stat.setInt(2, gl.getLocation().getBlockX());
			stat.setInt(3, gl.getLocation().getBlockY());
			stat.setInt(4, gl.getLocation().getBlockZ());
			res = stat.executeQuery();
			while (res.next()) {
				schedule = new Schedule(res.getInt("delay_left"), res.getInt("creation_timestamp"));
			}
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot get schedule: " + gl.toString());
			Logger.error(e);
		} finally {
			this.close(stat, conn, res);
		}
		return schedule;
	}

	@Override
	public void removeSchedule(GeneratorLocation gl) {
		Connection conn = null;
		PreparedStatement stat = null;
		
		Location l = gl.getLocation();
		try {
			conn = dataSource.getConnection();
			stat = conn.prepareStatement(
					"DELETE FROM " + SCHEDULED_TABLE + " WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
			stat.setString(1, l.getWorld().getName());
			stat.setInt(2, l.getBlockX());
			stat.setInt(3, l.getBlockY());
			stat.setInt(4, l.getBlockZ());
			stat.executeUpdate();
		} catch (Exception e) {
			Logger.error("Database " + dbType.name() + ": Cannot remove schedule " + gl.toString() + " from  database");
			Logger.error(e);
		} finally {
			this.close(stat, conn, null);
		}
	}
	
	public void close(Statement stat, Connection conn, ResultSet res) {
		try {
			if (stat != null && !stat.isClosed()) stat.close();
			if (conn != null && !conn.isClosed()) conn.close();
			if (res != null && !res.isClosed()) res.close();
		} catch (Exception e) {
			Logger.error(e);
		}
	}
}
