package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.ArrayList;

import org.bukkit.Location;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;

public class DecentHologramsProvider implements IHologramProvider {
	public void createHologram(Location location, ArrayList<String> lines) {
		DHAPI.createHologram(getHoloName(location), location, false, lines);
	}
	
	public void updateHologramLine(Location location, int lineNr, String line) {
		Hologram hologram = DHAPI.getHologram(getHoloName(location));
		DHAPI.setHologramLine(hologram, lineNr, line);
	}
	
	public void removeHologram(Location location) {
		if (DHAPI.getHologram(getHoloName(location)) != null)
			DHAPI.removeHologram(getHoloName(location));
	}
	
	private String getHoloName(Location loc) {
		return "KGenerators_" + loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
	}
}
