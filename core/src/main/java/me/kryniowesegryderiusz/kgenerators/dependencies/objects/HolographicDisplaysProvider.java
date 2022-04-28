package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import org.bukkit.plugin.Plugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class HolographicDisplaysProvider implements IHologramProvider {
	public void createHologram(GeneratorLocation gLocation) {
		Hologram hologram = HologramsAPI.createHologram((Plugin)Main.getInstance(), gLocation.getHologramLocation());
		for (String s : Main.getHolograms().getHologramLines(gLocation))
			hologram.appendTextLine(s); 
	}
	
	public void updateHologramLine(GeneratorLocation gLocation, int lineNr, String line) {
		for (Hologram hologram : HologramsAPI.getHolograms((Plugin)Main.getInstance())) {
			if (hologram.getLocation().equals(gLocation.getHologramLocation())) {
				hologram.removeLine(lineNr);
				hologram.insertTextLine(lineNr, line);
			} 
		} 
	}
	
	public void removeHologram(GeneratorLocation gLocation) {
		for (Hologram hologram : HologramsAPI.getHolograms((Plugin)Main.getInstance())) {
			if (hologram.getLocation().equals(gLocation.getHologramLocation()))
				hologram.delete(); 
		} 
	}
}
