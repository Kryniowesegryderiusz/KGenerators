package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.ArrayList;
import java.util.Map;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.classes.DecentHologramsProvider;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.HolographicDisplaysProvider;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.HologramText;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Holograms {
	static IHologramProvider hologramProvider;
	
	static ArrayList<GeneratorLocation> holograms = new ArrayList<>();
	
	public static void createHologram(GeneratorLocation gLocation) {
		if (hologramProvider == null)
			return; 
		if (gLocation == null)
			return; 
		hologramProvider.createHologram(gLocation.getHologramLocation(), getHologramLines(gLocation));
		if (!holograms.contains(gLocation))
			holograms.add(gLocation); 
	}
	
	static void everyFreq() {
		if (hologramProvider == null)
			return; 
		ArrayList<GeneratorLocation> toRemove = new ArrayList<>();
		for (GeneratorLocation gLocation : holograms) {
			if (Locations.exists(gLocation.getLocation()) && Schedules.timeLeft(gLocation) > 0) {
				int lineNo = 0;
				for (String s : Lang.getHologramTextStorage().get(HologramText.REMAINING_TIME).getLines()) {
					if (s.contains("<time>")) {
						hologramProvider.updateHologramLine(gLocation.getHologramLocation(), lineNo, s.replaceAll("<time>", Schedules.timeLeftFormatted(gLocation)));
						break;
					} 
					lineNo++;
				} 
				continue;
			} 
			hologramProvider.removeHologram(gLocation.getHologramLocation());
			toRemove.add(gLocation);
		} 
		holograms.removeAll(toRemove);
	}
	
	static void removeHologram(GeneratorLocation gLocation) {
		if (hologramProvider == null)
			return; 
		hologramProvider.removeHologram(gLocation.getHologramLocation());
		if (holograms.contains(gLocation))
			holograms.remove(gLocation); 
	}
	
	public static void setup() {
		Bukkit.getScheduler().runTask((Plugin)Main.getInstance(), () -> {
			
			if (Main.getDependencies().contains(Dependency.DECENT_HOLOGRAMS)) {
				hologramProvider = new DecentHologramsProvider();
				Logger.info("Holograms: Enabling DecentHologramsProvider");
			} else if (Main.getDependencies().contains(Dependency.HOLOGRAPHIC_DISPLAYS)) {
				hologramProvider = new HolographicDisplaysProvider();
				Logger.info("Holograms: Enabling HolographicDisplaysProvider");
			}
			
			Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
				public void run() {
					Holograms.everyFreq();
				}
			},	0L, Main.getSettings().getHologramUpdateFrequency() * 1L);
		});
	}
	
	private static ArrayList<String> getHologramLines(GeneratorLocation gLocation) {
		ArrayList<String> lines = new ArrayList<>();
		String time = Schedules.timeLeftFormatted(gLocation);
		for (String s : Lang.getHologramTextStorage().get(HologramText.REMAINING_TIME).getLines()) {
			if (s.contains("<time>"))
				s = s.replaceAll("<time>", time); 
			lines.add(s);
		} 
		return lines;
	}
}
