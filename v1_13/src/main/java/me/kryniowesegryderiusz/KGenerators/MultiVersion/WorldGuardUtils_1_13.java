package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

public class WorldGuardUtils_1_13 implements WorldGuardUtils {
	
	public static StateFlag PICK_UP_FLAG;
	
	@Override
	public boolean isWorldGuardHooked() {
		if (PICK_UP_FLAG == null) {
			return false;
		}
		else
		{
			return true;
		}
	}
	
	@Override
	public void worldGuardFlagAdd() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		
		if (registry == null) {
			return;
		}
		
		try {
			StateFlag flag = new StateFlag("kgenerators-pick-up", true);
			registry.register(flag);
			PICK_UP_FLAG = flag;
		}
		catch (FlagConflictException e)
		{
			System.out.println("[KGenerators] !!! ERROR !!! WorldGuard FlagConflictException!");
			Flag<?> existing = registry.get("kgenerators-pick-up");
	        if (existing instanceof StateFlag) {
	        	System.out.println("[KGenerators] !!! WARNING !!! Overriding flag!");
	        	PICK_UP_FLAG = (StateFlag) existing;
	        } else {
	            System.out.println("[KGenerators] !!! ERROR !!! WorldGuard flag overriding not possible! Types dont match!");
	        }
		}
	}

	@Override
	public boolean worldGuardCheck(Location location, Player player) {
		//returns if pick up is allow/deny
		
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		
		RegionManager regions = container.get(BukkitAdapter.adapt(location.getWorld()));
		ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.adapt(location).toVector().toBlockPoint());
		
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
		
		if (regionSet.testState(localPlayer, PICK_UP_FLAG)) 
		{
			return true;
		}
		return false;
	}
}
