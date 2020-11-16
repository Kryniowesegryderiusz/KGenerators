package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import me.kryniowesegryderiusz.KGenerators.KGenerators;

public class WorldGuardUtils_1_8 implements WorldGuardUtils {
	
	WorldGuardPlugin worldGuard = (WorldGuardPlugin) KGenerators.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
	
	public static StateFlag PICK_UP_FLAG = new StateFlag("kgenerators-pick-up", true);

	@Override
	public boolean isWorldGuardHooked() {
		if (PICK_UP_FLAG == null)
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean worldGuardCheck(Location location, Player player) {
		
		LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
		RegionContainer container = worldGuard.getRegionContainer();
		RegionQuery query = container.createQuery();
		
		if (query.testState(location, localPlayer, PICK_UP_FLAG)) 
		{
			return true;
		}
		return false;
	}

	@Override
	public void worldGuardFlagAdd() {
		FlagRegistry registry = worldGuard.getFlagRegistry();
		
	    try {
	        // register our flag with the registry
	        registry.register(PICK_UP_FLAG);
	    } catch (FlagConflictException e) {
	    	System.out.println("[KGenerators] !!! ERROR !!! WorldGuard FlagConflictException!");
	    	Flag<?> existing = registry.get("kgenerators-pick-up");
	        if (existing instanceof StateFlag) {
	        	System.out.println("[KGenerators] !!! WARNING !!! Overriding flag!");
	        	PICK_UP_FLAG = (StateFlag) existing;
	        } else {
	            System.out.println("[KGenerators] !!! ERROR !!! WorldGuard flag overriding not possible! Types dont match!");
	            PICK_UP_FLAG = null;
	        }
	    	
	    }
	}
}
