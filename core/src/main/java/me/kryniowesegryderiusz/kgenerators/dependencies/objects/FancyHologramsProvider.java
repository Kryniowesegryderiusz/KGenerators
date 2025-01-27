package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.ArrayList;
import java.util.Optional;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.FancyHologramsHook;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces.IHologramProvider;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class FancyHologramsProvider extends IHologramProvider {
	
	HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

	@Override
	public void providerCreateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		
		if (manager.getHologram(gLocation.getHologramUUID()).isPresent())
			return;
		
		TextHologramData hologramData = new TextHologramData(gLocation.getHologramUUID(), gLocation.getHologramLocation(0)); // or create BlockHologramData / ItemHologramData
		
		if (FancyHologramsHook.HOLOGRAM_COLOR != null)
			hologramData.setBackground(FancyHologramsHook.HOLOGRAM_COLOR);
		
		hologramData.setText(lines);
		
		hologramData.setPersistent(false);
		
		Hologram hologram = manager.create(hologramData);
		
		manager.addHologram(hologram);

	}

	@Override
	public void providerUpdateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		Optional<Hologram> ohologram = manager.getHologram(gLocation.getHologramUUID());
		if (ohologram.isPresent()) {
			TextHologramData hologramData = (TextHologramData) ohologram.get().getData();
			hologramData.setText(lines);
		} else {
			providerCreateHologram(gLocation, lines);
		}
	}

	@Override
	public void providerRemoveHologram(GeneratorLocation gLocation) {
		Optional<Hologram> ohologram = manager.getHologram(gLocation.getHologramUUID());
		if (ohologram.isPresent()) {
			manager.removeHologram(ohologram.get());
		}
	}
}
