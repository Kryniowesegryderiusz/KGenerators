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

import me.kryniowesegryderiusz.KGenerators.Enums.EnumWGFlags;
import me.kryniowesegryderiusz.KGenerators.Logger;
import me.kryniowesegryderiusz.KGenerators.Main;

public class WorldGuardUtils_1_8 implements WorldGuardUtils {
	
	WorldGuardPlugin worldGuard = (WorldGuardPlugin) Main.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
	
	public static StateFlag PICK_UP_FLAG = new StateFlag(EnumWGFlags.PICK_UP.getFlagId(), EnumWGFlags.PICK_UP.getFlagDefault());
	public static StateFlag ONLY_GEN_BREAK_FLAG = new StateFlag(EnumWGFlags.ONLY_GEN_BREAK.getFlagId(), EnumWGFlags.ONLY_GEN_BREAK.getFlagDefault());

	@Override
	public boolean isWorldGuardHooked() {
		if (PICK_UP_FLAG == null || ONLY_GEN_BREAK_FLAG == null)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public void worldGuardFlagsAdd() {
		try {
			FlagRegistry registry = worldGuard.getFlagRegistry();
			
			for (EnumWGFlags eflag : EnumWGFlags.values())
			{		
				Logger.info("[KGenerators] Registering worldguard " + eflag.getFlagId() + " flag");
			    try {
			    	
			    	if (eflag == EnumWGFlags.PICK_UP) registry.register(PICK_UP_FLAG);
					if (eflag == EnumWGFlags.ONLY_GEN_BREAK) registry.register(ONLY_GEN_BREAK_FLAG);
			        
			    } catch (FlagConflictException e) {
			    	Logger.error("[KGenerators] !!! ERROR !!! WorldGuard FlagConflictException!");
			    	Flag<?> existing = registry.get(eflag.getFlagId());
			        if (existing instanceof StateFlag) {
			        	Logger.info("[KGenerators] !!! WARNING !!! Overriding flag!");
				    	if (eflag == EnumWGFlags.PICK_UP) PICK_UP_FLAG = (StateFlag) existing;
			    		if (eflag == EnumWGFlags.ONLY_GEN_BREAK) ONLY_GEN_BREAK_FLAG = (StateFlag) existing;
			        	
			        } else {
			            Logger.error("[KGenerators] !!! ERROR !!! WorldGuard flag overriding not possible! Types dont match!");
				    	if (eflag == EnumWGFlags.PICK_UP) PICK_UP_FLAG = null;
			    		if (eflag == EnumWGFlags.ONLY_GEN_BREAK) ONLY_GEN_BREAK_FLAG = null;
			        }
			    }
			}
		} catch (NoClassDefFoundError e) {
			Logger.error("[KGenerators] !!! ERROR !!! An error occured, while adding WorldGuard flags!");
			Logger.error("[KGenerators] !!! ERROR !!! WorldGuard is installed, but didnt load properly!");
			PICK_UP_FLAG = null;
			ONLY_GEN_BREAK_FLAG = null;
			//e.printStackTrace();
		}
	}

	@Override
	public boolean worldGuardFlagCheck(Location location, Player player, EnumWGFlags flag) {
		
		LocalPlayer localPlayer = worldGuard.wrapPlayer(player);
		RegionContainer container = worldGuard.getRegionContainer();
		RegionQuery query = container.createQuery();
		
		StateFlag stateFlag = null;
		if (flag == EnumWGFlags.PICK_UP) stateFlag = PICK_UP_FLAG;
		if (flag == EnumWGFlags.ONLY_GEN_BREAK) stateFlag = ONLY_GEN_BREAK_FLAG;
		
		if (query.testState(location, localPlayer, stateFlag)) 
		{
			return true;
		}
		return false;
	}
}
