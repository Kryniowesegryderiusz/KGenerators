package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WorldGuardUtils {
	boolean isWorldGuardHooked();
	boolean worldGuardCheck(Location location, Player player);
	void worldGuardFlagAdd();
}