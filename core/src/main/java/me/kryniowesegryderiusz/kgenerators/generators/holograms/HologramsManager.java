package me.kryniowesegryderiusz.kgenerators.generators.holograms;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Bukkit;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.CMIHologramsProvider;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.DecentHologramsProvider;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.HolographicDisplaysProvider;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.HologramText;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class HologramsManager {
	
	private IHologramProvider hologramProvider;
	
	private ArrayList<GeneratorLocation> holograms = new ArrayList<>();
	
	public HologramsManager() {
		if (Main.getDependencies().isEnabled(Dependency.DECENT_HOLOGRAMS)) {
			hologramProvider = new DecentHologramsProvider();
			Logger.info("Holograms: Enabling DecentHologramsProvider");
		} else if (Main.getDependencies().isEnabled(Dependency.HOLOGRAPHIC_DISPLAYS)) {
			hologramProvider = new HolographicDisplaysProvider();
			Logger.info("Holograms: Enabling HolographicDisplaysProvider");
		} else if (Main.getDependencies().isEnabled(Dependency.CMI_HOLOGRAMS)) {
			hologramProvider = new CMIHologramsProvider();
			Logger.info("Holograms: Enabling CMIHologramsProvider");
		} else {
        	for (Map.Entry<String, Generator> e : Main.getGenerators().getEntrySet()) {
				if ((e.getValue()).isHologram())
					Logger.warn("Holograms: Generator " + e.getKey() + " has enabled holograms, but hologram provider was not found! Holograms wont work!"); 
			}
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
			public void run() {
				if (hologramProvider == null)
					return; 
				ArrayList<GeneratorLocation> toRemove = new ArrayList<>();
				for (GeneratorLocation gLocation : holograms) {
					if (Main.getPlacedGenerators().isLoaded(gLocation) && Main.getSchedules().timeLeft(gLocation) > 0) {
						int lineNo = 0;
						for (String s : Lang.getHologramTextStorage().get(HologramText.REMAINING_TIME).getLines()) {
							if (s.contains("<time>")) {
								hologramProvider.updateHologramLine(gLocation, lineNo, s.replaceAll("<time>", Main.getSchedules().timeLeftFormatted(gLocation)));
								break;
							} 
							lineNo++;
						} 
						continue;
					} 
					hologramProvider.removeHologram(gLocation);
					toRemove.add(gLocation);
				} 
				holograms.removeAll(toRemove);
			}
		},	0L, Main.getSettings().getHologramUpdateFrequency() * 1L);
	}
	
	public void createHologram(GeneratorLocation gLocation) {
		
		if (hologramProvider == null)
			return; 
		if (gLocation == null)
			return; 
		hologramProvider.createHologram(gLocation);
		if (!holograms.contains(gLocation))
			holograms.add(gLocation); 
	}
	
	public void removeHologram(GeneratorLocation gLocation) {
		if (hologramProvider == null)
			return; 
		hologramProvider.removeHologram(gLocation);
		if (holograms.contains(gLocation))
			holograms.remove(gLocation); 
	}
	
	public ArrayList<String> getHologramLines(GeneratorLocation gLocation) {
		ArrayList<String> lines = new ArrayList<>();
		String time = Main.getSchedules().timeLeftFormatted(gLocation);
		for (String s : Lang.getHologramTextStorage().get(HologramText.REMAINING_TIME).getLines()) {
			if (s.contains("<time>"))
				s = s.replaceAll("<time>", time); 
			lines.add(s);
		} 
		return lines;
	}
}
