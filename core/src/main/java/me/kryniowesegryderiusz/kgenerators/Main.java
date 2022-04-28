package me.kryniowesegryderiusz.kgenerators;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dev.lone.itemsadder.api.ItemsAdder;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.events.ReloadEvent;
import me.kryniowesegryderiusz.kgenerators.data.DatabaseManager;
import me.kryniowesegryderiusz.kgenerators.dependencies.DependenciesManager;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.ItemsAdderHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.GeneratorsManager;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.holograms.HologramsManager;
import me.kryniowesegryderiusz.kgenerators.generators.locations.GeneratorLocationsManager;
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
	@Getter private static GeneratorLocationsManager locations = new GeneratorLocationsManager();
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
    		this.getServer().getPluginManager().registerEvents(new ItemsAdderHook().new ItemsAdderHookLoadData(), this);
    	} else {
    		Logger.warn("KGenerators will be loaded in post init check, check for more informations futher in this log");
    		this.getServer().getScheduler().runTask(instance, new Runnable() {
				@Override
				public void run() {
					Main.getInstance().enable();
				}
    		});
    	}
    }

	@Override
    public void onLoad() {
    	instance = this;
    	Logger.setup();
    	multiVersion = new MultiVersionManager();
    }
    
    @Override
    public void onDisable() {
    	if (schedules != null)
    		schedules.saveToFile();
    	if (menus != null)
    		menus.closeAll();
    	if (databases != null && databases.getDb() != null)
    		databases.getDb().closeConnection();
    }
    
    public void reload() {
    	Logger.info("Reload: KGenerators reload started");
    	settings = new Settings();
    	generators.reload();
    	players.reload();
    	upgrades.reload();
    	limits = new LimitsManager();
    	Lang.loadFromFiles();
    	this.getServer().getPluginManager().callEvent(new ReloadEvent());
    }
    
    public void enable() {
    	try {
			/* Dependencies check */
			dependencies.standardDependenciesCheck();
			
			/* Configs loader */
			generators = new GeneratorsManager();
			
			settings = new Settings();
			FilesConverter.updateConfig(settings);
			
			recipes = new RecipesManager();
			upgrades = new UpgradesManager();
			limits = new LimitsManager();
			
			Lang.loadFromFiles();
			
			/* Database setup */
			databases = new DatabaseManager(Main.getSettings().getDbType());
			databases.getDb().loadGenerators();
			
			/* Other systems starter */
			holograms = new HologramsManager();
			schedules = new SchedulesManager();
			menus = new MenusManager();
			
			/* Commands setup */
			this.getServer().getPluginCommand("kgenerators").setExecutor(new Commands());
			this.getServer().getPluginCommand("kgenerators").setTabCompleter(new CommandTabCompleter());

			/* Listeners setup */
			this.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
			this.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
			this.getServer().getPluginManager().registerEvents(new CraftItemListener(), this);
			this.getServer().getPluginManager().registerEvents(new BlockPistonListener(), this);
			this.getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
			this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
			this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
			this.getServer().getPluginManager().registerEvents(new FurnaceSmeltListener(), this);
			
			if (!multiVersion.isHigher(12))
				this.getServer().getPluginManager().registerEvents(new PrepareItemCraftListener(), this);
			
			/* 
			 * Metrix
			 */
			int pluginId = 7871;
			Metrics metrics = new Metrics(this, pluginId);
			metrics.addCustomChart(new Metrics.SingleLineChart("configured_generators", () -> Main.getGenerators().getAmount()));
			metrics.addCustomChart(new Metrics.SingleLineChart("placed_generators", () -> Main.getLocations().getAmount()));
			
		} catch (Exception e) {
			Logger.error(e);
		}    	
    }
}