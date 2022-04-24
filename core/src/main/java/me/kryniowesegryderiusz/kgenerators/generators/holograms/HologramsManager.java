package me.kryniowesegryderiusz.kgenerators.generators.holograms;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.DecentHologramsProvider;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.HolographicDisplaysProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.HologramText;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class HologramsManager {
	
	private IHologramProvider hologramProvider;
	
	private ArrayList<GeneratorLocation> holograms = new ArrayList<>();
	
	public HologramsManager() {
		Bukkit.getScheduler().runTask((Plugin)Main.getInstance(), () -> {
			
			if (Main.getDependencies().isEnabled(Dependency.DECENT_HOLOGRAMS)) {
				hologramProvider = new DecentHologramsProvider();
				Logger.info("Holograms: Enabling DecentHologramsProvider");
			} else if (Main.getDependencies().isEnabled(Dependency.HOLOGRAPHIC_DISPLAYS)) {
				hologramProvider = new HolographicDisplaysProvider();
				Logger.info("Holograms: Enabling HolographicDisplaysProvider");
			}
			
			Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
				public void run() {
					if (hologramProvider == null)
						return; 
					ArrayList<GeneratorLocation> toRemove = new ArrayList<>();
					for (GeneratorLocation gLocation : holograms) {
						if (Main.getLocations().exists(gLocation.getLocation()) && Main.getSchedules().timeLeft(gLocation) > 0) {
							int lineNo = 0;
							for (String s : Lang.getHologramTextStorage().get(HologramText.REMAINING_TIME).getLines()) {
								if (s.contains("<time>")) {
									hologramProvider.updateHologramLine(gLocation.getHologramLocation(), lineNo, s.replaceAll("<time>", Main.getSchedules().timeLeftFormatted(gLocation)));
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
			},	0L, Main.getSettings().getHologramUpdateFrequency() * 1L);
		});
	}
	
	public void createHologram(GeneratorLocation gLocation) {
		if (hologramProvider == null)
			return; 
		if (gLocation == null)
			return; 
		hologramProvider.createHologram(gLocation.getHologramLocation(), getHologramLines(gLocation));
		if (!holograms.contains(gLocation))
			holograms.add(gLocation); 
	}
	
	public void removeHologram(GeneratorLocation gLocation) {
		if (hologramProvider == null)
			return; 
		hologramProvider.removeHologram(gLocation.getHologramLocation());
		if (holograms.contains(gLocation))
			holograms.remove(gLocation); 
	}
	
	private static ArrayList<String> getHologramLines(GeneratorLocation gLocation) {
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
