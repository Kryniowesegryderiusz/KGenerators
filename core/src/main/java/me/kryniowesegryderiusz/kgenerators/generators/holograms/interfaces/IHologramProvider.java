package me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public abstract class IHologramProvider {

	protected abstract void providerCreateHologram(GeneratorLocation gLocation, ArrayList<String> lines);

	protected abstract void providerUpdateHologram(GeneratorLocation gLocation, ArrayList<String> lines);

	protected abstract void providerRemoveHologram(GeneratorLocation gLocation);

	public void createHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				providerCreateHologram(gLocation, lines);
			});
		} else
			providerCreateHologram(gLocation, lines);
	}

	public void updateHologram(GeneratorLocation gLocation, ArrayList<String> lines) {
		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				providerUpdateHologram(gLocation, lines);
			});
		} else
			providerUpdateHologram(gLocation, lines);
	}

	public void removeHologram(GeneratorLocation gLocation) {
		if (!Bukkit.isPrimaryThread()) {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				providerRemoveHologram(gLocation);
			});
		} else
			providerRemoveHologram(gLocation);
	}
}
