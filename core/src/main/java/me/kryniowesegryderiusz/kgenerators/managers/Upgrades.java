package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.HashMap;

import me.kryniowesegryderiusz.kgenerators.classes.Upgrade;

public class Upgrades {
	
	static HashMap<String, Upgrade> upgrades = new HashMap<String, Upgrade>();
	
	public static void addUpgrade(String generatorId, Upgrade upgrade)
	{
		upgrades.put(generatorId, upgrade);
	}
	
	public static Upgrade getUpgrade(String generatorId)
	{
		return upgrades.get(generatorId);
	}
	
	public static void clear()
	{
		upgrades.clear();
	}
	
}
