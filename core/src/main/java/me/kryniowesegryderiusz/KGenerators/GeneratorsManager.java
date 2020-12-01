package me.kryniowesegryderiusz.KGenerators;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

public abstract class GeneratorsManager {
	
	public static void removeGenerator(GeneratorLocation gLocation, Location location, boolean drop) {
		final ItemStack air = XMaterial.AIR.parseItem();
		
		Generator generator = gLocation.getGenerator();
		
		KGenerators.generatorsLocations.remove(location);
		GeneratorsFileManger.removeGeneratorFromFile(location);
		PerPlayerGenerators.removeGeneratorFromPlayer(gLocation.getOwner(), gLocation.getGeneratorId());
		
		if (drop) {
			location.getWorld().dropItem(location, generator.getGeneratorItem());
		}
		
		KGenerators.getBlocksUtils().setBlock(location, air);
		
		if (generator.getType().equals("double")) {
			Location aLocation = location.clone().add(0,1,0);
			ItemStack m = KGenerators.getBlocksUtils().getItemStackByBlock(aLocation.getBlock());
			if (m.equals(generator.getPlaceholder()) || generator.getChances().containsKey(m)) {
				KGenerators.getBlocksUtils().setBlock(aLocation, air);
			}
		}
	}
}
