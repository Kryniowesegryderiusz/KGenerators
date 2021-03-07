package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;

public class Generators {
	
	private static LinkedHashMap<String, Generator> generators = new LinkedHashMap<String, Generator>();
	
	public static void clear()
	{
		generators.clear();
	}
	
	public static void add(String id, Generator generator)
	{
		generators.put(id, generator);
	}
	
	@Nullable
	public static Generator get(String id)
	{
		if (!generators.containsKey(id)) return null;
		return generators.get(id);
	}
	
	@Nullable
	public static Generator get(ItemStack item)
	{
		for(Entry<String, Generator> entry : getEntrySet())
		{
			if (entry.getValue().getGeneratorItem().getItemMeta().equals(item.getItemMeta())) return entry.getValue();
		}
		return null;
	}
	
	public static Set<Entry<String, Generator>> getEntrySet()
	{
		return generators.entrySet();
	}
	
	public static boolean exists(String id)
	{
		if (generators.containsKey(id)) return true;
		return false;
	}
	
	/**
	 * Checks if generator item exists
	 * @param generatorId
	 * @param item
	 * @return generatorId of doubled recipe, otherwise null
	 */
	
	public static String exactGeneratorItemExists(String generatorId, ItemStack item)
	{

		for (Entry<String, Generator> entry : getEntrySet()) {
			if (entry.getValue().getGeneratorItem().equals(item)) 
				return entry.getKey();
		}
		return null;
	}
	
	public static int amount()
	{
		return generators.size();
	}
	
	public static int amount(GeneratorType type)
	{
		int amount = 0;
		for (Entry<String, Generator> g : getEntrySet())
		{
			if (g.getValue().getType() == type)
			{
				amount++;
			}
		}
		return amount;
	}
}
