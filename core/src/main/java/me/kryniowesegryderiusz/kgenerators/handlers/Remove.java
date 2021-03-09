package me.kryniowesegryderiusz.kgenerators.handlers;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.files.PlacedGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class Remove {
	
	public static void removeGenerator(GeneratorLocation gLocation, boolean drop) {
		final ItemStack air = XMaterial.AIR.parseItem();
		Location location = gLocation.getLocation();
		Generator generator = gLocation.getGenerator();
		
		Locations.remove(location);
		Schedules.remove(gLocation);
		PlacedGeneratorsFile.removeGeneratorFromFile(location);
		gLocation.getOwner().removeGeneratorFromPlayer(gLocation.getGeneratorId());
		
		if (drop) {
			location.getWorld().dropItem(location, generator.getGeneratorItem());
		}
		
		Main.getBlocksUtils().setBlock(location, air);
		
		if (generator.getType() == GeneratorType.DOUBLE) {
			Location aLocation = location.clone().add(0,1,0);
			ItemStack m = Main.getBlocksUtils().getItemStackByBlock(aLocation.getBlock());
			if (m.equals(generator.getPlaceholder()) || generator.getChances().containsKey(m)) {
				Main.getBlocksUtils().setBlock(aLocation, air);
			}
		}
	}
	
	public static void removeGenerator (Location location, boolean drop)
	{
		removeGenerator(Locations.get(location), drop);
	}
}
