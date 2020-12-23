package me.kryniowesegryderiusz.KGenerators.GeneratorsManagement;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.Files;
import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

public abstract class Remove {
	
	public static void removeGenerator(GeneratorLocation gLocation, Location location, boolean drop) {
		final ItemStack air = XMaterial.AIR.parseItem();
		
		Generator generator = gLocation.getGenerator();
		
		Main.generatorsLocations.remove(location);
		Files.removeGeneratorFromFile(location);
		PerPlayerGenerators.removeGeneratorFromPlayer(gLocation.getOwner(), gLocation.getGeneratorId());
		
		if (drop) {
			location.getWorld().dropItem(location, generator.getGeneratorItem());
		}
		
		Main.getBlocksUtils().setBlock(location, air);
		
		if (generator.getType().equals("double")) {
			Location aLocation = location.clone().add(0,1,0);
			ItemStack m = Main.getBlocksUtils().getItemStackByBlock(aLocation.getBlock());
			if (m.equals(generator.getPlaceholder()) || generator.getChances().containsKey(m)) {
				Main.getBlocksUtils().setBlock(aLocation, air);
			}
		}
	}
}
