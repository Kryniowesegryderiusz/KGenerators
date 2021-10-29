package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMessage;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.Upgrade;

public class Upgrades {
	
	/*
	 * Upgrades manager
	 */
	
	static HashMap<String, Upgrade> upgrades = new HashMap<String, Upgrade>();
	
	public static void addUpgrade(String generatorId, Upgrade upgrade)
	{
		upgrades.put(generatorId, upgrade);
	}
	
	@Nullable public static Upgrade getUpgrade(String generatorId)
	{
		return upgrades.get(generatorId);
	}
	
	@Nullable public static Upgrade getUpgrade(ItemStack item)
	{
		ItemStack i = item.clone();
		i.setAmount(1);
		
		Generator g = Generators.get(item);
		if (g == null)
			return null;
		
		if (getUpgrade(g.getId()) == null)
			return null;
		return getUpgrade(g.getId());
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
	
	/*
	 * Upgrades costs manager
	 */
	
	private static ArrayList<Class<? extends IUpgradeCost>> upgradeCosts = new ArrayList<Class<? extends IUpgradeCost>>();
	
	public static <T extends IUpgradeCost> void registerUpgradeCost(Class<T> c)
	{
		if (!upgradeCosts.contains(c))
			upgradeCosts.add(c);
	}
	
	public static ArrayList<IUpgradeCost> getUpgradeCosts()
	{
		ArrayList<IUpgradeCost> newUpgradeCosts = new ArrayList<IUpgradeCost>();
		
		for (Class<? extends IUpgradeCost> c : upgradeCosts)
		{
			try {
				newUpgradeCosts.add(c.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				Logger.error("Upgrades: Cannot prepare upgrade cost list for " + c.getName());
				Logger.error(e);
			}
		}
		
		return newUpgradeCosts;
	}
}
