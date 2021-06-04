package me.kryniowesegryderiusz.kgenerators.hooks;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.events.island.IslandDeleteChunksEvent;
import world.bentobox.bentobox.api.events.island.IslandDeletedEvent;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.database.objects.IslandDeletion;
import world.bentobox.bentobox.managers.RanksManager;

public class BentoBoxHook implements Listener {
	
	public static enum Type
	{
		PICKUP_FLAG,
		USE_FLAG,
		OPEN_MENU_FLAG
	}
	
	static Flag KGENERATORS_PICKUP_FLAG = null;
	static Flag KGENERATORS_USE_FLAG = null;
	static Flag KGENERATORS_OPEN_MENU_FLAG = null;
	
	public static void setup()
	{
		Main.getInstance().getServer().getPluginManager().registerEvents(new BentoBoxHook(), Main.getInstance());
		/*
		 * Set up new setting
		 */
		KGENERATORS_PICKUP_FLAG =
				   new Flag.Builder("KGENERATORS_PICKUP_FLAG", Material.DIAMOND_SHOVEL).
				   type(Flag.Type.PROTECTION).
				   defaultRank(RanksManager.MEMBER_RANK).
				   build();
		
		KGENERATORS_USE_FLAG =
				   new Flag.Builder("KGENERATORS_USE_FLAG", Material.DIAMOND_PICKAXE).
				   type(Flag.Type.PROTECTION).
				   defaultRank(RanksManager.MEMBER_RANK).
				   build();
		
		KGENERATORS_OPEN_MENU_FLAG =
				   new Flag.Builder("KGENERATORS_OPEN_MENU_FLAG", Material.ITEM_FRAME).
				   type(Flag.Type.PROTECTION).
				   defaultRank(RanksManager.MEMBER_RANK).
				   build();
		
		Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
			Config locale = new Config(new File("plugins/BentoBox/locales/BentoBox/en-US.yml"));
			try {
				locale.loadConfig();
			} catch (IOException | InvalidConfigurationException e) {
				Logger.error("BentoBox: Cannot load generator flag locale!");
				Logger.error(e);
			}
			if (locale != null)
			{
				if (!locale.contains("protection.flags.KGENERATORS_PICKUP_FLAG.name"))
					locale.set("protection.flags.KGENERATORS_PICKUP_FLAG.name", "Generator pick up");
				if (!locale.contains("protection.flags.KGENERATORS_PICKUP_FLAG.description"))
					locale.set("protection.flags.KGENERATORS_PICKUP_FLAG.description", "Toggle generator pick up");
				
				if (!locale.contains("protection.flags.KGENERATORS_USE_FLAG.name"))
					locale.set("protection.flags.KGENERATORS_USE_FLAG.name", "Generator use");
				if (!locale.contains("protection.flags.KGENERATORS_USE_FLAG.description"))
					locale.set("protection.flags.KGENERATORS_USE_FLAG.description", "Toggle breaking generator");
				
				if (!locale.contains("protection.flags.KGENERATORS_OPEN_MENU_FLAG.name"))
					locale.set("protection.flags.KGENERATORS_OPEN_MENU_FLAG.name", "Generators open gui");
				if (!locale.contains("protection.flags.KGENERATORS_OPEN_MENU_FLAG.description"))
					locale.set("protection.flags.KGENERATORS_OPEN_MENU_FLAG.description", "Toggle menu opening");
				try {
					locale.saveConfig();
					BentoBox.getInstance().getLocalesManager().reloadLanguages();
				} catch (IOException e) {
					Logger.error("BentoBox: Cannot add  generator flag to locale!");
					Logger.error(e);
				}
			}	
		});
		
		BentoBox.getInstance().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
			KGENERATORS_PICKUP_FLAG.addGameModeAddon(gameModeAddon);
			KGENERATORS_USE_FLAG.addGameModeAddon(gameModeAddon);
			KGENERATORS_OPEN_MENU_FLAG.addGameModeAddon(gameModeAddon);
		});
		
		BentoBox.getInstance().getFlagsManager().registerFlag(null, KGENERATORS_PICKUP_FLAG);
		BentoBox.getInstance().getFlagsManager().registerFlag(null, KGENERATORS_USE_FLAG);
		BentoBox.getInstance().getFlagsManager().registerFlag(null, KGENERATORS_OPEN_MENU_FLAG);
		
		Logger.info("BentoBox: Added settings flags");
	}
	
	public static boolean isAllowed(Player p, Type type)
	{
		Optional<Island> oisland = BentoBox.getInstance().getIslands().getIslandAt(p.getLocation());
		if(oisland.isPresent()) {
			
			Flag flag = null;
			if (type == Type.PICKUP_FLAG)
				flag = KGENERATORS_PICKUP_FLAG;
			else if (type == Type.USE_FLAG)
				flag = KGENERATORS_USE_FLAG;
			else if (type == Type.OPEN_MENU_FLAG)
				flag = KGENERATORS_OPEN_MENU_FLAG;
			
			if (!oisland.get().isAllowed(User.getInstance(p), flag))
				return false;
		}
		return true;
	}
	
	@EventHandler
	public void onDeleteEvent (IslandDeleteChunksEvent e)
	{
		IslandDeletion id = e.getDeletedIslandInfo();
		Locations.bulkRemoveGenerators(id.getWorld(), id.getMinX(), 0, id.getMinZ(), id.getMaxX(), id.getWorld().getMaxHeight(), id.getMaxZ(), false);
	}
}
