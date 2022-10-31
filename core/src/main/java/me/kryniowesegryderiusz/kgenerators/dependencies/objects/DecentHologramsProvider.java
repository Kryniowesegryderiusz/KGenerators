package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.ArrayList;

import eu.decentsoftware.holograms.api.DHAPI;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class DecentHologramsProvider implements IHologramProvider {
	public void createHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		if (DHAPI.getHologram(gLocation.getHologramUUID()) == null)
			DHAPI.createHologram(gLocation.getHologramUUID(), gLocation.getHologramLocation(lines.size()), false, lines);
	}
	
	public void updateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		this.createHologram(gLocation, lines);
		DHAPI.setHologramLines(DHAPI.getHologram(gLocation.getHologramUUID()), lines);
	}
	
	public void removeHologram(GeneratorLocation gLocation) {
		if (DHAPI.getHologram(gLocation.getHologramUUID()) != null)
			DHAPI.removeHologram(gLocation.getHologramUUID());
	}
}
