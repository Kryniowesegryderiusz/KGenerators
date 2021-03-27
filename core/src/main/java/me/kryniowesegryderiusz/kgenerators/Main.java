package me.kryniowesegryderiusz.kgenerators;


import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumDependency;
import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.Settings;
import me.kryniowesegryderiusz.kgenerators.files.GeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.LangFiles;
import me.kryniowesegryderiusz.kgenerators.files.PlacedGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.RecipesFile;
import me.kryniowesegryderiusz.kgenerators.files.ScheduledGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.UpgradesFile;
import me.kryniowesegryderiusz.kgenerators.gui.MainMenu;
import me.kryniowesegryderiusz.kgenerators.gui.ChancesMenu;
import me.kryniowesegryderiusz.kgenerators.gui.GeneratorMenu;
import me.kryniowesegryderiusz.kgenerators.gui.Menus;
import me.kryniowesegryderiusz.kgenerators.gui.RecipeMenu;
import me.kryniowesegryderiusz.kgenerators.gui.UpgradeMenu;
import me.kryniowesegryderiusz.kgenerators.handlers.Vault;
import me.kryniowesegryderiusz.kgenerators.files.ConfigFile;
import me.kryniowesegryderiusz.kgenerators.files.FilesConverter;
import me.kryniowesegryderiusz.kgenerators.listeners.onBlockBreakEvent;
import me.kryniowesegryderiusz.kgenerators.listeners.onBlockPistonEvent;
import me.kryniowesegryderiusz.kgenerators.listeners.onBlockPlaceEvent;
import me.kryniowesegryderiusz.kgenerators.listeners.onCraftItemEvent;
import me.kryniowesegryderiusz.kgenerators.listeners.onExplosion;
import me.kryniowesegryderiusz.kgenerators.listeners.onFurnaceSmeltEvent;
import me.kryniowesegryderiusz.kgenerators.listeners.onInventoryClickEvent;
import me.kryniowesegryderiusz.kgenerators.listeners.onJetsMinions;
import me.kryniowesegryderiusz.kgenerators.listeners.onPlayerInteractEvent;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Holograms;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.multiversion.ActionBar;
import me.kryniowesegryderiusz.kgenerators.multiversion.BlocksUtils;
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
	public static ArrayList<EnumDependency> dependencies = new ArrayList<EnumDependency>();
	
	/* Multiversion reflections */
	static @Getter @Setter
	private RecipesLoader recipesLoader;
	@Getter @Setter
	private static BlocksUtils blocksUtils;
	@Getter @Setter
	private static WorldGuardUtils worldGuardUtils;
	@Getter @Setter
	private static ActionBar actionBar;
	
    @Override
    public void onEnable() {

        int pluginId = 7871;

		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, pluginId);
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_loaded_generators", () -> Generators.amount()));
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_single_generators", () -> Generators.amount(GeneratorType.SINGLE)));
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_double_generators", () -> Generators.amount(GeneratorType.DOUBLE)));
		
		metrics.addCustomChart(new Metrics.SimplePie("per_player_generators_enabled", () -> String.valueOf(Main.getSettings().isPerPlayerGenerators()) ));
    	
    	/* Dependencies check */
    	dependenciesCheck();
    	
    	FilesConverter.convert();
    	
    	ConfigFile.globalSettingsLoader();
    	FilesConverter.updateConfig(settings);
    	
    	GeneratorsFile.loadGenerators();
    	RecipesFile.loadRecipes();
    	PlacedGeneratorsFile.loadPlacedGenerators();
    	UpgradesFile.load();
    	
    	LangFiles.loadLang();
    	
    	Holograms.setup();
    	
    	Schedules.setup();
    	ScheduledGeneratorsFile.load();
    	
    	Menus.setup();
    	
    	this.getServer().getPluginCommand("kgenerators").setExecutor(new Commands());

    	this.getServer().getPluginManager().registerEvents(new onBlockBreakEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onBlockPlaceEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onCraftItemEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onBlockPistonEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onExplosion(), this);
    	this.getServer().getPluginManager().registerEvents(new onPlayerInteractEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onInventoryClickEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onFurnaceSmeltEvent(), this);
    	
    	this.getServer().getPluginManager().registerEvents(new GeneratorMenu(), this);
    	this.getServer().getPluginManager().registerEvents(new MainMenu(), this);
    	this.getServer().getPluginManager().registerEvents(new ChancesMenu(), this);
    	this.getServer().getPluginManager().registerEvents(new RecipeMenu(), this);
    	this.getServer().getPluginManager().registerEvents(new UpgradeMenu(), this);
    	
    	if (dependencies.contains(EnumDependency.JetsMinions))
    	{
    		this.getServer().getPluginManager().registerEvents(new onJetsMinions(), this);
    	}
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
    }
    
    
    public static void dependenciesCheck() {
    	Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
    		dependencies.clear();
    		
	    	if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
	    		Logger.info("Dependencies: Detected plugin SuperiorSkyblock2. Hooking into it.");
	    		dependencies.add(EnumDependency.SuperiorSkyblock2);
	    	}
	    	
	    	if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
	    		Logger.info("Dependencies: Detected plugin BentoBox. Hooking into it.");
	    		dependencies.add(EnumDependency.BentoBox);
	    	}
	    	
	    	if (Bukkit.getPluginManager().isPluginEnabled("JetsMinions")) {
	    		Logger.info("Dependencies: Detected plugin JetsMinions. Hooking into it.");
	    		dependencies.add(EnumDependency.JetsMinions);
	    	}
	    	
	    	if (worldGuardUtils != null && Main.getWorldGuardUtils().isWorldGuardHooked()) {
	   			Logger.info("Dependencies: Detected plugin WorldGuard. Hooked into it.");
	   			dependencies.add(EnumDependency.WorldGuard);
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
	   			dependencies.add(EnumDependency.HolographicDisplays);
	    	}
	    	else
	    	{
	    		for (Entry<String, Generator> e : Generators.getEntrySet())
	    		{
	    			if (e.getValue().isHologram())
	    				Logger.warn("Generators file: Generator " + e.getKey() + " has enabled holograms, but HolographicDisplays was not found! Holograms wouldnt work!");
	    		}
	    	}
	    	
	    	if (Vault.setupEconomy())
	    	{
	    		Logger.info("Dependencies: Detected Vault economy. Hooked into it.");
	    		dependencies.add(EnumDependency.VaultEconomy);
	    	}
	    	else
	    	{
	    		Logger.warn("Dependencies: Vault economy was not found! Some features could not work!");
	    	}
	    	
	    	if (Vault.setupPermissions())
	    	{
	    		Logger.info("Dependencies: Detected Vault permissions. Hooked into it.");
	    		dependencies.add(EnumDependency.VaultPermissions);
	    	}
	    	else
	    	{
	    		Logger.warn("Dependencies: Vault permissions was not found! Some features could not work!");
	    	}
    	});
	}
}