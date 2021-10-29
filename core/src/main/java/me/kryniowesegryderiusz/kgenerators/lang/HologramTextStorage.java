package me.kryniowesegryderiusz.kgenerators.lang;

import java.util.HashMap;
import java.util.Map.Entry;

import me.kryniowesegryderiusz.kgenerators.api.interfaces.IHologramText;
import me.kryniowesegryderiusz.kgenerators.classes.StringContent;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class HologramTextStorage {
	
	private HashMap<String, StringContent> lang = new HashMap<String, StringContent>();
	Config config;
	
	public HologramTextStorage(Config config)
	{
		this.config = config;
	}
	
	public <T extends Enum<T> & IHologramText> void register(Class<T> c)
	{
		for(T en : c.getEnumConstants())
        {
			lang.put(en.getKey(), new StringContent("holograms."+en.getKey(), this.config, en.getStringContent()));
        }
	}
	
	public void reload()
	{
		for (Entry<String, StringContent> e : lang.entrySet())
		{
			e.getValue().load("holograms."+e.getKey(), config, e.getValue());
		}
	}
	
	public <T extends Enum<T> & IHologramText> StringContent get(T m)
	{
		return lang.get(m.getKey()).clone();
	}

}
