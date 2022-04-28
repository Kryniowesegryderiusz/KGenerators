package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import org.bukkit.Location;

import eu.decentsoftware.holograms.api.DHAPI;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class DecentHologramsProvider implements IHologramProvider {
	public void createHologram(GeneratorLocation gLocation) {
		if (DHAPI.getHologram(getHoloName(gLocation.getHologramLocation())) == null)
			DHAPI.createHologram(getHoloName(gLocation.getHologramLocation()), gLocation.getHologramLocation(), false, Main.getHolograms().getHologramLines(gLocation));
	}
	
	public void updateHologramLine(GeneratorLocation gLocation, int lineNr, String line) {
		this.createHologram(gLocation);
		DHAPI.setHologramLine(DHAPI.getHologram(getHoloName(gLocation.getHologramLocation())), lineNr, line);
	}
	
	public void removeHologram(GeneratorLocation gLocation) {
		if (DHAPI.getHologram(getHoloName(gLocation.getHologramLocation())) != null)
			DHAPI.removeHologram(getHoloName(gLocation.getHologramLocation()));
	}
	
	private String getHoloName(Location loc) {
		return "KGenerators_" + loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
	}
}
