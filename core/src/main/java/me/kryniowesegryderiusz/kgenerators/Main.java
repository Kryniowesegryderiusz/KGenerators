package me.kryniowesegryderiusz.kgenerators;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dev.lone.itemsadder.api.ItemsAdder;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.addons.Addons;
import me.kryniowesegryderiusz.kgenerators.addons.objects.Addon;
import me.kryniowesegryderiusz.kgenerators.api.events.PluginDisabledEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.PluginEnabledEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.PluginReloadEvent;
import me.kryniowesegryderiusz.kgenerators.data.DatabaseManager;
import me.kryniowesegryderiusz.kgenerators.dependencies.DependenciesManager;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.ItemsAdderHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.GeneratorsManager;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.HologramsManager;
import me.kryniowesegryderiusz.kgenerators.generators.locations.PlacedGeneratorsManager;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.players.PlayersManager;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.LimitsManager;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.RecipesManager;
import me.kryniowesegryderiusz.kgenerators.generators.schedules.SchedulesManager;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.UpgradesManager;
import me.kryniowesegryderiusz.kgenerators.gui.MenusManager;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.listeners.BlockBreakListener;
import me.kryniowesegryderiusz.kgenerators.listeners.BlockPistonListener;
import me.kryniowesegryderiusz.kgenerators.listeners.BlockPlaceListener;
import me.kryniowesegryderiusz.kgenerators.listeners.CraftItemListener;
import me.kryniowesegryderiusz.kgenerators.listeners.ExplosionListener;
import me.kryniowesegryderiusz.kgenerators.listeners.FurnaceSmeltListener;
import me.kryniowesegryderiusz.kgenerators.listeners.InventoryClickListener;
import me.kryniowesegryderiusz.kgenerators.listeners.LeavesDecayListener;
import me.kryniowesegryderiusz.kgenerators.listeners.PlayerInteractListener;
import me.kryniowesegryderiusz.kgenerators.listeners.PrepareItemCraftListener;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.multiversion.MultiVersionManager;
import me.kryniowesegryderiusz.kgenerators.settings.Settings;
import me.kryniowesegryderiusz.kgenerators.utils.FilesConverter;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Metrics;

public class Main extends JavaPlugin {

	@Getter private static Main instance;
	
	@Getter private static Settings settings;
	
	@Getter private static GeneratorsManager generators;
	@Getter private static HologramsManager holograms;
	@Getter private static PlacedGeneratorsManager placedGenerators;
	@Getter private static PlayersManager players = new PlayersManager();
	@Getter private static LimitsManager limits;
	@Getter private static RecipesManager recipes;
	@Getter private static SchedulesManager schedules;
	@Getter private static UpgradesManager upgrades;
	
	@Getter private static DatabaseManager databases;
	@Getter private static DependenciesManager dependencies = new DependenciesManager();
	@Getter private static MultiVersionManager multiVersion;
	@Getter private static MenusManager menus;
	
    @Override
    public void onEnable() {
    	
    	dependencies.onEnableDependenciesCheck();
    	
    	if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null && !ItemsAdder.areItemsLoaded()) {
    		Logger.warn("ItemsAdder is enabled, KGenerators loading postponed until IA is loaded");
    		this.getServer().getPluginManager().registerEvents(new ItemsAdderHook().new ItemsAdderListeners(), this);
    	} else {
    		Logger.warn("KGenerators will be loaded in post init check, check for more informations futher in this log");
    		this.getServer().getScheduler().runTask(instance, () -> {
    			Main.getInstance().enable();
    		});
    	}
    }

	@Override
    public void onLoad() {
    	instance = this;
    	
    	Logger.setup();
    	
		settings = new Settings();
		FilesConverter.updateConfig(settings);
    	
    	multiVersion = new MultiVersionManager();
    }
    
    @Override
    public void onDisable() {
    	Logger.info("Disabling KGenerators");
    	
    	instance.getServer().getPluginManager().callEvent(new PluginDisabledEvent());
    	
    	Logger.info("Saving " + Main.getPlacedGenerators().getLoadedGeneratorsAmount() + " running generators.");
    	if (placedGenerators != null)
    		placedGenerators.onDisable();

    	Logger.info("Safely closing menus.");
    	if (menus != null)
    		menus.closeAll();
    	
    	Logger.info("Safely shutting down database.");
    	if (databases != null && databases.getDb() != null)
    		databases.getDb().closeConnection();
    	
    	this.getServer().getScheduler().cancelTasks(this);
    }
    
    public void reload() {
    	Logger.info("Reload: KGenerators reload started");
    	settings = new Settings();
    	generators.reload();
    	players.clear();
    	upgrades.reload();
    	limits = new LimitsManager();
    	Lang.loadFromFiles();
    	this.getServer().getPluginManager().callEvent(new PluginReloadEvent());
    	Logger.info("Reload: KGenerators reloaded successfully");
    }
    
    public void enable() {
    	try {
    		
			/* Commands setup */
			this.getServer().getPluginCommand("kgenerators").setExecutor(new Commands());
			this.getServer().getPluginCommand("kgenerators").setTabCompleter(new CommandTabCompleter());
    		
			/* Dependencies check */
			dependencies.standardDependenciesCheck();
			
			/* Configs loader */
			generators = new GeneratorsManager();
			
			Lang.loadFromFiles();
			
			recipes = new RecipesManager();
			upgrades = new UpgradesManager();
			limits = new LimitsManager();
			
			/* Database setup */
			databases = new DatabaseManager(Main.getSettings().getDbType());
			databases.getDb().updateTable();
			
			/* Other systems starter */
			holograms = new HologramsManager();
			schedules = new SchedulesManager();
			menus = new MenusManager();
			
			/* Load from already loaded chunks */
			placedGenerators = new PlacedGeneratorsManager();
			
			/* Listeners setup */
			Logger.debugPluginLoad("MainManager: Loading listeners");
			this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
			this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
			this.getServer().getPluginManager().registerEvents(new CraftItemListener(), this);
			this.getServer().getPluginManager().registerEvents(new BlockPistonListener(), this);
			this.getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
			this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
			this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
			this.getServer().getPluginManager().registerEvents(new FurnaceSmeltListener(), this);
			this.getServer().getPluginManager().registerEvents(new PrepareItemCraftListener(), this);
			this.getServer().getPluginManager().registerEvents(new LeavesDecayListener(), this);
			//Chunk listeners were moved to PlacedGeneratorsManager
			
			/* 
			 * Metrix
			 */
			Logger.debugPluginLoad("MainManager: Loading Metrix");
			int pluginId = 7871;
			Metrics metrics = new Metrics(this, pluginId);
			metrics.addCustomChart(new Metrics.SingleLineChart("configured_generators", () -> Main.getGenerators().getAmount()));
			metrics.addCustomChart(new Metrics.SingleLineChart("database_generators", () -> Main.getDatabases().getDb().getGeneratorsAmount()));
			metrics.addCustomChart(new Metrics.SingleLineChart("loaded_generators", () -> Main.getPlacedGenerators().getLoadedGeneratorsAmount()));
			metrics.addCustomChart(new Metrics.SimplePie("database_type", () -> {return Main.getSettings().getDbType().toString();}));
			metrics.addCustomChart(new Metrics.AdvancedPie("features", new Callable<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> call() throws Exception {
		            Map<String, Integer> valueMap = new HashMap<>();
		            if (Main.getLimits().hasLimits()) valueMap.put("Limits", 1);
		            if (Main.getRecipes().hasRecipes()) valueMap.put("Recipes", 1);
		            if (Main.getUpgrades().hasUpgrades()) valueMap.put("Upgrades", 1);
		            if (Main.getSettings().isBlockDropToEq()) valueMap.put("DropToInventory", 1);
		            return valueMap;
				}
			}));
			metrics.addCustomChart(new Metrics.AdvancedPie("hooked_plugins", new Callable<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> call() throws Exception {
		            Map<String, Integer> valueMap = new HashMap<>();
		            if (Main.getDependencies() != null) {
		            	for (Dependency dep : Main.getDependencies().getDependencies()) {
		            		valueMap.put(dep.toString(), 1);
		            	}
		            }
		            return valueMap;
				}
			}));
	        metrics.addCustomChart(new Metrics.DrilldownPie("complex_plugin_version", () -> {
	            Map<String, Map<String, Integer>> map = new HashMap<>();
	            String[] sVersion = this.getDescription().getVersion().split("-");
	            Map<String, Integer> entry = new HashMap<>();
	            entry.put(sVersion[1], 1);
	            map.put(sVersion[0], entry);
	            return map;
	        }));
			metrics.addCustomChart(new Metrics.AdvancedPie("addons", new Callable<Map<String, Integer>>() {
				@Override
				public Map<String, Integer> call() throws Exception {
		            Map<String, Integer> valueMap = new HashMap<>();
		            for (Addon a : Addons.getAddons()) {
		            	valueMap.put(a.getName(), 1);
		            }
		            return valueMap;
				}
			}));
			
			Logger.info("MainManager: KGenerators loaded successfully");
			
			this.getServer().getPluginManager().callEvent(new PluginEnabledEvent());
	        
		} catch (Exception e) {
			Logger.error(e);
		}    	
    }
}