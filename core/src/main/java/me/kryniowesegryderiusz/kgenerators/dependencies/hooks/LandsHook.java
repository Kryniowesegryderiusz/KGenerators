package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.entity.Player;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;

public class LandsHook {
	
	private static LandsIntegration api;
	
	public static boolean isPlayerAllowedToMine(Player player, org.bukkit.Location location) {
		if (!Main.getDependencies().isEnabled(Dependency.LANDS))
			return true;
		return canHere(player, location, Flags.BLOCK_BREAK);
	}
	
	public static boolean isPlayerAllowedToPickUp(Player player, org.bukkit.Location location) {
		if (!Main.getDependencies().isEnabled(Dependency.LANDS))
			return true;
		return canHere(player, location, Flags.INTERACT_GENERAL);
	}
	
	public static boolean isPlayerAllowedToInteract(Player player, org.bukkit.Location location) {
		if (!Main.getDependencies().isEnabled(Dependency.LANDS))
			return true;
		return canHere(player, location, Flags.INTERACT_GENERAL);
	}
	
	public static boolean isPlayerAllowedToPlace(Player player, org.bukkit.Location location) {
		if (!Main.getDependencies().isEnabled(Dependency.LANDS))
			return true;
		return canHere(player, location,Flags.BLOCK_PLACE);
	}
	
	private static boolean canHere(Player p, org.bukkit.Location l, RoleFlag flag) {
		
		if (api == null)
			api = LandsIntegration.of(Main.getInstance());
		
		if (!Main.getDependencies().isEnabled(Dependency.LANDS))
			return true;
		
		if (p.hasPermission("kgenerators.bypass.lands"))
			return true;   
        
		LandWorld world = api.getWorld(l.getWorld());
		if (world != null) { // Lands is enabled in this world
		    if (!world.hasRoleFlag(api.getLandPlayer(p.getUniqueId()), l, flag, null, false)) {
		       return false;
		    }
		}
		return true;
		
	}

}
