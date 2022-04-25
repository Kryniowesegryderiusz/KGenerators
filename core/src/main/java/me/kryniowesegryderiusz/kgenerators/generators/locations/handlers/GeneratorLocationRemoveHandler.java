package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import java.util.HashMap;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.ItemsAdderHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class GeneratorLocationRemoveHandler {
	
	public void handle(GeneratorLocation gLocation, boolean drop, @Nullable Player toWho) {
		final ItemStack air = XMaterial.AIR.parseItem();
		Location location = gLocation.getLocation();
		Generator generator = gLocation.getGenerator();
		
		Main.getLocations().remove(gLocation);
		Main.getSchedules().remove(gLocation);
		Main.getDatabases().getDb().removePlacedGenerator(location);
		gLocation.getOwner().removeGeneratorFromPlayer(gLocation.getGenerator());
		
		if (drop) {
			if (toWho == null || !Main.getSettings().isPickUpToEq())
				location.getWorld().dropItem(location, generator.getGeneratorItem());
			else
			{
				HashMap<Integer, ItemStack> left = toWho.getInventory().addItem(generator.getGeneratorItem());
				if (!left.isEmpty())
					location.getWorld().dropItem(location, generator.getGeneratorItem());
			}
		}
		
		Main.getMultiVersion().getBlocksUtils().setBlock(location, air);
		Main.getMultiVersion().getBlocksUtils().setBlock(gLocation.getGeneratedBlockLocation(), air);
		ItemsAdderHook.handleGeneratorLocationRemove(gLocation);
	}
}
