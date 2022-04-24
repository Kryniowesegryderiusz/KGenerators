package me.kryniowesegryderiusz.kgenerators.lang.storages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IMenuItemType;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class  MenuItemStorage {
	
	private HashMap<String, MenuItem> lang = new HashMap<String, MenuItem>();
	Config config;
	ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	
	public MenuItemStorage(Config config)
	{
		this.config = config;
	}
	
	public <T extends Enum<T> & IMenuItemType> void register(Class<T> c)
	{
		for(T en : c.getEnumConstants())
        {
			lang.put(en.getKey(), en.getMenuItem());
			en.getMenuItem().load(en.getKey(), config);
        }
		
		if(!classes.contains(c))
			classes.add(c);
	}
	
	public void reload()
	{
		for (Entry<String, MenuItem> e : lang.entrySet())
		{
			e.getValue().load(e.getKey(), config);
		}
	}
	
	public <T extends Enum<T> & IMenuItemType> MenuItem get(T m)
	{
		return lang.get(m.getKey()).clone();
	}
	
	public Collection<MenuItem> values()
	{
		return lang.values();
	}
	
	public <T extends Enum<T> & IMenuItemType> ArrayList<T> getAllEnums()
	{
		ArrayList<T> enums = new ArrayList<T>();
		for (Class c : this.classes)
		{
			Class<T> cl = c;
			for (T en : cl.getEnumConstants())
			{
				enums.add(en);
			}
		}
		return enums;
	}

}
