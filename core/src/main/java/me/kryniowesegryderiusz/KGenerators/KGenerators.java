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

import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.Listeners.onBlockBreakEvent;
import me.kryniowesegryderiusz.KGenerators.Listeners.onBlockPistonEvent;
import me.kryniowesegryderiusz.KGenerators.Listeners.onBlockPlaceEvent;
import me.kryniowesegryderiusz.KGenerators.Listeners.onCraftItemEvent;
import me.kryniowesegryderiusz.KGenerators.Listeners.onExplosion;
import me.kryniowesegryderiusz.KGenerators.Listeners.onJetsMinions;
import me.kryniowesegryderiusz.KGenerators.MultiVersion.ActionBar;
import me.kryniowesegryderiusz.KGenerators.MultiVersion.BlocksUtils;
import me.kryniowesegryderiusz.KGenerators.MultiVersion.WorldGuardUtils;
import me.kryniowesegryderiusz.KGenerators.MultiVersion.RecipesLoader;
import me.kryniowesegryderiusz.KGenerators.Utils.Config;
import me.kryniowesegryderiusz.KGenerators.Utils.ConfigManager;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;
import me.kryniowesegryderiusz.KGenerators.Utils.Metrics;

public class KGenerators extends JavaPlugin {


	private static KGenerators instance;
	static Config config;	
	static Config generatorsFile;
	static Config messagesFile;
	static Config recipesFile;

	public static LinkedHashMap<String, Generator> generators = new LinkedHashMap<String, Generator>();
	public static HashMap<Location, GeneratorLocation> generatorsLocations = new HashMap<Location, GeneratorLocation>();
	
	/* For quick check */
	public static ArrayList<ItemStack> generatorsItemStacks = new ArrayList<ItemStack>();
	
	/* Global settings */
	public static ArrayList<ItemStack> generatingWhitelist = new ArrayList<ItemStack>();
	public static String lang = "en";
	public static Boolean overAllPerPlayerGeneratorsEnabled = false;
	public static int overAllPerPlayerGeneratorsPlaceLimit = -1;
	public static Boolean generatorsActionbarMessages = true;
	
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
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_loaded_generators", () -> KGenerators.generatorsLocations.size()));
		metrics.addCustomChart(new Metrics.SingleLineChart("number_of_single_generators", () -> {
			int nr = 0;
			for (Entry<String, Generator> g : KGenerators.generators.entrySet())
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
			for (Entry<String, Generator> g : KGenerators.generators.entrySet())
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
    	
    	/* Dependencies check */
    	if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
    		System.out.println("[KGenerators] Detected plugin SuperiorSkyblock2. Hooking into it.");
    		dependencies.add("SuperiorSkyblock2");
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
    		System.out.println("[KGenerators] Detected plugin BentoBox. Hooking into it.");
    		dependencies.add("BentoBox");
    	}
    	
    	if (Bukkit.getPluginManager().isPluginEnabled("JetsMinions")) {
    		System.out.println("[KGenerators] Detected plugin JetsMinions. Hooking into it.");
    		dependencies.add("JetsMinions");
    	}
    	
    	if (worldGuardUtils != null && KGenerators.getWorldGuardUtils().isWorldGuardHooked()) {
   			System.out.println("[KGenerators] Detected plugin WorldGuard. Hooked into it.");
   			dependencies.add("WorldGuard");
    	}
    	else if (worldGuardUtils != null)
    	{
    		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
    			System.out.println("[KGenerators] Detected plugin WorldGuard, but couldnt hook into it! Search console log above for errors!");
    		}
    	}
    	
    	/* Config loader */
    	if (!new File(getDataFolder(), "config.yml").exists()){
    		System.out.println("[KGenerators] Generating config.yml");
    		this.saveResource("config.yml", false);
    	}
    	try {
			config = ConfigManager.getConfig("config.yml", null, false);
		} catch (FileNotFoundException e) {
			System.out.println("[KGenerators] Cant load config");
			this.getServer().getPluginManager().disablePlugin(this);
			e.printStackTrace();
		}
    	try {
			config.loadConfig();
		} catch (IOException | InvalidConfigurationException e2) {
			e2.printStackTrace();
		}
    	ConfigLoader.loadConfig();
    		
    	/* Recipes loader */
    	if (!new File(getDataFolder(), "recipes.yml").exists()){
    		System.out.println("[KGenerators] Generating recipes.yml");
    		this.saveResource("recipes.yml", false);
    	}
    	try {
    		recipesFile = ConfigManager.getConfig("recipes.yml", null, false);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    	try {
			recipesFile.loadConfig();
		} catch (IOException
				| InvalidConfigurationException e) {
			e.printStackTrace();
		}
    	recipesLoader.loadRecipes();
    	
    	/* Placed generators loader */
    	try {
			generatorsFile = ConfigManager.getConfig("generators.yml", null, false);
		} catch (FileNotFoundException e) {
			try {
				ConfigManager.createNewFile("generators.yml", null);
				generatorsFile = ConfigManager.getConfig("generators.yml", null, false);
			} catch (IOException e1) {
				this.getServer().getPluginManager().disablePlugin(this);
				e1.printStackTrace();
			}
		}
    	try {
			generatorsFile.loadConfig();
		} catch (IOException | InvalidConfigurationException e2) {
			e2.printStackTrace();
		}
    	
        Bukkit.getScheduler().runTask(instance, () -> {
        	ConfigLoader.loadGenerators();
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
			messagesFile = ConfigManager.getConfig("lang/"+lang+".yml", null, false);
		} catch (FileNotFoundException e1) {
			System.out.println("[KGenerators] Cant find lang file");
			e1.printStackTrace();
		}
    	try {
			messagesFile.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
    	
    	try {
			LangUtils.loadMessages();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println("[KGenerators] Messages file loaded with lang: " + lang);
    	
    	this.getServer().getPluginCommand("kgenerators").setExecutor(new Commands());

    	this.getServer().getPluginManager().registerEvents(new onBlockBreakEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onBlockPlaceEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onCraftItemEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onBlockPistonEvent(), this);
    	this.getServer().getPluginManager().registerEvents(new onExplosion(), this);
    	
    	if (dependencies.contains("JetsMinions"))
    	{
    		this.getServer().getPluginManager().registerEvents(new onJetsMinions(), this);
    	}
    	
    	System.out.println("[KGenerators] Placed generators are loaded in delayed init task! Informations about them are located further in this log!");
    }
    
    @Override
    public void onLoad() {
    	
    	instance = this;
    	versioningSetup();
    	
    }

    @Override
    public void onDisable() {
    }
    
    static Config getPluginConfig(){
    	return config;
    }
    
    static Config getPluginGeneratorsFile(){
    	return generatorsFile;
    }
    
    public static Config getPluginMessages(){
    	return messagesFile;
    }
    
    public static Config getRecipesFile(){
    	return recipesFile;
    }
    
    public static KGenerators getInstance(){
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
    
	static void mkdir(String dir){
		File file = new File(KGenerators.getInstance().getDataFolder()+"/"+dir);
		
		  if (file.exists()) {
			  return;
		  }
		
	      boolean bool = file.mkdir();
	      if(!bool){
	         System.out.println("[KGenerators] Can not create directory for "+dir);
	      }
	}
	
	void versioningSetup()
	{
    	String version = this.getServer().getVersion();
    	String packageName = KGenerators.class.getPackage().getName() + ".MultiVersion";
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
			e3.printStackTrace();
		}
	}
}