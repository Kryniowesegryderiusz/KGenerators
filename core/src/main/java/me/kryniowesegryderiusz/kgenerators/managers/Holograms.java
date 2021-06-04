package me.kryniowesegryderiusz.kgenerators.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.HologramText;

public class Holograms {
	
	public static void createHologram(GeneratorLocation gLocation)
	{
		if(!Main.dependencies.contains(Dependency.HOLOGRAPHIC_DISPLAYS)) return;
		
		if (gLocation == null) return;
		Hologram hologram = HologramsAPI.createHologram(Main.getInstance(), gLocation.getHologramLocation());
		
		String time = Schedules.timeLeftFormatted(gLocation);
		for (String s : Lang.getHologram(HologramText.REMAINING_TIME))
		{
			if (s.contains("<time>")) s = s.replaceAll("<time>", time);
			hologram.appendTextLine(s);
		}

	}
	
	static void everyFreq()
	{
		if(!Main.dependencies.contains(Dependency.HOLOGRAPHIC_DISPLAYS)) return;
		
		int line = -1;
		for (String s : Lang.getHologram(HologramText.REMAINING_TIME))
		{
			line++;
			if (s.contains("<time>")) break;
		}
		
		for (Hologram hologram : HologramsAPI.getHolograms(Main.getInstance()))
		{
			Location generatorLocation = hologram.getLocation().clone().getBlock().getLocation();
			if (Locations.exists(generatorLocation.add(0,-1,0)))
				updateHologram(line, hologram, Locations.get(generatorLocation));
			else if (Locations.exists(generatorLocation.add(0,-1,0)))
				updateHologram(line, hologram, Locations.get(generatorLocation));
			else if (Locations.exists(generatorLocation.add(0,-1,0)))
				updateHologram(line, hologram, Locations.get(generatorLocation));
			else
				hologram.delete();
		}
	}
	
	static void updateHologram(int line, Hologram hologram, GeneratorLocation gLocation)
	{		
		String time = Schedules.timeLeftFormatted(gLocation);
		
		if (time.equals(""))
		{
			hologram.delete();
			return;
		}
		if (line < hologram.size())
		{
			hologram.removeLine(line);
			hologram.insertTextLine(line, Lang.getHologram(HologramText.REMAINING_TIME).get(line).replaceAll("<time>", time));
		}
	}
	
	static void removeHologram(GeneratorLocation gLocation)
	{
		if(!Main.dependencies.contains(Dependency.HOLOGRAPHIC_DISPLAYS)) return;
		
		for (Hologram hologram : HologramsAPI.getHolograms(Main.getInstance()))
		{
			if(gLocation != null && hologram.getLocation().equals(gLocation.getHologramLocation()))
				hologram.delete();
		}
	}
	
	public static void setup()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
		    @Override
		    public void run() {
		    	everyFreq();
		    }
		}, 0L, Main.getSettings().getHologramUpdateFrequency()*1L);
	}
}