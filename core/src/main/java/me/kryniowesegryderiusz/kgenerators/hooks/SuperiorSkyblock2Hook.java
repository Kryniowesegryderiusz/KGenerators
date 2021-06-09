package me.kryniowesegryderiusz.kgenerators.hooks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;

public class SuperiorSkyblock2Hook implements Listener {
	
	public static void setup()
	{
		Main.getInstance().getServer().getPluginManager().registerEvents(new SuperiorSkyblock2Hook(), Main.getInstance());
	}
	
	public static void handleBlockPlace(Block block)
	{
		Island island = SuperiorSkyblockAPI.getGrid().getIslandAt(block.getLocation());
		  if (island != null) {
			  island.handleBlockPlace(block);
		  }
	}
	
	@EventHandler
	public void onDeleteEvent (IslandDisbandEvent e)
	{
		Location min = e.getIsland().getMinimum();
		Location max = e.getIsland().getMaximum();
		
		Logger.info("Detected SuperiorSkyblock2 removing island in world " + min.getWorld().getName() + " starting at " + min.getBlockX() + "," + min.getBlockZ() + " and ending at " + max.getBlockX()+","+max.getBlockZ());
		Locations.bulkRemoveGenerators(min.getWorld(), min.getBlockX(), 0, min.getBlockZ(), max.getBlockX(), min.getWorld().getMaxHeight(), max.getBlockZ(), false);

	}

}
