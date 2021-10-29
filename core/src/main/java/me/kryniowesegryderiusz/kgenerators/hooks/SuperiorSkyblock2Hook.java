package me.kryniowesegryderiusz.kgenerators.hooks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.events.BlockStackEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;

public class SuperiorSkyblock2Hook implements Listener {
	
	public static enum Type
	{
		PICKUP_FLAG,
		USE_FLAG,
		OPEN_MENU_FLAG
	}
	
	static IslandPrivilege KGENERATORS_PICKUP_FLAG;
	static IslandPrivilege KGENERATORS_USE_FLAG;
	static IslandPrivilege KGENERATORS_OPEN_MENU_FLAG;
	
	static boolean initialised = false;

	
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
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onStackEvent(BlockStackEvent e)
	{
		if (Locations.exists(e.getBlock().getLocation()))
			e.setCancelled(true);
	}
	
	/*
	 * New flags
	 */
	
    @EventHandler
    public void onPluginInit(PluginInitializeEvent e){
    	
        IslandPrivilege.register("KGENERATORS_PICKUP_FLAG");
        KGENERATORS_PICKUP_FLAG = IslandPrivilege.getByName("KGENERATORS_PICKUP_FLAG");
        
        IslandPrivilege.register("KGENERATORS_USE_FLAG");
        KGENERATORS_USE_FLAG = IslandPrivilege.getByName("KGENERATORS_USE_FLAG");
        
        IslandPrivilege.register("KGENERATORS_OPEN_MENU_FLAG");
        KGENERATORS_OPEN_MENU_FLAG = IslandPrivilege.getByName("KGENERATORS_OPEN_MENU_FLAG");
        
        initialised = true;
        Logger.info("Dependencies: SuperiorSkyblock2 additional privilages set up");
        
    }

	public static boolean isAllowed(Player p, Type type)
	{
		if (!initialised || !Main.dependencies.contains(Dependency.SUPERIOR_SKYBLOCK_2) || p.hasPermission("kgenerators.bypass.superiorskyblock2"))	
			return true;
		
		Island island = SuperiorSkyblockAPI.getIslandAt(p.getLocation());
		
		if (island != null) {
			IslandPrivilege flag = null;
			if (type == Type.PICKUP_FLAG)
				flag = KGENERATORS_PICKUP_FLAG;
			else if (type == Type.USE_FLAG)
				flag = KGENERATORS_USE_FLAG;
			else if (type == Type.OPEN_MENU_FLAG)
				flag = KGENERATORS_OPEN_MENU_FLAG;
			
			if (!island.hasPermission(p, flag))
				return false;
		}
		return true;
	}
}
