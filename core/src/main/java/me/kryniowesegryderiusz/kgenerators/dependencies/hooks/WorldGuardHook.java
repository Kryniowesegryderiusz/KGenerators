package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.WGFlag;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;

public class WorldGuardHook {
	
	public static boolean isPlayerAllowedToMine(Player player, Location location) {
		if (Main.getDependencies().isEnabled(Dependency.WORLD_GUARD) 
				&& !player.hasPermission("kgenerators.bypass.worldguard")
				&& Main.getMultiVersion().getWorldGuardUtils().isWorldGuardFlagAllow(location, player, WGFlag.ONLY_GEN_BREAK)) {
			Lang.getMessageStorage().send(player, Message.GENERATORS_DIGGING_ONLY_GEN);
			return false;
		} else return true;
	}
	
	public static boolean isPlayerAllowedToPickUp(Player player, Location location) {
		if (Main.getDependencies().isEnabled(Dependency.WORLD_GUARD)) {
			return player.hasPermission("kgenerators.bypass.worldguard") 
					|| Main.getMultiVersion().getWorldGuardUtils().isWorldGuardFlagAllow(location, player, WGFlag.PICK_UP);
		} else return true;
	}
	
	public static boolean isPlayerAllowedToInteract(Player player, Location location) {
		if (Main.getDependencies().isEnabled(Dependency.WORLD_GUARD)) {
			return player.hasPermission("kgenerators.bypass.worldguard") 
					|| Main.getMultiVersion().getWorldGuardUtils().isWorldGuardFlagAllow(location, player, WGFlag.INTERACT);
		} else return true;
	}

}
