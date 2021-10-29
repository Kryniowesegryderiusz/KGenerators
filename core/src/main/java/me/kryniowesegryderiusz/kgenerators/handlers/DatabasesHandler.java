package me.kryniowesegryderiusz.kgenerators.handlers;

import java.io.File;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.bukkit.Location;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IDatabase;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.files.FilesFunctions;
import me.kryniowesegryderiusz.kgenerators.files.PlacedGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;

public class DatabasesHandler {
	
	/**
	 * Gets proper database or null if OTHER
	 * @param dbType
	 * @return
	 */
	@Nullable public static IDatabase getDatabase(DatabaseType dbType)
	{
		switch (dbType)
		{
			case MYSQL:
				return new SQL(DatabaseType.MYSQL, Main.getSettings().getSqlConfig());
			case SQLITE:
				return new SQL(DatabaseType.SQLITE, Main.getSettings().getSqlConfig());
			case YAML:
				return new PlacedGeneratorsFile();
			default:
				Logger.error("DatabasesHandler: Could not return OTHER database, as not specified");
				return null;
		}
	}
	
	public static void changeTo(DatabaseType toDbType)
	{
		Logger.info("Converting database from " + Main.getSettings().getDbType().name() + " to " + toDbType.name()+". IT COULD TAKE A WHILE!");
		
		IDatabase newDb = getDatabase(toDbType);
		
		if (newDb == null || newDb.getConnection() == null)
		{
			Logger.error("Cannot change database to " + toDbType.toString() + "! Connection could not be initialised!");
			return;
		}
		
		for (Entry<Location, GeneratorLocation> e : Locations.getEntrySet())
		{
			newDb.savePlacedGenerator(e.getValue());
		}
		
		Main.getSettings().setDbType(toDbType);
		FilesFunctions.changeText(new File(Main.getInstance().getDataFolder(), "config.yml"), "  dbtype: ", "  dbtype: "+toDbType.name()+"  #Earlier: ");
		
		Logger.info("Succesfully converted database to " + toDbType.toString()+"!");
	}

}
