package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public interface IUpgradeCost {
	
	/**
	 * Loads cost from config
	 * @param config
	 * @param generatorId is config path for upgrade ex. "generatorId.cost"
	 * @return true if cost was found in config and loaded, false if this cost is not set up
	 * @throws CannnotLoadUpgradeException
	 */
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException;
	
	/**
	 * Checks if requirements are met
	 * @param player
	 * @param amount of generators beeing upgraded
	 * @return true if requirements are met
	 */
	public boolean checkRequirements(Player p, int amount);
	
	/**
	 * Takes requirements
	 * @param player
	 * @param amount of generators beeing upgraded
	 */
	public void takeRequirements(Player p, int amount);
	
	
	/**
	 * Gets lang formatted string for display reasons
	 * @return string with formatted cost
	 */
	public String getCostFormatted();

}
