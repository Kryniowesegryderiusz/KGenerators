package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.GenerateBlock;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;

public class Schedules {
	
	@Getter
	private static HashMap<GeneratorLocation, Integer> schedules = new HashMap<GeneratorLocation, Integer>();
	
	public static void setup()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
		    @Override
		    public void run() {
		    	everyFreq();
		    }
		}, 0L, Main.getSettings().getGenerationCheckFrequency()*1L);
	}
	
	private static void everyFreq()
	{
		int freq = Main.getSettings().getGenerationCheckFrequency();
		ArrayList<GeneratorLocation> toRemove = new ArrayList<GeneratorLocation>();
		for (Entry<GeneratorLocation, Integer> e : schedules.entrySet())
		{
			e.setValue(e.getValue()-freq);
			if (e.getValue() <= 0)
			{
				GenerateBlock.generate(e.getKey());
				toRemove.add(e.getKey());
			}
		}
		
		for (GeneratorLocation l : toRemove)
		{
			if(schedules.get(l) <= 0)
				remove(l);
		}
	}

	public static void schedule(GeneratorLocation gLocation, boolean createHologram) {
		
		if (gLocation.getGenerator().getDelay() <= 0)
		{
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				scheduleNow(gLocation);
			});
		}
		else
		{
			GenerateBlock.generatePlaceholder(gLocation);
			
			if (createHologram && gLocation.getGenerator().isHologram())
			{
				Holograms.createHologram(gLocation);
			}
			
			schedules.put(gLocation, gLocation.getGenerator().getDelay());
		}
	}
	
	public static void schedule(GeneratorLocation gLocation) {
		schedule(gLocation, true);
	}
	
	public static void scheduleNow(GeneratorLocation gLocation) {
		GenerateBlock.generate(gLocation);
	}
	
	/**
	 * @param location Generator location
	 * @return left ticks or -1 if not exist
	 */
	public static int timeLeft(GeneratorLocation gLocation)
	{
		if (schedules.containsKey(gLocation))
			return schedules.get(gLocation);
		else
		{
			Location aLocation = gLocation.getLocation().clone().add(0,1,0);
			GeneratorLocation agLocation;
			
			if (!Locations.exists(aLocation))
				return -1;
			else
				agLocation = Locations.get(aLocation);
			
			if (schedules.containsKey(agLocation) && Main.getBlocksUtils().isAir(aLocation.getBlock()))
				return schedules.get(agLocation);
			else
				return -1;
		}
			
	}
	
	public static String timeLeftFormatted(GeneratorLocation gLocation, boolean removeMs)
	{
		LinkedHashMap<Integer, Message> times = new LinkedHashMap<Integer, Message>();
		times.put(20*60*60*24, Message.COMMANDS_TIME_LEFT_FORMAT_DAY);
		times.put(20*60*60, Message.COMMANDS_TIME_LEFT_FORMAT_HOUR);
		times.put(20*60, Message.COMMANDS_TIME_LEFT_FORMAT_MIN);

		
		int delay = timeLeft(gLocation);
		if (delay <= 0) return "";
		
		String s = "";
		
		for (Entry<Integer, Message> e : times.entrySet())
		{
			if (delay >= e.getKey())
			{
				int ticks = e.getKey();
				int amount = Math.floorDiv(delay, ticks);
				delay -= ticks*amount;
				if (!s.equals("")) s += " ";
				s += String.valueOf(amount) + Lang.getMessageStorage().get(e.getValue(), false);
			}
		}
		if (!s.equals("")) s += " ";
		if (removeMs || gLocation.getGenerator().isSecondMultiple())
		{
			int d = ((int) delay/20);
			s += String.valueOf(d);
		}
		else
		{
			double d = ((double) delay/20);
			s += String.valueOf(d);
		}
		
		s += Lang.getMessageStorage().get(Message.COMMANDS_TIME_LEFT_FORMAT_SEC, false);
		
		return s;
	}
	
	public static String timeLeftFormatted(GeneratorLocation generatorLocation)
	{
		return timeLeftFormatted(generatorLocation, false);
	}
	
	public static void insert(GeneratorLocation gLocation, Integer delay)
	{
		schedules.put(gLocation, delay);
	}
	
	public static void remove(GeneratorLocation gLocation)
	{
		Holograms.removeHologram(gLocation);
		schedules.remove(gLocation);
	}
	

}
