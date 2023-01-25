package me.kryniowesegryderiusz.kgenerators.data;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.data.databases.IDatabase;
import me.kryniowesegryderiusz.kgenerators.data.databases.SQLDatabase;
import me.kryniowesegryderiusz.kgenerators.data.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;

public class DatabaseManager {

	@Getter
	private IDatabase db;
	
	@Getter boolean isMigratorRunning = false;

	public DatabaseManager(DatabaseType dbType) {
		Logger.debugPluginLoad("DatabaseManager: Setting up manager");
		this.db = this.getDatabase(dbType);
	}

	/**
	 * Gets proper database or null if OTHER
	 * 
	 * @param dbType
	 * @return
	 */
	@Nullable
	public IDatabase getDatabase(DatabaseType dbType) {
		switch (dbType) {
		case MYSQL:
			return new SQLDatabase(DatabaseType.MYSQL, Main.getSettings().getSqlConfig());
		case SQLITE:
			return new SQLDatabase(DatabaseType.SQLITE, Main.getSettings().getSqlConfig());
		default:
			Logger.error("DatabasesHandler: Could not return OTHER database, as not specified");
			return null;
		}
	}
	
	private MigratorInfo migratorInfo = null;

	public void changeTo(DatabaseType toDbType) {

		Main.getInstance().getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				Logger.info("DatabasesHandler: Converting database from " + Main.getSettings().getDbType().name() + " to " + toDbType.name() + ". IT COULD TAKE A WHILE!");

				//db.getDataSource().setConnectionTimeout(5*60*1000);
				//db.getDataSource().setLeakDetectionThreshold(5*60*1000);
				//db.getDataSource().setMaximumPoolSize(500);
				
				IDatabase newDb = getDatabase(toDbType);

				if (newDb == null) {
					Logger.error("DatabasesHandler: Cannot change database to " + toDbType.toString()
							+ "! Connection could not be initialised!");
					return;
				}
				
				int amount = db.getGeneratorsAmount();
				
				migratorInfo = new MigratorInfo(amount);
				isMigratorRunning = true;

				int amountAtOnce = 500;
				
				for (int i = 0; i <= amount; i = i + amountAtOnce) {
					Logger.info("DatabasesHandler: Migrating next "+amountAtOnce+" generators: " + i + "-" + (i+amountAtOnce));
					ArrayList<GeneratorLocation> next = db.getGenerators(i, amountAtOnce);
					Logger.info("DatabaseHandler: Obtained " + next.size() + " from old database");
					for (GeneratorLocation gl : next) {
						newDb.saveGenerator(gl);
						migratorInfo.increment();
					}
				}


				Main.getSettings().setDbType(toDbType);
				FilesUtils.changeText(new File(Main.getInstance().getDataFolder(), "config.yml"), "  dbtype: ",
						"  dbtype: " + toDbType.name() + "  #Earlier: ");

				migratorInfo.end();
				migratorInfo = null;
				isMigratorRunning = false;
				
				db = newDb;
				Logger.info("DatabasesHandler: Succesfully converted database to " + toDbType.toString() + "!");
			}
		});
	}
	
	class MigratorInfo {
		
		int amount;
		int migratedAmount = 0;
		int taskId;
		
		public MigratorInfo(int generatorsAmount) {
			amount = generatorsAmount;
			
			taskId = Main.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
				@Override
				public void run() {
					Logger.info("DatabasesHandler: Migrated " + migratedAmount + "/" + amount);
				}
			}, 40L, 40L);
		}
		
		public void increment() {
			this.migratedAmount++;
		}
		
		public void end() {
			Main.getInstance().getServer().getScheduler().cancelTask(taskId);
			Logger.info("DatabasesHandler: Migrated " + this.migratedAmount + "/" + this.amount);
		}
		
	}
}
