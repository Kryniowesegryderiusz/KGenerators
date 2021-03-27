package me.kryniowesegryderiusz.kgenerators.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Players;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;

public class PlacedGeneratorsFile {
	
	static Config file;
	
	public static void loadPlacedGenerators()
	{
    	try {
    		file = ConfigManager.getConfig("placed.yml", "/data", false, true);
    		file.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error(e);
			return;
		}
    	
    	Logger.info("Placed generators file: Placed generators are loaded in delayed init task! Informations about them are located further in this log!");
    	
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
        	
        	Locations.clear();
        	Players.clear();
        	
        	Config config = file;
        	
        	int amount = 0;
        	int notLoaded = 0;
        	ArrayList<String> errNotSetId = new ArrayList<String>();
        	ArrayList<String> errNotExist = new ArrayList<String>();
    		ArrayList<String> errWorlds = new ArrayList<String>();
    		
        	ConfigurationSection mainSection = config.getConfigurationSection("");
        	for(String generatorLocationString: mainSection.getKeys(false))
        	{
        		
        		boolean err = false;
        		
        		String generatorID = config.getString(generatorLocationString+".generatorID");
        		
        		if (config.contains(generatorLocationString+".owner"))
        		{
        			Players.createPlayer(config.getString(generatorLocationString+".owner"));
        		}		
        		
        		GeneratorPlayer gPlayer = Players.getPlayer(config.getString(generatorLocationString+".owner"));
       		
        		String world = generatorLocationString.split(",")[0];    		
        		if (Main.getInstance().getServer().getWorld(world) == null)
        		{
        			if (!errWorlds.contains(world))
        				errWorlds.add(world);
        			err = true;
        		}
        		Location generatorLocation = Locations.stringToLocation(generatorLocationString);
        		
        		if (generatorID != null){
        			if (!Generators.exists(generatorID))
        			{
        				if (!errNotExist.contains(generatorID))
        					errNotExist.add(generatorID);
        				err = true;	
        			}
        		}
        		else
        		{
        			if (!errNotSetId.contains(generatorLocationString))
        				errNotSetId.add(generatorLocationString);
        			err = true;
        		}
        		
        		if (!err)
        		{
        			Locations.add(generatorID, generatorLocation, gPlayer);
        			gPlayer.addGeneratorToPlayer(generatorID);
    				amount++;
        		}
        		else
        		{
        			notLoaded++;
        		}
        	}
        	
        	if (notLoaded != 0)
        	{
        		Logger.error("Placed generators file: An error occured, while loading " + String.valueOf(notLoaded) + " placed generators!");
        		if (!errWorlds.isEmpty())
            	{
            		Logger.error("Placed generators file: Cant load worlds: " + errWorlds.toString());
            		String possible = "";
            		for (World world : Bukkit.getWorlds())
            			possible += world.getName()+", ";
            		Logger.error("Placed generators file: Possible worlds: " + possible);
            	}
        		if (!errNotExist.isEmpty())
            	{
            		Logger.error("Placed generators file: There are no generators with ids: " + errNotExist.toString());
            		String possible = "";
            		for (Entry<String, Generator> e : Generators.getEntrySet())
            			possible += e.getKey()+", ";
            		Logger.error("Placed generators file: Possible ids: " + possible);
            	}
        		if (!errNotSetId.isEmpty())
            	{
            		Logger.error("Placed generators file: Generator id is not set for: " + errNotSetId.toString());
            	}
        	}
        	
        	Logger.info("Placed generators file: Loaded properly " + amount + " placed generators");
        });
	}
	
	public static void saveGeneratorToFile(Location location, Player player, String generatorID)
	{	
		String sLocation = Locations.locationToString(location);
		file.set(sLocation+".generatorID", generatorID);
		if (player != null) file.set(sLocation+".owner", player.getName());
		
		try {
			file.saveConfig();
		} catch (IOException e) {
			Logger.error(e);
		}
	}
	
	public static void removeGeneratorFromFile(Location location)
	{
		String sLocation = Locations.locationToString(location);
		
		file.set(sLocation+".generatorID", null);
		file.set(sLocation+".owner", null);
		file.set(sLocation, null);
		
		try {
			file.saveConfig();
		} catch (IOException e) {
			Logger.error(e);
		}
	}

}
