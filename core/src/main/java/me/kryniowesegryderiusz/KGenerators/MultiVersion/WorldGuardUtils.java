package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.KGenerators.EnumsManager.EnumWGFlags;

public interface WorldGuardUtils {
	
	boolean isWorldGuardHooked();
	boolean worldGuardFlagCheck(Location location, Player player, EnumWGFlags flag);
	void worldGuardFlagsAdd();	
}