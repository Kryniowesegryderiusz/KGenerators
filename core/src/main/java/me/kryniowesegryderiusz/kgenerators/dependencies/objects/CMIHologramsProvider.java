package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.ArrayList;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;

import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class CMIHologramsProvider implements IHologramProvider {

	@SuppressWarnings("deprecation")
	@Override
	public void createHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		if (CMI.getInstance().getHologramManager().getHolograms().get(gLocation.getHologramUUID()) == null) {
			CMIHologram holo = new CMIHologram(gLocation.getHologramUUID(), gLocation.getHologramLocation(lines.size()));
			holo.setLines(lines);
			CMI.getInstance().getHologramManager().addHologram(holo);
			holo.update();
		} 
	}

	@Override
	public void updateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		this.createHologram(gLocation, lines);
		CMIHologram holo = CMI.getInstance().getHologramManager().getHolograms().get(gLocation.getHologramUUID());
		holo.setLines(lines);
		holo.update();
	}

	@Override
	public void removeHologram(GeneratorLocation gLocation) {
		CMIHologram holo = CMI.getInstance().getHologramManager().getHolograms().get(gLocation.getHologramUUID());
		if (holo != null)
			holo.remove();		
	}
}
