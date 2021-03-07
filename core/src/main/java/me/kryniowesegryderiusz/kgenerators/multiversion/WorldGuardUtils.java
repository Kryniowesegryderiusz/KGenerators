package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Enums.EnumWGFlags;

public interface WorldGuardUtils {
	
	boolean isWorldGuardHooked();
	boolean worldGuardFlagCheck(Location location, Player player, EnumWGFlags flag);
	void worldGuardFlagsAdd();	
}