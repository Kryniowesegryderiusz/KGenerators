package me.kryniowesegryderiusz.KGenerators;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.kryniowesegryderiusz.KGenerators.Enums.EnumPickUpMode;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.GenerateBlock;
import me.kryniowesegryderiusz.KGenerators.Listeners.onBlockBreakEvent;
import me.kryniowesegryderiusz.KGenerators.Listeners.onBlockPistonEvent;
import me.kryniowesegryderiusz.KGenerators.Listeners.onBlockPlaceEvent;
import me.kryniowesegryderiusz.KGenerators.Listeners.onCraftItemEvent;
import me.kryniowesegryderiusz.KGenerators.Listeners.onExplosion;
import me.kryniowesegryderiusz.KGenerators.Listeners.onJetsMinions;
import me.kryniowesegryderiusz.KGenerators.Listeners.onPlayerInteractEvent;
import me.kryniowesegryderiusz.KGenerators.MultiVersion.ActionBar;
import me.kryniowesegryderiusz.KGenerators.MultiVersion.BlocksUtils;
import me.kryniowesegryderiusz.KGenerators.MultiVersion.WorldGuardUtils;
import me.kryniowesegryderiusz.KGenerators.MultiVersion.RecipesLoader;
import me.kryniowesegryderiusz.KGenerators.Utils.Config;
import me.kryniowesegryderiusz.KGenerators.Utils.ConfigManager;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;
import me.kryniowesegryderiusz.KGenerators.Utils.Metrics;

public class Main extends JavaPlugin {


	private static Main instance;
	private static Config configFile;	
	private static Config generatorsFile;
	private static Config messagesFile;
	private static Config recipesFile;

	public static LinkedHashMap<String, Generator> generators = new LinkedHashMap<String, Generator>();
	public static HashMap<Location, GeneratorLocation> generatorsLocations = new HashMap<Location, GeneratorLocation>();
	public static ArrayList<Location> scheduledLocations = new ArrayList<Location>();
	
	/* For quick check */
	public static ArrayList<ItemStack> generatorsItemStacks = new ArrayList<ItemStack>();
	
	/* Global settings */
	public static ArrayList<ItemStack> generatingWhitelist = new ArrayList<ItemStack>();
	public static String lang = "en";
	public static Boolean overAllPerPlayerGeneratorsEnabled = false;
	public static int overAllPerPlayerGeneratorsPlaceLimit = -1;
	public static Boolean generatorsActionbarMessages = true;
	public static Boolean restrictMiningByPermission = false;
	public static Boolean enableWorldGuardChecks = false;
	public static EnumPickUpMode pickUpMode = EnumPickUpMode.BREAK;
	public static Boolean pickUpSneak = true;
	public static ItemStack pickUpItem = null;
	public static short explosionHandler = 0;
	
	/* Dependencies */
	public static ArrayList<String> dependencies = new ArrayList<String>();
	
	/* Multiversion reflections */
	private RecipesLoader recipesLoader;
	private static BlocksUtils blocksUtils;
	private static WorldGuardUtils worldGuardUtils;
	private static ActionBar actionBar;
	
    @Override
    public void onEnable() {

        int pluginId = 7871;

		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this, pluginId);
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_loaded_generators", () -> Main.generatorsLocations.size()));
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_single_generators", () -> {
			int nr = 0;
			for (Entry<String, Generator> g : Main.generators.entrySet())
			{
				if (g.getValue().getType().equals("single"))
				{
					nr++;
				}
			}
			return nr;
			}));
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_double_generators", () -> {
			int nr = 0;
			for (Entry<String, Generator> g : Main.generators.entrySet())
			{
				if (g.getValue().getType().equals("double"))
				{
					nr++;
				}
			}
			return nr;
			}));
		
		metrics.addCustomChart(new Metrics.SimplePie("per_player_generators_enabled", () -> overAllPerPlayerGeneratorsEnabled.toString()));
    	
    	ConfigManager.setup();
    	
    	/* Logger setup */
    	Logger.setup();
    	
    	/* Dependencies check */
    	if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
    		Logger.info("[KGenerators] Detected plugin SuperiorSkyblock2. Hooking into it.");
    		dependencies.add("SuperiorSkyblock2");
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
    		Logger.info("[KGenerators] Detected plugin BentoBox. Hooking into it.");
    		dependencies.add("BentoBox");
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("JetsMinions")) {
    		Logger.info("[KGenerators] Detected plugin JetsMinions. Hooking into it.");
    		dependencies.add("JetsMinions");
    	}
    	
    	if (worldGuardUtils != null && Main.getWorldGuardUtils().isWorldGuardHooked()) {
   			Logger.info("[KGenerators] Detected plugin WorldGuard. Hooked into it.");
   			dependencies.add("WorldGuard");
    	}
    	else if (worldGuardUtils != null)
    	{
    		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
    			Logger.info("[KGenerators] Detected plugin WorldGuard, but couldnt hook into it! Search console log above for errors!");
    		}
    	}
    	
    	/* Config loader */
    	if (!new File(getDataFolder(), "config.yml").exists()){
    		Logger.info("[KGenerators] Generating config.yml");
    		this.saveResource("config.yml", false);
    	}
    	try {
			setConfigFile(ConfigManager.getConfig("config.yml", null, false));
		} catch (FileNotFoundException e) {
			Logger.info("[KGenerators] Cant load config");
			this.getServer().getPluginManager().disablePlugin(this);
			Logger.error(e);
		}
    	try {
			getConfigFile().loadConfig();
		} catch (IOException | InvalidConfigurationException e2) {
			Logger.error(e2);
		}
    	Files.loadConfig();
    		
    	/* Recipes loader */
    	if (!new File(getDataFolder(), "recipes.yml").exists()){
    		Logger.info("[KGenerators] Generating recipes.yml");
    		this.saveResource("recipes.yml", false);
    	}
    	try {
    		setRecipesFile(ConfigManager.getConfig("recipes.yml", null, false));
		} catch (FileNotFoundException e1) {
			Logger.error(e1);
		}
    	try {
			getRecipesFile().loadConfig();
		} catch (IOException
				| InvalidConfigurationException e) {
			Logger.error(e);
		}
    	recipesLoader.loadRecipes();
    	
    	/* Placed generators loader */
    	try {
			setGeneratorsFile(ConfigManager.getConfig("generators.yml", null, false));
		} catch (FileNotFoundException e) {
			try {
				ConfigManager.createNewFile("generators.yml", null);
				setGeneratorsFile(ConfigManager.getConfig("generators.yml", null, false));
			} catch (IOException e1) {
				this.getServer().getPluginManager().disablePlugin(this);
				Logger.error(e1);
			}
		}
    	try {
			getGeneratorsFile().loadConfig();
		} catch (IOException | InvalidConfigurationException e2) {
			Logger.error(e2);
		}
    	
        Bukkit.getScheduler().runTask(instance, () -> {
        	Files.loadGenerators();
        });
    	
    	/* Languages manager */
    	mkdir("lang");
    	

    	if (!new File(getDataFolder(), "lang/en.yml").exists()){
    		this.saveResource("lang/en.yml", false);
    	}
    	if (!new File(getDataFolder(), "lang/pl.yml").exists()){
    		this.saveResource("lang/pl.yml", false);
    	}
    	
    	try {
			setMessagesFile(ConfigManager.getConfig("lang/"+lang+".yml", null, false));
		} catch (FileNotFoundException e1) {
			Logger.error("[KGenerators] Cant find lang file " + lang);
			//Logger.error(e1);
		}
    	try {
			getMessagesFile().loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error(e);
		}
    	
    	try {
			LangUtils.loadMessages();
		} catch (IOException e) {
			Logger.error(e);
		}
    	Logger.info("[KGenerators] Messages file loaded with lang: " + lang);
    	
    	this.getServer().getPluginCommand("kgenerators").setExecutor(new Commands());

    	this.getServer().getPluginManager().registerEvents(new onBlockBreakEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onBlockPlaceEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onCraftItemEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onBlockPistonEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onExplosion(), this);
    	this.getServer().getPluginManager().registerEvents(new onPlayerInteractEvent(), this);
    	
    	if (dependencies.contains("JetsMinions"))
    	{
    		this.getServer().getPluginManager().registerEvents(new onJetsMinions(), this);
    	}
    	
    	Logger.info("[KGenerators] Placed generators are loaded in delayed init task! Informations about them are located further in this log!");
    }
    
    @Override
    public void onLoad() {
    	
    	instance = this;
    	versioningSetup();
    	
    }
    
    @Override
    public void onDisable() {
    	this.getServer().getScheduler().cancelTasks(this);
    	for (Location location : scheduledLocations)
    	{
    		GenerateBlock.now(location, Main.generatorsLocations.get(location).getGenerator());
    	}
    }
    
	static void mkdir(String dir){
		File file = new File(Main.getInstance().getDataFolder()+"/"+dir);
		
		  if (file.exists()) {
			  return;
		  }
		
	      boolean bool = file.mkdir();
	      if(!bool){
	         Logger.info("[KGenerators] Can not create directory for "+dir);
	      }
	}
	
	void versioningSetup()
	{
    	String version = this.getServer().getVersion();
    	String packageName = Main.class.getPackage().getName() + ".MultiVersion";
    	String recipesPackage;
    	String blocksPackage;
    	String wgPackage;
    	String actionBarPackage;
    	
    	if (version.contains("1.8")) {
    		recipesPackage = packageName + ".RecipesLoader_1_8";
    		blocksPackage = packageName + ".BlocksUtils_1_8";
    		wgPackage = packageName + ".WorldGuardUtils_1_8";
    		actionBarPackage = packageName + ".ActionBar_1_8";
    	} 
    	else if (version.contains("1.9") || version.contains("1.10") || version.contains("1.11"))
    	{
    		recipesPackage = packageName + ".RecipesLoader_1_8";
    		blocksPackage = packageName + ".BlocksUtils_1_8";
    		wgPackage = packageName + ".WorldGuardUtils_1_8";
    		actionBarPackage = packageName + ".ActionBar_1_9";
    	}
    	else if (version.contains("1.12"))
    	{
    		recipesPackage = packageName + ".RecipesLoader_1_12";
    		blocksPackage = packageName + ".BlocksUtils_1_8";
    		wgPackage = packageName + ".WorldGuardUtils_1_8";
    		actionBarPackage = packageName + ".ActionBar_1_9";
    	}
    	else
    	{
    		recipesPackage = packageName + ".RecipesLoader_1_13";
    		blocksPackage = packageName + ".BlocksUtils_1_13";
    		wgPackage = packageName + ".WorldGuardUtils_1_13";
    		actionBarPackage = packageName + ".ActionBar_1_9";
    	}
    	
    	try {
			recipesLoader = (RecipesLoader) Class.forName(recipesPackage).newInstance();
			blocksUtils = (BlocksUtils) Class.forName(blocksPackage).newInstance();
			if (this.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
				worldGuardUtils = (WorldGuardUtils) Class.forName(wgPackage).newInstance();
	    		worldGuardUtils.worldGuardFlagsAdd();
	    	}
			actionBar = (ActionBar) Class.forName(actionBarPackage).newInstance();
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e3) {
			Logger.error(e3);
		}
	}
    
    public static Main getInstance(){
    	return instance;
    }
    
    public static BlocksUtils getBlocksUtils(){
    	return blocksUtils;
    }
    
    public static WorldGuardUtils getWorldGuardUtils(){
    	return worldGuardUtils;
    }
    
    public static ActionBar getActionBar(){
    	return actionBar;
    }

	public static Config getMessagesFile() {
		return messagesFile;
	}

	public static void setMessagesFile(Config messagesFile) {
		Main.messagesFile = messagesFile;
	}

	public static Config getConfigFile() {
		return configFile;
	}

	public static void setConfigFile(Config configFile) {
		Main.configFile = configFile;
	}

	public static Config getGeneratorsFile() {
		return generatorsFile;
	}

	public static void setGeneratorsFile(Config generatorsFile) {
		Main.generatorsFile = generatorsFile;
	}

	public static Config getRecipesFile() {
		return recipesFile;
	}

	public static void setRecipesFile(Config recipesFile) {
		Main.recipesFile = recipesFile;
	}
}