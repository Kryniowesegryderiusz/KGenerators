package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.Limit;

public class Limits {
	
	private static LinkedHashMap<String, Limit> limits = new LinkedHashMap<String, Limit>();
	
	public static void add(Limit limit)
	{
		limits.put(limit.getId(), limit);
	}
	
	public static Limit get(String id)
	{
		return limits.get(id);
	}
	
	public static Set<Entry<String, Limit>> getEntrySet()
	{
		return limits.entrySet();
	}
	
	public static Collection<Limit> getValues()
	{
		return limits.values();
	}	
	
	/**
	 * Checks if any limit has only owner use in context of generator
	 * @param generator
	 * @return
	 */
	public static boolean isOnlyOwnerUse(Generator g)
	{
		for (Limit l : limits.values())
		{
			if(l.getGenerators().contains(g) && l.isOnlyOwnerUse())
				return true;
		}
		return false;
	}

	public static void clear() {
		limits.clear();
	}
	
}
