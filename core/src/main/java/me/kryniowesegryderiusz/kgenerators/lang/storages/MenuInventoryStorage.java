package me.kryniowesegryderiusz.kgenerators.lang.storages;

import java.util.HashMap;
import java.util.Map.Entry;

import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuInventory;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IMenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class MenuInventoryStorage {
	
	private HashMap<String, MenuInventory> lang = new HashMap<String, MenuInventory>();
	Config config;
	
	public MenuInventoryStorage(Config config)
	{
		this.config = config;
	}
	
	public <T extends Enum<T> & IMenuInventoryType> void register(Class<T> c)
	{
		for(T en : c.getEnumConstants())
        {
			lang.put(en.getKey(), en.getMenuInventory());
			en.getMenuInventory().load(en, config);
        }
	}
	
	public void reload()
	{
		for (Entry<String, MenuInventory> e : lang.entrySet())
		{
			e.getValue().load(e.getKey(), config);
		}
	}
	
	public <T extends Enum<T> & IMenuInventoryType> MenuInventory get(T m)
	{
		return lang.get(m.getKey());
	}

}
