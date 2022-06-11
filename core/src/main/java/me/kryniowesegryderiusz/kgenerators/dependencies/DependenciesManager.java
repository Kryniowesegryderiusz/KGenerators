package me.kryniowesegryderiusz.kgenerators.dependencies;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.IridiumSkyblockHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.JetsMinionsHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.MinionsHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SlimefunHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.VaultHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.WildStackerHook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class DependenciesManager {
	
	@Getter private ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
	
	public void standardDependenciesCheck() {
		
		dependencies.clear();
    	
    	/*
    	 * Delayed checks
    	 */
		
    	if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
    		Logger.info("Dependencies: Detected plugin ItemsAdder. Hooking into it.");
    		dependencies.add(Dependency.ITEMS_ADDER);
    	}
		
    	if (Bukkit.getPluginManager().getPlugin("EcoItems") != null) {
    		Logger.info("Dependencies: Detected plugin EcoItems. Hooking into it.");
    		dependencies.add(Dependency.ECO_ITEMS);
    	}
    	
    	if (VaultHook.setupEconomy())
    	{
    		Logger.info("Dependencies: Detected Vault economy. Hooked into it.");
    		dependencies.add(Dependency.VAULT_ECONOMY);
    	}
    	else
    		Logger.warn("Dependencies: Vault economy was not found! Some features could not work!");
    	
    	if (VaultHook.setupPermissions())
    	{
    		Logger.info("Dependencies: Detected Vault permissions. Hooked into it.");
    		dependencies.add(Dependency.VAULT_PERMISSIONS);
    	}
    	else
    		Logger.warn("Dependencies: Vault permissions was not found! Some features could not work!");
		
    	if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
    		Logger.info("Dependencies: Detected plugin BentoBox. Hooking into it.");
    		BentoBoxHook.setup();
    		dependencies.add(Dependency.BENTO_BOX);
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("IridiumSkyblock")) {
    		Logger.info("Dependencies: Detected plugin IridiumSkyblock. Hooking into it.");
    		IridiumSkyblockHook.setup();
    		dependencies.add(Dependency.IRIDIUM_SKYBLOCK);
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("JetsMinions")) {
    		Logger.info("Dependencies: Detected plugin JetsMinions. Hooking into it.");
    		Main.getInstance().getServer().getPluginManager().registerEvents(new JetsMinionsHook(), Main.getInstance());
    		dependencies.add(Dependency.JETS_MINIONS);
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("Minions-Revamped")) {
    		Logger.info("Dependencies: Detected plugin Minions-Revamped. Hooking into it.");
    		Main.getInstance().getServer().getPluginManager().registerEvents(new MinionsHook(), Main.getInstance());
    		dependencies.add(Dependency.MINIONS);
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("Slimefun")) {
    		Logger.info("Dependencies: Detected plugin Slimefun. Hooking into it.");
    		Main.getInstance().getServer().getPluginManager().registerEvents(new SlimefunHook(), Main.getInstance());
    		dependencies.add(Dependency.SLIMEFUN);
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
    		Logger.info("Dependencies: Detected plugin WorldEdit. Hooking into it.");
    		dependencies.add(Dependency.WORLD_EDIT);
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
    		Logger.info("Dependencies: Detected plugin Oraxen. Hooking into it.");
    		dependencies.add(Dependency.ORAXEN);
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("WildStacker")) {
    		Logger.info("Dependencies: Detected plugin WildStacker. Hooking into it.");
    		dependencies.add(Dependency.WILDSTACKER);
    		WildStackerHook.setup();
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
    		Logger.info("Dependencies: Detected plugin PlaceholderAPI. Hooking into it.");
    		dependencies.add(Dependency.PLACEHOLDERAPI);
    	}
    	
    	if (Main.getMultiVersion().getWorldGuardUtils() != null && Main.getMultiVersion().getWorldGuardUtils().isWorldGuardHooked()) {
   			Logger.info("Dependencies: Detected plugin WorldGuard. Hooked into it.");
   			dependencies.add(Dependency.WORLD_GUARD);
    	}
    	else if (Main.getMultiVersion().getWorldGuardUtils() != null)
    	{
    		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
    			Logger.error("Dependencies: Detected plugin WorldGuard, but couldnt hook into it! Search console log above for errors!");
    		}
    	}
    	
        if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
        	Logger.info("Dependencies: Detected plugin HolographicDisplays. Hooked holograms into it.");
        	dependencies.add(Dependency.HOLOGRAPHIC_DISPLAYS);
        } else if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
        	Logger.info("Dependencies: Detected plugin DecentHolograms. Hooked holograms into it.");
        	dependencies.add(Dependency.DECENT_HOLOGRAMS);
        } else if (Bukkit.getPluginManager().isPluginEnabled("CMI")) {
        	Logger.info("Dependencies: Detected plugin CMI. Hooked holograms into it.");
        	dependencies.add(Dependency.CMI_HOLOGRAMS);
        }
	}
	
	public void onEnableDependenciesCheck() {
    	if (Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2") != null) {
    		Logger.info("Dependencies: Detected plugin SuperiorSkyblock2. Hooking into it.");
    		dependencies.add(Dependency.SUPERIOR_SKYBLOCK_2);
    		SuperiorSkyblock2Hook.setup();
    	}
	}
	
	public boolean isEnabled(Dependency dep) {
		return this.dependencies.contains(dep);
	}
}
