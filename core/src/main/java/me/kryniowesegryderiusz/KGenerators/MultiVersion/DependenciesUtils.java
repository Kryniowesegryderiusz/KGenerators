package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface DependenciesUtils {
	boolean isWorldGuardHooked();
	boolean worldGuardCheck(Location location, Player player);
	void worldGuardFlagAdd();
}