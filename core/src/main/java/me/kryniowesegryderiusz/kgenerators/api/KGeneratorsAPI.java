package me.kryniowesegryderiusz.kgenerators.api;

import javax.annotation.Nullable;

import org.bukkit.Location;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.Upgrade;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.settings.Settings;

public class KGeneratorsAPI
{
	public KGeneratorsAPI() {}
	
	/**
	 * Get GeneratorLocation by location.
	 * It gives access to all generator-related methods
	 * 
	 * @param location Location
	 * @return GeneratorLocation or null if doesn't exist
	 */
	public static @Nullable GeneratorLocation getGeneratorLocation(Location location)
	{
		return Main.getLocations().get(location);
	}
	
	/**
	 * Registers and loads new upgrade cost. It should implement IUpgradeCost interface 
	 * Ex. {@link me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.UpgradeCostMoney} 
	 * @param class beeing registered
	 * @since 6.0.0
	 */
	public static <T extends IUpgradeCost> void registerUpgradeCost(Class<T> c)
	{
		Main.getUpgrades().getUpgradesCostsManager().registerUpgradeCost(c);
		Main.getUpgrades().reload();
		Logger.info("Upgrades: Registered and loaded new UpgradeCost " + c.getName());
	}
	
	/**
	 * Get upgrade by generatorId
	 * 
	 * @return Upgrade or null if generator doesnt have one
	 */
	public static Upgrade getUpgrade(String generatorId)
	{
		return Main.getUpgrades().getUpgrade(generatorId);
	}
	
	/**
	 * Get global settings
	 * 
	 * @return Settings interface
	 */
	public static Settings getGlobalSettings()
	{
		return Main.getSettings();
	}
	
}
