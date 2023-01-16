package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.ArrayList;

import eu.decentsoftware.holograms.api.DHAPI;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class DecentHologramsProvider extends IHologramProvider {
	
	public void providerCreateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		if (DHAPI.getHologram(gLocation.getHologramUUID()) == null)
			DHAPI.createHologram(gLocation.getHologramUUID(), gLocation.getHologramLocation(lines.size()), false, lines);
	}
	
	public void providerUpdateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		this.createHologram(gLocation, lines);
		DHAPI.setHologramLines(DHAPI.getHologram(gLocation.getHologramUUID()), lines);
	}
	
	public void providerRemoveHologram(GeneratorLocation gLocation) {
		if (DHAPI.getHologram(gLocation.getHologramUUID()) != null)
			DHAPI.removeHologram(gLocation.getHologramUUID());
	}
}
