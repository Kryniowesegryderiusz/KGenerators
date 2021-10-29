package me.kryniowesegryderiusz.kgenerators.enums;

import me.kryniowesegryderiusz.kgenerators.Logger;

public enum DatabaseType {
	
	YAML,
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
