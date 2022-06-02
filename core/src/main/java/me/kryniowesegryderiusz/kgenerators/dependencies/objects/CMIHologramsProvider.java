package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import org.bukkit.Location;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class CMIHologramsProvider implements IHologramProvider {

	@Override
	public void createHologram(GeneratorLocation gLocation) {
		if (CMI.getInstance().getHologramManager().getHolograms().get(this.getHoloName(gLocation.getHologramLocation())) == null) {
			CMIHologram holo = new CMIHologram(this.getHoloName(gLocation.getHologramLocation()), gLocation.getHologramLocation());
			holo.setLines(Main.getHolograms().getHologramLines(gLocation));
			CMI.getInstance().getHologramManager().addHologram(holo);
			holo.update();
		} 
	}

	@Override
	public void updateHologramLine(GeneratorLocation gLocation, int paramInt, String paramString) {
		this.createHologram(gLocation);
		CMIHologram holo = CMI.getInstance().getHologramManager().getHolograms().get(this.getHoloName(gLocation.getHologramLocation()));
		holo.setLine(paramInt, paramString);
		holo.refresh();
	}

	@Override
	public void removeHologram(GeneratorLocation gLocation) {
		CMIHologram holo = CMI.getInstance().getHologramManager().getHolograms().get(this.getHoloName(gLocation.getHologramLocation()));
		if (holo != null)
			holo.remove();		
	}
	
	private String getHoloName(Location loc) {
		return "kgenerators_" + loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
	}
}
