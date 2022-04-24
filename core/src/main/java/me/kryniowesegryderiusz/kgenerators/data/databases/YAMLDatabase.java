package me.kryniowesegryderiusz.kgenerators.data.databases;

import java.io.IOException;
import java.sql.Connection;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.data.objects.PlacedGeneratorsLoader;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;

public class YAMLDatabase implements IDatabase {
	
	Config file;
	
	public YAMLDatabase()
	{
    	try {
    		this.file = ConfigManager.getConfig("placed.yml", "/data", false, true);
    		this.file.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			Logger.error(e);
			return;
		}
	}
	
	@Override
	public void loadGenerators() {    	
       	
    	Main.getLocations().clear();
    	Main.getPlayers().clear();
    	
    	Config config = file;
    	
    	PlacedGeneratorsLoader loader = new PlacedGeneratorsLoader();
    	
    	for(String generatorLocationString: config.getConfigurationSection("").getKeys(false))
    	{
    		String generatorID = config.getString(generatorLocationString+".generatorID");
    		String owner = "";
    		if (config.contains(generatorLocationString+".owner"))
    			owner = config.getString(generatorLocationString+".owner");
    		
    		loader.loadNext(generatorID, generatorLocationString, owner);
    	}
    	loader.finish();
	}
	
	@Override
	public void savePlacedGenerator(GeneratorLocation gl)
	{
		String sLocation = Main.getLocations().locationToString(gl.getLocation());
		file.set(sLocation+".generatorID", gl.getGenerator().getId());
		if (gl.getOwner().getOfflinePlayer() != null) file.set(sLocation+".owner", gl.getOwner().getOfflinePlayer().getName());
		
		try {
			file.saveConfig();
		} catch (IOException e) {
			Logger.error(e);
		}
	}
	
	@Override
	public void removePlacedGenerator(Location location)
	{
		String sLocation = Main.getLocations().locationToString(location);
		
		file.set(sLocation+".generatorID", null);
		file.set(sLocation+".owner", null);
		file.set(sLocation, null);
		
		try {
			file.saveConfig();
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public void closeConnection() {		
	}
}
