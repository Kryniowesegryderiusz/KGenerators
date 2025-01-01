package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.entity.Player;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;

public class FactionsUUIDHook {
	
	public static boolean isPlayerAllowedToMine(Player player, org.bukkit.Location location) {
		return canHere(player, location);
	}
	
	public static boolean isPlayerAllowedToPickUp(Player player, org.bukkit.Location location) {
		return canHere(player, location);
	}
	
	public static boolean isPlayerAllowedToInteract(Player player, org.bukkit.Location location) {
		return canHere(player, location);
	}
	
	public static boolean isPlayerAllowedToPlace(Player player, org.bukkit.Location location) {
		return canHere(player, location);
	}
	
	private static boolean canHere(Player p, org.bukkit.Location l) {
		
		if (!Main.getDependencies().isEnabled(Dependency.FACTIONSUUID))
			return true;
		
		if (p.hasPermission("kgenerators.bypass.factionsuuid"))
			return true;
        
		FLocation floc = new FLocation(l);
		Faction faction = Board.getInstance().getFactionAt(floc);
		if (faction == null)
			return true;
		
		FPlayer fplayer = FPlayers.getInstance().getByPlayer(p);
		
		if(faction.getFPlayers().contains(fplayer))
			return true;

		return false;
		
	}

}
