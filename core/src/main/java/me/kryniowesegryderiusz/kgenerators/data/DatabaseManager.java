package me.kryniowesegryderiusz.kgenerators.data;

import java.io.File;

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
	
	@Getter private IDatabase db;
	
	public DatabaseManager(DatabaseType dbType) {
		this.db = this.getDatabase(dbType);
	}
	
	/**
	 * Gets proper database or null if OTHER
	 * @param dbType
	 * @return
	 */
	@Nullable public IDatabase getDatabase(DatabaseType dbType)
	{
		switch (dbType)
		{
			case MYSQL:
				return new SQLDatabase(DatabaseType.MYSQL, Main.getSettings().getSqlConfig());
			case SQLITE:
				return new SQLDatabase(DatabaseType.SQLITE, Main.getSettings().getSqlConfig());
			default:
				Logger.error("DatabasesHandler: Could not return OTHER database, as not specified");
				return null;
		}
	}
	
	public void changeTo(DatabaseType toDbType)
	{
		Logger.info("Converting database from " + Main.getSettings().getDbType().name() + " to " + toDbType.name()+". IT COULD TAKE A WHILE!");
		
		IDatabase newDb = getDatabase(toDbType);
		
		if (newDb == null || newDb.getConnection() == null) {
			Logger.error("Cannot change database to " + toDbType.toString() + "! Connection could not be initialised!");
			return;
		}
		
		for (GeneratorLocation gl : db.getGenerators()) {
			newDb.saveGenerator(gl);
		}
		
		Main.getSettings().setDbType(toDbType);
		FilesUtils.changeText(new File(Main.getInstance().getDataFolder(), "config.yml"), "  dbtype: ", "  dbtype: "+toDbType.name()+"  #Earlier: ");
		
		Logger.info("Succesfully converted database to " + toDbType.toString()+"!");
	}
}
