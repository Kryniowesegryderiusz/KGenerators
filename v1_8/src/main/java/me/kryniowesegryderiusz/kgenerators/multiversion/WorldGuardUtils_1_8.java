package me.kryniowesegryderiusz.kgenerators.multiversion;

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

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumWGFlags;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.multiversion.WorldGuardUtils;

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
				Logger.info("WorldGuard: Registering " + eflag.getFlagId() + " flag");
			    try {
			    	
			    	if (eflag == EnumWGFlags.PICK_UP) registry.register(PICK_UP_FLAG);
					if (eflag == EnumWGFlags.ONLY_GEN_BREAK) registry.register(ONLY_GEN_BREAK_FLAG);
			        
			    } catch (FlagConflictException e) {
			    	Logger.error("WorldGuard: FlagConflictException!");
			    	Flag<?> existing = registry.get(eflag.getFlagId());
			        if (existing instanceof StateFlag) {
			        	Logger.warn("WorldGuard: Overriding flag!");
				    	if (eflag == EnumWGFlags.PICK_UP) PICK_UP_FLAG = (StateFlag) existing;
			    		if (eflag == EnumWGFlags.ONLY_GEN_BREAK) ONLY_GEN_BREAK_FLAG = (StateFlag) existing;
			        	
			        } else {
			            Logger.error("WorldGuard: Flag overriding not possible! Types dont match!");
				    	if (eflag == EnumWGFlags.PICK_UP) PICK_UP_FLAG = null;
			    		if (eflag == EnumWGFlags.ONLY_GEN_BREAK) ONLY_GEN_BREAK_FLAG = null;
			        }
			    }
			}
		} catch (NoClassDefFoundError e) {
			Logger.error("WorldGuard: An error occured, while adding WorldGuard flags!");
			Logger.error("WorldGuard: WorldGuard is installed, but didnt load properly!");
			PICK_UP_FLAG = null;
			ONLY_GEN_BREAK_FLAG = null;
			//e.printStackTrace();
		}
	}

	@Override
	public boolean worldGuardFlagCheck(GeneratorLocation gLocation, Player player, EnumWGFlags flag) {
		
		Location location = gLocation.getLocation();
		
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
