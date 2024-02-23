package me.kryniowesegryderiusz.kgenerators.generators.schedules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.objects.Schedule;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.objects.CustomBlockData;

public class SchedulesManager {
	
	@Getter private ConcurrentHashMap<GeneratorLocation, Schedule> schedules = new ConcurrentHashMap<GeneratorLocation, Schedule>();
	
	public SchedulesManager() {
		
		Logger.debugPluginLoad("SchedulesManager: Setting up manager");
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
		    @Override
		    public void run() {
		    	try {
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
						if (Main.getPlacedGenerators().isLoaded(gl)) {
							Logger.debugSchedulesManager("SchedulesManager: Regenerating " + gl.getId());
							gl.regenerateGenerator();
						}
					}
		    	} catch (Exception e) {
		    		Logger.error("SchedulesManager: An error occured at scheduler task");
		    		Logger.error(e);
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
			
			if (agLocation != null && schedules.containsKey(agLocation) && Main.getMultiVersion().getBlocksUtils().isAir(aLocation.getBlock()))
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

}
