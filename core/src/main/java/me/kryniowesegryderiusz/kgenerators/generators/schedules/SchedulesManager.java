package me.kryniowesegryderiusz.kgenerators.generators.schedules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;

public class SchedulesManager {
	
	private HashMap<GeneratorLocation, Integer> schedules = new HashMap<GeneratorLocation, Integer>();
	
	public SchedulesManager() {
		/*
		 * Schedule task initialiser
		 */
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
		    @Override
		    public void run() {
				int freq = Main.getSettings().getGenerationCheckFrequency();
				ArrayList<GeneratorLocation> toRemove = new ArrayList<GeneratorLocation>();
				for (Entry<GeneratorLocation, Integer> e : schedules.entrySet()) {
					e.setValue(e.getValue()-freq);
					if (e.getValue() <= 0) {
						if (Main.getLocations().stillExists(e.getKey()))
							e.getKey().regenerateGenerator();
						toRemove.add(e.getKey());
					}
				}
				
				for (GeneratorLocation l : toRemove) {
					if(schedules.get(l) <= 0)
						remove(l);
				}
		    }
		}, 0L, Main.getSettings().getGenerationCheckFrequency()*1L);
		
		/*
		 * Scheduled tasks loader
		 */
		
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
    	
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
        	ConfigurationSection mainSection = file.getConfigurationSection("");
			int amount = 0;
        	for(String generatorLocationString: mainSection.getKeys(false)) {
        		Location location = Main.getLocations().stringToLocation(generatorLocationString);
        		GeneratorLocation gLocation = Main.getLocations().get(location);
        		this.insert(gLocation, file.getInt(generatorLocationString + ".delay"));
        		if (gLocation.getGenerator().isHologram()) Main.getHolograms().createHologram(gLocation);
        		amount++;
        	}
        	Logger.info("Scheduled generators data file: Loaded " + String.valueOf(amount) + " scheduled generators");
        	f.delete();
        });
	}

	public void schedule(GeneratorLocation gLocation, boolean place) {
		
		if ((place && gLocation.getGenerator().isGenerateImmediatelyAfterPlace()) 
				|| gLocation.getGenerator().getDelay() <= 0) {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				gLocation.regenerateGenerator();
			});
		} else {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				this.generatePlaceholder(gLocation);
			});
			
			if (gLocation.getGenerator().isHologram())
				Main.getHolograms().createHologram(gLocation);
			
			schedules.put(gLocation, gLocation.getGenerator().getDelay());
		}
	}
	
	private void generatePlaceholder(GeneratorLocation gLocation) {	
		ItemStack placeholder = gLocation.getGenerator().getPlaceholder();
		if (placeholder != null)
			Main.getMultiVersion().getBlocksUtils().setBlock(gLocation.getGeneratedBlockLocation(), placeholder);
	}
	
	/**
	 * @param location Generator location
	 * @return left ticks or -1 if not exist
	 */
	public int timeLeft(GeneratorLocation gLocation)
	{
		if (schedules.containsKey(gLocation))
			return schedules.get(gLocation);
		else {
			Location aLocation = gLocation.getLocation().clone().add(0,1,0);
			GeneratorLocation agLocation;
			
			if (Main.getLocations().get(aLocation) == null)
				return -1;
			else
				agLocation = Main.getLocations().get(aLocation);
			
			if (schedules.containsKey(agLocation) && Main.getMultiVersion().getBlocksUtils().isAir(aLocation.getBlock()))
				return schedules.get(agLocation);
			else
				return -1;
		}
			
	}
	
	public String timeLeftFormatted(GeneratorLocation gLocation, boolean removeMs) {
		LinkedHashMap<Integer, Message> times = new LinkedHashMap<Integer, Message>();
		times.put(20*60*60*24, Message.COMMANDS_TIME_LEFT_FORMAT_DAY);
		times.put(20*60*60, Message.COMMANDS_TIME_LEFT_FORMAT_HOUR);
		times.put(20*60, Message.COMMANDS_TIME_LEFT_FORMAT_MIN);

		
		int delay = timeLeft(gLocation);
		if (delay <= 0) return "";
		
		String s = "";
		
		for (Entry<Integer, Message> e : times.entrySet()) {
			if (delay >= e.getKey()) {
				int ticks = e.getKey();
				int amount = Math.floorDiv(delay, ticks);
				delay -= ticks*amount;
				if (!s.equals("")) s += " ";
				s += String.valueOf(amount) + Lang.getMessageStorage().get(e.getValue(), false);
			}
		}
		if (!s.equals("")) s += " ";
		if (removeMs || gLocation.getGenerator().isSecondMultiple()) {
			int d = ((int) delay/20);
			s += String.valueOf(d);
		}
		else {
			double d = ((double) delay/20);
			s += String.valueOf(d);
		}
		
		s += Lang.getMessageStorage().get(Message.COMMANDS_TIME_LEFT_FORMAT_SEC, false);
		
		return s;
	}
	
	public String timeLeftFormatted(GeneratorLocation generatorLocation) {
		return timeLeftFormatted(generatorLocation, false);
	}
	
	public void insert(GeneratorLocation gLocation, Integer delay) {
		schedules.put(gLocation, delay);
	}
	
	public void remove(GeneratorLocation gLocation) {
		Main.getHolograms().removeHologram(gLocation);
		schedules.remove(gLocation);
	}
	
	public boolean isScheduled(GeneratorLocation gLocation) {
		return schedules.containsKey(gLocation);
	}
	
	public void saveToFile() {
		Config file;

		try {
			file = ConfigManager.getConfig("schedules.yml", "/data", false, true);
			file.loadConfig();
		} catch (IOException | InvalidConfigurationException e1) {
			Logger.error(e1);
			return;
		}
			
		for(Entry<GeneratorLocation, Integer> e : this.schedules.entrySet()) {
			file.set(Main.getLocations().locationToString(e.getKey().getLocation()) + ".delay", e.getValue());
		}
		
		try {
			file.saveConfig();
		} catch (IOException e1) {
			Logger.error(e1);
		}
	}

}
