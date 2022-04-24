package me.kryniowesegryderiusz.kgenerators.api;

import java.util.ArrayList;

import org.bukkit.plugin.Plugin;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Addons;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

/**
 * Create new instance of this class to register new addon in KGenerators
 */
public class Addon {
	
	String name;
	String version;
	@Getter
	ArrayList<String> configs;
	
	public Addon(Plugin plugin)
	{
		this.name = plugin.getName();
		this.version = plugin.getDescription().getVersion();
		Addons.register(this);
		Logger.info("Addons manager: Registered " + this.toString() + " addon");
	}
	
	public Addon(Plugin plugin, ArrayList<String> configs)
	{
		this.name = plugin.getName();
		this.version = plugin.getDescription().getVersion();
		this.configs = configs;
		Addons.register(this);
		Logger.info("Addons manager: Registered " + this.toString() + " addon");
	}
	
	public String toString()
	{
		return this.name + "-" + this.version;
	}
}
