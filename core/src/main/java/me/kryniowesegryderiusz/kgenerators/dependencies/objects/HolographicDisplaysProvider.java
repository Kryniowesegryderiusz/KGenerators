package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class HolographicDisplaysProvider extends IHologramProvider {
	
	HashMap<String, Hologram> holograms = new HashMap<String, Hologram>();
	
	public void providerCreateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		Hologram hologram = HologramsAPI.createHologram((Plugin)Main.getInstance(), gLocation.getHologramLocation(lines.size()));
		for (String s : lines)
			hologram.appendTextLine(s); 
		holograms.put(gLocation.getHologramUUID(), hologram);
	}
	
	public void providerUpdateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		Hologram hologram = holograms.get(gLocation.getHologramUUID());
		if(hologram != null) {
			hologram.clearLines();
			for (String s : lines)
				hologram.appendTextLine(s); 
		}
	}
	
	public void providerRemoveHologram(GeneratorLocation gLocation) {
		
		Hologram hologram = holograms.get(gLocation.getHologramUUID());
		if(hologram != null) {
			holograms.remove(gLocation.getHologramUUID());
			hologram.delete();
		}
	}
}
