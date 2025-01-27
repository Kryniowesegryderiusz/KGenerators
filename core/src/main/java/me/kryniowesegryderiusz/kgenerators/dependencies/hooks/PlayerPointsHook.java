package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.PlayerPointsUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.UpgradesManager.UpgradesCostsManager;

public class PlayerPointsHook {
	
	private static PlayerPointsAPI ppAPI;
	
	public static void setupPlayerPoints() {
		if (Main.getInstance().getServer().getPluginManager().isPluginEnabled("PlayerPoints")) {
            ppAPI = PlayerPoints.getInstance().getAPI();
        }
	}
	
	public static void registerUpgradeCost(UpgradesCostsManager ucm) {
		ucm.registerUpgradeCost(PlayerPointsUpgradeCost.class);
	}
	
	public static int getPlayerPoints(Player p) {
		return ppAPI.look(p.getUniqueId());
	}

	public static void takePlayerPoints(Player p, int amount) {
		ppAPI.take(p.getUniqueId(), amount);
	}

}
