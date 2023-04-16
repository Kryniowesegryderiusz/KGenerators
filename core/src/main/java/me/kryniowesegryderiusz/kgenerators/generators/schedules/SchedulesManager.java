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
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.utils.objects.CustomBlockData;

public class SchedulesManager {
	
	private HashMap<GeneratorLocation, Schedule> schedules = new HashMap<GeneratorLocation, Schedule>();
	
	public SchedulesManager() {
		
		Logger.debugPluginLoad("SchedulesManager: Setting up manager");
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
		    @Override
		    public void run() {
				int freq = Main.getSettings().getGenerationCheckFrequency();
				
				ArrayList<GeneratorLocation> readyForRegeneration = new ArrayList<GeneratorLocation>();
				
				for (Entry<GeneratorLocation, Schedule> e : schedules.entrySet()) {
					e.getValue().decreaseDelay(freq);
					if (e.getValue().isReadyForRegeneration()) {
						readyForRegeneration.add(e.getKey());
					}
				}
				
				for (GeneratorLocation gl : readyForRegeneration) {
					remove(gl);
					if (Main.getPlacedGenerators().isLoaded(gl))
						gl.regenerateGenerator();
				}
		    }
		}, 0L, Main.getSettings().getGenerationCheckFrequency()*1L);
	}
	
	public int getAmount() {
		return this.schedules.size();
	}
	
	public boolean isScheduled(GeneratorLocation gLocation) {
		return schedules.containsKey(gLocation);
	}

	public void schedule(GeneratorLocation gLocation, boolean place) {
		
		Logger.debugSchedulesManager("SchedulesManager: Scheduling " + gLocation.toString() + "| place: " + place);
		
		if ((place && gLocation.getGenerator().isGenerateImmediatelyAfterPlace()) 
				|| gLocation.getGenerator().getDelay() <= 0) {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				gLocation.regenerateGenerator();
			});
		} else {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				this.generatePlaceholder(gLocation);
			});
			
			schedules.put(gLocation, new Schedule(gLocation.getGenerator().getDelay()));
			
			if (gLocation.getGenerator().isHologram())
				Main.getHolograms().createRemainingTimeHologram(gLocation);
		}
		
		Logger.debugSchedulesManager("SchedulesManager: Scheduling " + gLocation.getId() + " completed");
	}
	
	public Schedule getSchedule(GeneratorLocation gLocation) {
		return this.schedules.get(gLocation);
	}
	
	public void remove(GeneratorLocation gLocation) {
		Logger.debugSchedulesManager("SchedulesManager: Removing " + gLocation.toString());
		schedules.remove(gLocation);
		if (gLocation.getGenerator().isHologram())
			Main.getHolograms().removeHologram(gLocation);
		Logger.debugSchedulesManager("SchedulesManager: Removing " + gLocation.getId() + " completed");
	}
	
	/*
	 * Methods related to chunk management
	 */
	
	public void loadSchedule(GeneratorLocation gLocation) {

		Schedule schedule = Main.getDatabases().getDb().getSchedule(gLocation);
		Logger.debugSchedulesManager("SchedulesManager: Loading " + gLocation.toString() + "| isNull: " + (schedule == null));
		if (schedule == null) return;
		
		if (gLocation.getGenerator().isHologram())
			Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
				Main.getHolograms().createRemainingTimeHologram(gLocation);
			});
		
		Main.getDatabases().getDb().removeSchedule(gLocation);
		
		schedules.put(gLocation, schedule);
		Logger.debugSchedulesManager("SchedulesManager: Loading " + gLocation.getId() + " completed");
	}
	
	public void unloadSchedule(GeneratorLocation gl) {
		Schedule schedule = this.getSchedule(gl);
		Logger.debugSchedulesManager("SchedulesManager: Unloading " + gl.toString() + " | isNull: " + (schedule == null));
		if (schedule != null) {
			Main.getDatabases().getDb().addSchedule(gl, schedule);
			Main.getSchedules().remove(gl);
			Logger.debugSchedulesManager("SchedulesManager: Unloading " + gl.getId() + " completed");
		}
	}
	
	public void unloadAllSchedules() {
		for (GeneratorLocation gl : this.schedules.keySet()) {
			Main.getDatabases().getDb().addSchedule(gl, Main.getSchedules().getSchedule(gl));
			if (gl.getGenerator().isHologram())
				Main.getHolograms().removeHologram(gl);
		}
		this.schedules.clear();
	}
	
	/*
	 * Internal methods
	 */
	
	private void generatePlaceholder(GeneratorLocation gLocation) {	
		CustomBlockData placeholder = gLocation.getGenerator().getPlaceholder();
		if (placeholder != null)
			placeholder.setBlock(gLocation.getGeneratedBlockLocation());
	}
	
	/*
	 * Time left methods
	 */
	
	/**
	 * @param location Generator location
	 * @return left ticks or -1 if not exist
	 */
	public int timeLeft(GeneratorLocation gLocation) {
		if (schedules.containsKey(gLocation))
			return schedules.get(gLocation).getTimeLeft();
		else {
			Location aLocation = gLocation.getLocation().clone().add(0,1,0);
			GeneratorLocation agLocation;
			
			if (Main.getPlacedGenerators().getLoaded(aLocation) != null)
				return -1;
			else
				agLocation = Main.getPlacedGenerators().getLoaded(aLocation);
			
			if (schedules.containsKey(agLocation) && Main.getMultiVersion().getBlocksUtils().isAir(aLocation.getBlock()))
				return schedules.get(agLocation).getTimeLeft();
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

	/*
	 * Deprecated
	 */
	
	public void loadOldSchedulesFile() {
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
    	
    	ConfigurationSection mainSection = file.getConfigurationSection("");
		int amount = 0;
    	for(String generatorLocationString: mainSection.getKeys(false)) {
    		Location location = Main.getPlacedGenerators().stringToLocation(generatorLocationString);
    		GeneratorLocation gLocation = Main.getPlacedGenerators().getUnknown(location);
    		schedules.put(gLocation, new Schedule(file.getInt(generatorLocationString + ".delay")));
    		if (gLocation.getGenerator().isHologram()) Main.getHolograms().createRemainingTimeHologram(gLocation);
    		amount++;
    	}
    	Logger.info("Scheduled generators data file: Loaded " + String.valueOf(amount) + " scheduled generators");
    	f.delete();
	}
}
