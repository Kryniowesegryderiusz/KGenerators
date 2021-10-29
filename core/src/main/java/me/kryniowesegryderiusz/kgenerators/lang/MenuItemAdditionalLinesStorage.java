package me.kryniowesegryderiusz.kgenerators.lang;

import java.util.HashMap;
import java.util.Map.Entry;

import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.classes.StringContent;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class MenuItemAdditionalLinesStorage {
	
	private HashMap<String, StringContent> lang = new HashMap<String, StringContent>();
	Config config;
	
	public MenuItemAdditionalLinesStorage(Config config)
	{
		this.config = config;
	}
	
	public <T extends Enum<T> & IMenuItemAdditionalLines> void register(Class<T> c)
	{
		for(T en : c.getEnumConstants())
        {
			lang.put(en.getKey(), new StringContent("additional-lines."+en.getKey(), this.config, en.getStringContent()));
        }
	}
	
	public void reload()
	{
		for (Entry<String, StringContent> e : lang.entrySet())
		{
			e.getValue().load("additional-lines."+e.getKey(), config, e.getValue());
		}
	}
	
	public <T extends Enum<T> & IMenuItemAdditionalLines> StringContent get(T m)
	{
		return lang.get(m.getKey()).clone();
	}

}
