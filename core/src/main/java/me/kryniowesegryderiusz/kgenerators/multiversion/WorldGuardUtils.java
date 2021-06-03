package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.enums.WGFlag;

public interface WorldGuardUtils {
	
	boolean isWorldGuardHooked();
	boolean worldGuardFlagCheck(Location location, Player player, WGFlag flag);
	void worldGuardFlagsAdd();	
}