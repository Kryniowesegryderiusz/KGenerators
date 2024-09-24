package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.api.IslandDeleteEvent;
import com.iridium.iridiumskyblock.database.Island;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class IridiumSkyblockHook implements Listener {

	public static void setup() {
		Main.getInstance().getServer().getPluginManager().registerEvents(new IridiumSkyblockHook(), Main.getInstance());
	}

	@EventHandler
	public void onDeleteEvent(IslandDeleteEvent e) {
		islandDeletion(e.getIsland());
	}

	private void islandDeletion(Island is) {
		World[] worlds = { IridiumSkyblockAPI.getInstance().getWorld(),
				IridiumSkyblockAPI.getInstance().getNetherWorld(), IridiumSkyblockAPI.getInstance().getEndWorld() };

		for (World w : worlds) {

			Location min = is.getPosition1(w);
			Location max = is.getPosition2(w);

			Logger.info("Detected IridiumSkyblock removing island in world " + min.getWorld().getName()
					+ " starting at " + min.getBlockX() + "," + min.getBlockZ() + " and ending at " + max.getBlockX()
					+ "," + max.getBlockZ());
			Main.getPlacedGenerators().bulkRemoveGenerators(min.getWorld(), min.getBlockX(), 0, min.getBlockZ(),
					max.getBlockX(), min.getWorld().getMaxHeight(), max.getBlockZ(), false);
		}
	}

}
