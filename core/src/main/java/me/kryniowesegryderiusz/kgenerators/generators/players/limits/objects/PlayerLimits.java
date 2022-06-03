package me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.kryniowesegryderiusz.kgenerators.Main;

public class PlayerLimits {

	private HashMap<Limit, Integer> adjustedPlaceLimits = new HashMap<Limit, Integer>();

	/**
	 * Creates limit map with limits adjusted by permissions
	 * 
	 * @param player
	 */
	public PlayerLimits(Player player) {

		/* Filling variables */
		for (Entry<String, Limit> e : Main.getLimits().getEntrySet()) {
			this.adjustedPlaceLimits.put(e.getValue(), e.getValue().getPlaceLimit());
		}

		ArrayList<Limit> bypassedLimits = new ArrayList<Limit>();

		/* Checking limits */
		player.recalculatePermissions();

		for (PermissionAttachmentInfo pai : player.getEffectivePermissions()) {
			String perm = pai.getPermission();

			for (Limit limit : Main.getLimits().getValues()) {
				if (perm.contains("kgenerators.placelimit." + limit.getId())) {
					String[] sPerm = perm.split("\\.");
					int amount;
					try {
						amount = Integer.parseInt(sPerm[3]);
						if (amount > this.adjustedPlaceLimits.get(limit)) {
							this.adjustedPlaceLimits.put(limit, amount);
						}
					} catch (NumberFormatException e1) {
					}
				}

				if (perm.contains("kgenerators.placelimit." + limit.getId() + ".bypass")) {
					bypassedLimits.add(limit);
				}
			}
		}

		/* Adjusting limits basing on bypass permissions */

		for (Limit limit : bypassedLimits) {
			this.adjustedPlaceLimits.put(limit, -1);
		}
	}

	public int getLimit(Limit limit) {
		return this.adjustedPlaceLimits.get(limit);
	}
}
