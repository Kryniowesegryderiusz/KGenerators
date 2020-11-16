package me.kryniowesegryderiusz.KGenerators;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

public abstract class GeneratorsManager {
	
	public static void removeGenerator(Generator generator, Location location, boolean drop) {
		final ItemStack air = XMaterial.AIR.parseItem();
		
		KGenerators.generatorsLocations.remove(location);
		GeneratorsFileManger.removeGeneratorFromFile(location);
		
		if (drop) {
			location.getWorld().dropItem(location, generator.getGeneratorItem());
		}
		
		KGenerators.getBlocksUtils().setBlock(location, air);
		
		if (generator.getType().equals("double")) {
			Location aboveBlockLocation = location.clone().add(0,1,0);
			ItemStack m = KGenerators.getBlocksUtils().getItemStackByBlock(aboveBlockLocation.getBlock());
			if (m.equals(generator.getPlaceholder()) || generator.getChances().containsKey(m)) {
				KGenerators.getBlocksUtils().setBlock(aboveBlockLocation, air);
			}
		}
	}
}
