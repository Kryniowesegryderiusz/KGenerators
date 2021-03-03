package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Enums.EnumWGFlags;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;

public interface WorldGuardUtils {
	
	boolean isWorldGuardHooked();
	boolean worldGuardFlagCheck(GeneratorLocation gLocation, Player player, EnumWGFlags flag);
	void worldGuardFlagsAdd();	
}