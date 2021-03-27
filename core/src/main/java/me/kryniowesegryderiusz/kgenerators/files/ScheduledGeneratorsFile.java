package me.kryniowesegryderiusz.kgenerators.files;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.managers.Holograms;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;

public class ScheduledGeneratorsFile {
	
	public static void load()
	{
		File f = new File(Main.getInstance().getDataFolder()+"/data", "schedules.yml");
		
		if (!f.exists()){
    		return;
    	}
		
		Config file;
		
		try {
			file = ConfigManager.getConfig("schedules.yml", "/data", false, false);
			file.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error("Scheduled generators data file: Cant load scheduled generators file. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
		
		Logger.info("Scheduled generators data file: Scheduled generators are loaded in delayed init task! Informations about them are located further in this log!");
    	
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
        	ConfigurationSection mainSection = file.getConfigurationSection("");
			int amount = 0;
        	for(String generatorLocationString: mainSection.getKeys(false))
        	{
        		Location location = Locations.stringToLocation(generatorLocationString);
        		GeneratorLocation gLocation = Locations.get(location);
        		Schedules.insert(gLocation, file.getInt(generatorLocationString + ".delay"));
        		if (gLocation.getGenerator().isHologram()) Holograms.createHologram(gLocation);
        		amount++;
        	}
        	Logger.info("Scheduled generators data file: Loaded " + String.valueOf(amount) + " scheduled generators");
        	f.delete();
        });
	}
	
	public static void save()
	{
		Config file;

		try {
			file = ConfigManager.getConfig("schedules.yml", "/data", false, true);
			file.loadConfig();
		} catch (IOException | InvalidConfigurationException e1) {
			Logger.error(e1);
			return;
		}
			
		for(Entry<GeneratorLocation, Integer> e : Schedules.getSchedules().entrySet())
		{
			file.set(Locations.locationToString(e.getKey().getLocation()) + ".delay", e.getValue());
		}
		
		try {
			file.saveConfig();
		} catch (IOException e1) {
			Logger.error(e1);
		}
	}
}
