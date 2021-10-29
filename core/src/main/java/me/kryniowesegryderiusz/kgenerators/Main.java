package me.kryniowesegryderiusz.kgenerators;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IDatabase;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.UpgradeCostExp;
import me.kryniowesegryderiusz.kgenerators.classes.UpgradeCostMoney;
import me.kryniowesegryderiusz.kgenerators.files.GeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.LangFiles;
import me.kryniowesegryderiusz.kgenerators.files.LimitsFile;
import me.kryniowesegryderiusz.kgenerators.files.PlacedGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.RecipesFile;
import me.kryniowesegryderiusz.kgenerators.files.ScheduledGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.UpgradesFile;
import me.kryniowesegryderiusz.kgenerators.gui.MainMenu;
import me.kryniowesegryderiusz.kgenerators.gui.ChancesMenu;
import me.kryniowesegryderiusz.kgenerators.gui.GeneratorMenu;
import me.kryniowesegryderiusz.kgenerators.gui.LimitsMenu;
import me.kryniowesegryderiusz.kgenerators.gui.Menus;
import me.kryniowesegryderiusz.kgenerators.gui.RecipeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.UpgradeMenu;
import me.kryniowesegryderiusz.kgenerators.handlers.DatabasesHandler;
import me.kryniowesegryderiusz.kgenerators.handlers.SQL;
import me.kryniowesegryderiusz.kgenerators.handlers.Vault;
import me.kryniowesegryderiusz.kgenerators.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.hooks.IridiumSkyblockHook;
import me.kryniowesegryderiusz.kgenerators.hooks.SlimefunHook;
import me.kryniowesegryderiusz.kgenerators.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.hooks.JetsMinionsHook;
import me.kryniowesegryderiusz.kgenerators.files.ConfigFile;
import me.kryniowesegryderiusz.kgenerators.files.FilesConverter;
import me.kryniowesegryderiusz.kgenerators.listeners.BlockBreakListener;
import me.kryniowesegryderiusz.kgenerators.listeners.BlockPistonListener;
import me.kryniowesegryderiusz.kgenerators.listeners.BlockPlaceListener;
import me.kryniowesegryderiusz.kgenerators.listeners.CraftItemListener;
import me.kryniowesegryderiusz.kgenerators.listeners.ExplosionListener;
import me.kryniowesegryderiusz.kgenerators.listeners.FurnaceSmeltListener;
import me.kryniowesegryderiusz.kgenerators.listeners.InventoryClickListener;
import me.kryniowesegryderiusz.kgenerators.listeners.PlayerInteractListener;
import me.kryniowesegryderiusz.kgenerators.listeners.PrepareItemCraftListener;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Holograms;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;
import me.kryniowesegryderiusz.kgenerators.multiversion.ActionBar;
import me.kryniowesegryderiusz.kgenerators.multiversion.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.ChatUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.MultiVersion;
import me.kryniowesegryderiusz.kgenerators.multiversion.RecipesLoader;
import me.kryniowesegryderiusz.kgenerators.multiversion.WorldGuardUtils;
import me.kryniowesegryderiusz.kgenerators.utils.Metrics;

public class Main extends JavaPlugin {

	@Getter
	private static Main instance;
	
	/* Settings */
	
	@Getter @Setter
	private static Settings settings = new Settings();
	
	/* Dependencies */
	@Getter
	public static ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
	
	/* Multiversion reflections */
	static @Getter @Setter
	private RecipesLoader recipesLoader;
	@Getter @Setter
	private static BlocksUtils blocksUtils;
	@Getter @Setter
	private static WorldGuardUtils worldGuardUtils;
	@Getter @Setter
	private static ActionBar actionBar;
	@Getter @Setter
	private static ChatUtils chatUtils;
	
	/* Database */
	@Getter
	private static IDatabase db;
	
    @Override
    public void onEnable() {

    	/* Metrix */
        int pluginId = 7871;
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, pluginId);
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_loaded_generators", () -> Generators.amount()));
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_single_generators", () -> Generators.amount(GeneratorType.SINGLE)));
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_double_generators", () -> Generators.amount(GeneratorType.DOUBLE)));
		metrics.addCustomChart(new Metrics.SimplePie("per_player_generators_enabled", () -> String.valueOf(Main.getSettings().isLimits()) ));
    	
    	/* Dependencies check */
		dependencies.clear();
    	dependenciesCheckOnEnable();
    	dependenciesCheckDelayed();
    	
    	
		/* Configs loader */
    	FilesConverter.convert();
    	ConfigFile.globalSettingsLoader();
    	FilesConverter.updateConfig(settings);
		
    	
    	
    	GeneratorsFile.load();
    	RecipesFile.load();
    	Upgrades.registerUpgradeCost(UpgradeCostMoney.class);
    	Upgrades.registerUpgradeCost(UpgradeCostExp.class);
    	UpgradesFile.load();
    	LimitsFile.load();
    	LangFiles.loadLang();
    	
    	/* Database setup */
		this.db = DatabasesHandler.getDatabase(Main.getSettings().getDbType());
		
		
    	Logger.info("Database: Placed generators are loaded in delayed init task! Informations about them are located further in this log!");
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
        	this.db.loadGenerators();
        });	
		
		
		/* Other systems starter */
    	Holograms.setup();
    	Schedules.setup();
    	ScheduledGeneratorsFile.load();
    	Menus.setup();
    	
    	
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
    	
    	if (!MultiVersion.isHigher(12))
    		this.getServer().getPluginManager().registerEvents(new PrepareItemCraftListener(), this);    	
    }

	@Override
    public void onLoad() {
    	
    	instance = this;
    	/* Logger setup */
    	Logger.setup();
    	MultiVersion.setup();
    	
    }
    
    @Override
    public void onDisable() {
    	ScheduledGeneratorsFile.save();
    	Menus.closeAll();
    	this.db.closeConnection();

    }

    public static void dependenciesCheckOnEnable() {
    	
    	if (Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2") != null) {
    		Logger.info("Dependencies: Detected plugin SuperiorSkyblock2. Hooking into it.");
    		dependencies.add(Dependency.SUPERIOR_SKYBLOCK_2);
    		SuperiorSkyblock2Hook.setup();
    	}
    	
    	if (Vault.setupEconomy())
    	{
    		Logger.info("Dependencies: Detected Vault economy. Hooked into it.");
    		dependencies.add(Dependency.VAULT_ECONOMY);
    	}
    	else
    		Logger.warn("Dependencies: Vault economy was not found! Some features could not work!");
    	
    	if (Vault.setupPermissions())
    	{
    		Logger.info("Dependencies: Detected Vault permissions. Hooked into it.");
    		dependencies.add(Dependency.VAULT_PERMISSIONS);
    	}
    	else
    		Logger.warn("Dependencies: Vault permissions was not found! Some features could not work!");
    	
    }
    
    public static void dependenciesCheckDelayed() {
    	Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
	    	
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
	    	
	    	if (Bukkit.getPluginManager().isPluginEnabled("Slimefun")) {
	    		Logger.info("Dependencies: Detected plugin Slimefun. Hooking into it.");
	    		Main.getInstance().getServer().getPluginManager().registerEvents(new SlimefunHook(), Main.getInstance());
	    		dependencies.add(Dependency.SLIMEFUN);
	    	}
	    	
	    	if (worldGuardUtils != null && Main.getWorldGuardUtils().isWorldGuardHooked()) {
	   			Logger.info("Dependencies: Detected plugin WorldGuard. Hooked into it.");
	   			dependencies.add(Dependency.WORLD_GUARD);
	    	}
	    	else if (worldGuardUtils != null)
	    	{
	    		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
	    			Logger.error("Dependencies: Detected plugin WorldGuard, but couldnt hook into it! Search console log above for errors!");
	    		}
	    	}
	    	
	    	if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays"))
	    	{
	    		Logger.info("Dependencies: Detected plugin HolographicDisplays. Hooked into it.");
	   			dependencies.add(Dependency.HOLOGRAPHIC_DISPLAYS);
	    	}
	    	else
	    	{
	    		for (Entry<String, Generator> e : Generators.getEntrySet())
	    		{
	    			if (e.getValue().isHologram())
	    				Logger.warn("Generators file: Generator " + e.getKey() + " has enabled holograms, but HolographicDisplays was not found! Holograms wouldnt work!");
	    		}
	    	}
    	});
	}
}