package me.kryniowesegryderiusz.kgenerators.data.enums;

import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public enum DatabaseType {
	
	SQLITE,
	MYSQL,
	OTHER
	;
	
	public static class Functions
	{
		public static DatabaseType getTypeByString(String s)
		{
			for (DatabaseType type : DatabaseType.values())
			{
				if (type.toString().toLowerCase().equals(s.toLowerCase())) return type;
			}
			Logger.error("Config file: Database type " + s + " doesnt exist! Using SQLITE!");
			return DatabaseType.SQLITE;
		}
	}
}
