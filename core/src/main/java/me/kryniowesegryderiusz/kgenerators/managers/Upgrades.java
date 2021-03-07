package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.HashMap;
import java.util.Map.Entry;

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
	
	/**
	 * Checks if generator could be obtained by upgrade
	 * @param generatorId
	 * @return
	 */
	public static boolean couldBeObtained(String generatorId)
	{
		for (Entry<String, Upgrade> e : upgrades.entrySet())
		{
			if (e.getValue().getNextGeneratorId().equals(generatorId))
				return true;
		}
		return false;
	}
	
	public static String getPreviousGeneratorId(String generatorId)
	{
		for (Entry<String, Upgrade> e : upgrades.entrySet())
		{
			if (e.getValue().getNextGeneratorId().equals(generatorId))
				return e.getKey();
		}
		return "";
	}
	
}
