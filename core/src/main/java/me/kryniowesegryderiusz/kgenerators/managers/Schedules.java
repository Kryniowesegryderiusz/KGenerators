package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumDependency;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.GenerateBlock;

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
			int ticks = e.getValue();
			ticks-=freq;
			if (ticks <= 0)
			{
				GenerateBlock.generate(e.getKey());
				toRemove.add(e.getKey());
			}
			else
			{
				e.setValue(ticks);
			}
		}
		
		for (GeneratorLocation l : toRemove)
		{
			remove(l);
		}
	}

	public static void schedule(GeneratorLocation gLocation, boolean place) {
		
		GenerateBlock.generatePlaceholder(gLocation);
		
		if (!place && Main.dependencies.contains(EnumDependency.HolographicDisplays) && gLocation.getGenerator().isHologram())
		{
			Holograms.createHologram(gLocation);
		}
		
		schedules.put(gLocation, gLocation.getGenerator().getDelay());
		
	}
	
	public static void schedule(GeneratorLocation gLocation) {
		schedule(gLocation, false);
	}
	
	public static void scheduleNow(GeneratorLocation gLocation) {
		GenerateBlock.generate(gLocation);
	}
	
	/*
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
		LinkedHashMap<Integer, EnumMessage> times = new LinkedHashMap<Integer, EnumMessage>();
		times.put(20*60*60*24, EnumMessage.CommandsTimeLeftFormatDay);
		times.put(20*60*60, EnumMessage.CommandsTimeLeftFormatHour);
		times.put(20*60, EnumMessage.CommandsTimeLeftFormatMin);

		
		int delay = timeLeft(gLocation);
		if (delay <= 0) return "";
		
		String s = "";
		
		for (Entry<Integer, EnumMessage> e : times.entrySet())
		{
			if (delay >= e.getKey())
			{
				int ticks = e.getKey();
				int amount = Math.floorDiv(delay, ticks);
				delay -= ticks*amount;
				if (!s.equals("")) s += " ";
				s += String.valueOf(amount) + Lang.getMessage(e.getValue(), false, false);
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
		
		s += Lang.getMessage(EnumMessage.CommandsTimeLeftFormatSec, false, false);
		
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
