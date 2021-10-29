package me.kryniowesegryderiusz.kgenerators.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Settings;
import me.kryniowesegryderiusz.kgenerators.enums.Action;
import me.kryniowesegryderiusz.kgenerators.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.enums.Interaction;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class FilesConverter {
	
	@SuppressWarnings("unchecked")
	public static void convert()
	{
		File configFile = new File(Main.getInstance().getDataFolder(), "config.yml");
		Config config;
		
		if (configFile.exists()){
			try {
	    		config = ConfigManager.getConfig("config.yml", (String) null, false, false);
				config.loadConfig();
			} catch (IOException | InvalidConfigurationException e) {
				Logger.error("FilesConverter: Cant load config. Disabling plugin.");
				Logger.error(e);
				Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
				return;
			}
			
			if (!config.contains("generators"))
				return;

			Logger.warn("FilesConverter: FilesConverter: Recognised KGenerators v3 filesystem");
			Logger.warn("FilesConverter: FilesConverter: Converting to KGenerators v4 filesystem");

			
			/*
			 * Converting placed generators
			 */
			
			File placedGeneratorsFile = new File(Main.getInstance().getDataFolder(), "generators.yml");
			Config placedGeneratorsConfig;
			Config newPlacedGeneratorsConfig;
			
			if (placedGeneratorsFile.exists()){
				try {
					placedGeneratorsConfig = ConfigManager.getConfig("generators.yml", (String) null, false, false);
					placedGeneratorsConfig.loadConfig();
				} catch (IOException | InvalidConfigurationException e) {
					Logger.error("FilesConverter: Cant load generators.yml. Disabling plugin.");
					Logger.error(e);
					Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
					return;
				}
				
				if (placedGeneratorsConfig.contains("placedGenerators"))
				{
					try {
						newPlacedGeneratorsConfig = ConfigManager.getConfig("placed.yml", "/data", false, true);
						newPlacedGeneratorsConfig.loadConfig();
					} catch (IOException | InvalidConfigurationException e) {
						Logger.error("FilesConverter: Cant load data/generators.yml. Disabling plugin.");
						Logger.error(e);
						Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
						return;
					}
					
					int amount = 0;
					ConfigurationSection mainSection = placedGeneratorsConfig.getConfigurationSection("placedGenerators");
		        	for(String generatorLocationString: mainSection.getKeys(false))
		        	{
		        		if (placedGeneratorsConfig.contains("placedGenerators."+generatorLocationString+".generatorID"))
		        		{
		        			String generatorId = placedGeneratorsConfig.getString("placedGenerators."+generatorLocationString+".generatorID");
		        			String owner = "";
		        			if (placedGeneratorsConfig.contains("placedGenerators."+generatorLocationString+".owner"))
			        		{
			        			owner = placedGeneratorsConfig.getString("placedGenerators."+generatorLocationString+".owner");
			        		}
		        			newPlacedGeneratorsConfig.set(generatorLocationString+".generatorID", generatorId);
		        			if (!owner.equals("")) newPlacedGeneratorsConfig.set(generatorLocationString+".owner", owner);
		        			amount++;
		        		}
		        		else
		        		{
		        			Logger.error("FilesConverter: Couldnt load " + generatorLocationString + "! That generator would be lost!");
		        		}
		        	}
		        	
		        	try {
		        		newPlacedGeneratorsConfig.saveConfig();
					} catch (IOException e) {
						Logger.error("FilesConverter: Cant save data/generators.yml. Disabling plugin.");
						Logger.error(e);
						Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
						return;
					}
		        	
		        	placedGeneratorsFile.delete();
		        	Logger.info("FilesConverter: Converted " + String.valueOf(amount) + " placed generators to new filesystem!");
				}
			}
			
			/*
			 * Converting settings
			 */
			
			Settings settings = new Settings();
			
			if (config.contains("settings.lang")) settings.setLang(config.getString("settings.lang"));
			
			if (config.contains("settings.can-generate-instead")) {
				ArrayList<String> tempListString = new ArrayList<String>();
				ArrayList<ItemStack> generatingWhitelist = new ArrayList<ItemStack>();
				tempListString = (ArrayList<String>) config.getList("settings.can-generate-instead");
				
				for (String s : tempListString) {
					ItemStack m = XUtils.parseItemStack(s, "FilesConverter Config file", true);
					generatingWhitelist.add(m);
				}
				settings.setGeneratingWhitelist(generatingWhitelist);
			}
			
			if (config.contains("settings.generators-actionbar-messages"))
			{
				settings.setActionbarMessages(config.getBoolean("settings.generators-actionbar-messages"));
			}
			
			if (config.contains("settings.explosion-handler"))
			{
				settings.setExplosionHandler((short) config.getInt("settings.explosion-handler"));
			}
			
			Interaction interaction;
			ItemStack item;
			boolean sneak;
			
			if (config.contains("settings.pick-up.mode"))
			{
				if (config.getString("settings.pick-up.mode").equals("BREAK")) interaction = Interaction.BREAK;
				else if (config.getString("settings.pick-up.mode").equals("RIGHT_CLICK")) interaction = Interaction.RIGHT_CLICK;
				else interaction = Interaction.LEFT_CLICK;
			}
			else
			{
				interaction = Interaction.LEFT_CLICK;
			}
			
			if (config.contains("settings.pick-up.item"))
			{
				if (config.getString("settings.pick-up.item").toLowerCase().equals("any")) item = null;
				else item = (XUtils.parseItemStack(config.getString("settings.pick-up.item"), "FilesConverter Config file", false));
			}
			else
			{
				item = null;
			}
			
			if (config.contains("settings.pick-up.sneak")) sneak = config.getBoolean("settings.pick-up.sneak");
			else sneak = true;
			settings.addGeneratorAction(Action.PICKUP, new GeneratorAction(Action.PICKUP, interaction, item, sneak));
			settings.addGeneratorAction(Action.OPENGUI, new GeneratorAction(Action.OPENGUI, Interaction.NONE, null, false));
			settings.addGeneratorAction(Action.TIMELEFT, new GeneratorAction(Action.TIMELEFT, Interaction.NONE, null, false));
			
			/*
			 * Converting generators definitions
			 */
			
			File newGeneratorsFile = new File(Main.getInstance().getDataFolder(), "generators.yml");
			
			if (!newGeneratorsFile.exists())
			{
				try {
					newGeneratorsFile.createNewFile();
				} catch (IOException e1) {
					Logger.error("FilesConverter: Cant create new generators file. Disabling plugin.");
					Logger.error(e1);
					Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
					return;
				}
			}
			
			add(newGeneratorsFile, "#KGenerators made by Kryniowesegryderiusz (Krynio_ on spigot)");
			add(newGeneratorsFile, "#https://www.spigotmc.org/resources/79246/");
			add(newGeneratorsFile, "#Need help? Join KGenerators discord: https://discord.gg/BZRjhpP4Ab");
			add(newGeneratorsFile, "");
			add(newGeneratorsFile, "#Check why a lot of settings are optional and how does it work: https://github.com/Kryniowesegryderiusz/KGenerators/wiki/Configuration-disclaimer");
			add(newGeneratorsFile, "");
			add(newGeneratorsFile, "#!!! WARNING !!! This file was autoupdated from KGenerators v3 to Kgenerators v4!");
			add(newGeneratorsFile, "#There are probably new generators functions that you will like to use");
			add(newGeneratorsFile, "#Check them at: https://github.com/Kryniowesegryderiusz/KGenerators/blob/main/core/src/main/resources/generators.yml");
			add(newGeneratorsFile, "");
			
			try {
				boolean now = false;
				ArrayList<String> allLines = (ArrayList<String>) Files.readAllLines(configFile.toPath());
				for (String l : allLines)
				{
					if(now)
					{
						if (l.length() >= 2)
							add(newGeneratorsFile, l.substring(2));
						else
							add(newGeneratorsFile, l);
					}
					if (l.startsWith("generators:")) now = true;
				}
			} catch (IOException e) {
				Logger.error("FilesConverter: Cant load old config lines. Disabling plugin.");
				Logger.error(e);
				Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
				return;
			}
			
			/*
			 * Converting recipes
			 */
			
			File oldRecipesFile = new File(Main.getInstance().getDataFolder(), "recipes.yml");
			File newRecipesFile = new File(Main.getInstance().getDataFolder(), "recipestemp.yml");
			
			if (!newRecipesFile.exists())
			{
				try {
					newRecipesFile.createNewFile();
				} catch (IOException e1) {
					Logger.error("FilesConverter: Cant create temp recipes file. Disabling plugin.");
					Logger.error(e1);
					Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
					return;
				}
			}
			
			add(newRecipesFile, "#KGenerators made by Kryniowesegryderiusz (Krynio_ on spigot)");
			add(newRecipesFile, "#https://www.spigotmc.org/resources/79246/");
			add(newRecipesFile, "#Need help? Join KGenerators discord: https://discord.gg/BZRjhpP4Ab");
			add(newRecipesFile, "");
			add(newRecipesFile, "#!!! WARNING !!! This file was autoupdated from KGenerators v3 to Kgenerators v4!");
			add(newRecipesFile, "");
			
			try {
				boolean now = false;
				ArrayList<String> allLines = (ArrayList<String>) Files.readAllLines(oldRecipesFile.toPath());
				for (String l : allLines)
				{
					if(now)
					{
						if (l.length() >= 2)
							add(newRecipesFile, l.substring(2));
						else
							add(newRecipesFile, l);
					}
					if (l.startsWith("recipes:")) now = true;
				}
			} catch (IOException e) {
				Logger.error("FilesConverter: Cant load old recipe lines. Disabling plugin.");
				Logger.error(e);
				Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
				return;
			}
			try {
				Files.delete(oldRecipesFile.toPath());
				Files.copy(newRecipesFile.toPath(), oldRecipesFile.toPath());
				Files.delete(newRecipesFile.toPath());
			} catch (IOException e) {
				Logger.error("FilesConverter: Cant finish new recipe file. Disabling plugin.");
				Logger.error(e);
				Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
				return;
			}
			
			/*
			 * End
			 */
			
			configFile.delete();
			updateConfig(settings);
			
			Logger.warn("FilesConverter: All files have been converted to v4 filesystem correctly!");
		}
	}
	
	public static void updateConfig(Settings settings)
	{
		File file = new File(Main.getInstance().getDataFolder(), "config.yml");
		Config config;
		try {
    		config = ConfigManager.getConfig("config.yml", (String) null, false, true);
			config.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error("FilesConverter: Cant load config. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
		
		if (!config.contains("lang"))
		{
			add(file, "#KGenerators made by Kryniowesegryderiusz (Krynio_ on spigot)");
			add(file, "#https://www.spigotmc.org/resources/79246/");
			add(file, "#Need help? Join KGenerators discord: https://discord.gg/BZRjhpP4Ab");
			add(file, "");
			add(file, "#Choose lang file. Default possibilities are en, pl, ro, vi");
			add(file, "lang: " + settings.getLang());
			Logger.info("FilesConverter: Added lang settings to config file");
		}
		
		if (!config.contains("can-generate-instead"))
		{
			add(file, "");
			add(file, "#By default generator will generate block only on air. Here you can add blocks, which will be replaced by generated block.");
			add(file, "can-generate-instead:");
			for (ItemStack i : settings.getGeneratingWhitelist())
			{
				add(file, "  - " + i.getType().toString());
			}
			Logger.info("FilesConverter: Added can-generate-instead settings to config file");
		}
		
		if (!config.contains("actions"))
		{
			add(file, "");
			add(file, "#This configuration section is for configuring actions needed for particular features");
			add(file, "#Possible modes: BREAK (avaible only for pick-up), LEFT_CLICK, RIGHT_CLICK, NONE (ex. because of gui usage)");
			add(file, "#Item could be \"ANY\"");
			add(file, "#Sneak indicates if shift pressed is required");
			add(file, "actions:");
			add(file, "  #Action, which will be used for picking up generators");
			add(file, "  pick-up:");
			add(file, "    mode: " + settings.getAction(Action.PICKUP).getInteraction().toString());
			if (settings.getAction(Action.PICKUP).getItem() != null)
				add(file, "    item: " + settings.getAction(Action.PICKUP).getItem().getType().toString());
			else
				add(file, "    item: ANY");
			add(file, "    sneak: " + String.valueOf(settings.getAction(Action.PICKUP).isSneak()));
			add(file, "  #Action, which will be used for opening generator gui");
			add(file, "  open-gui:");
			add(file, "    mode: " + settings.getAction(Action.OPENGUI).getInteraction().toString());
			if (settings.getAction(Action.PICKUP).getItem() != null)
				add(file, "    item: " + settings.getAction(Action.OPENGUI).getItem().getType().toString());
			else
				add(file, "    item: ANY");
			add(file, "    sneak: " + String.valueOf(settings.getAction(Action.OPENGUI).isSneak()));
			add(file, "  #Action, which will be used for checking how much time left before regeneration");
			add(file, "  time-left-check:");
			add(file, "    mode: " + settings.getAction(Action.TIMELEFT).getInteraction().toString());
			if (settings.getAction(Action.TIMELEFT).getItem() != null)
				add(file, "    item: " + settings.getAction(Action.PICKUP).getItem().getType().toString());
			else
				add(file, "    item: ANY");
			add(file, "    sneak: " + String.valueOf(settings.getAction(Action.TIMELEFT).isSneak()));
			Logger.info("FilesConverter: Added actions settings to config file");
		}
		
		if (!config.contains("intervals"))
		{
			add(file, "");
			add(file, "#These settings are related to performance and its not recommended to set them too low");
			add(file, "#Values are presented in ticks (20 ticks = 1 second)");
			add(file, "#Changed values need server restart to affect plugin functioning");
			add(file, "intervals:");
			add(file, "  #How often should generators check if they should regenerate?");
			add(file, "  #All generator delays should be a multiple of this number");
			add(file, "  generation-check: 10");
			add(file, "  #How often should holograms be updated?");
			add(file, "  hologram-update: 20");
			add(file, "  #How often should generators guis be updated?");
			add(file, "  #Set -1 to not update GUI");
			add(file, "  gui-update: 20");
			Logger.info("FilesConverter: Added intervals settings to config file");
		}
		
		if (!config.contains("disabled-worlds"))
		{
			add(file, "");
			add(file, "#List of worlds, where generators will not be enabled:");
			add(file, "disabled-worlds:");
			add(file, "- test_world");
			Logger.info("FilesConverter: Added disabled-worlds settings to config file");
		}
			
		if (!config.contains("pick-up-to-eq"))
		{
			add(file, "");
			add(file, "#Should generators be picked up directly to equipment?");
			add(file, "pick-up-to-eq: true");
			Logger.info("FilesConverter: Added pick-up-to-eq settings to config file");
		}
		
		if (!config.contains("sounds"))
		{
			add(file, "");
			add(file, "#Here you can choose which sounds should be played on each action.");
			add(file, "#ALWAYS use names from https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html");
			add(file, "sounds:");
			add(file, "  place:");
			add(file, "    #Type sound name or \"NONE\" to disable sound");
			add(file, "    sound: BLOCK_ANVIL_LAND");
			add(file, "    #Volume should be a number between 0.0 and 10.0 (1.0 is normal)");
			add(file, "    volume: 0.2");
			add(file, "    #Volume should be a number between 0.0 and 2.0 (0.0 is normal)");
			add(file, "    pitch: 0.0");
			add(file, "  pick-up:");
			add(file, "    sound: ENTITY_BAT_TAKEOFF");
			add(file, "    volume: 0.3");
			add(file, "    pitch: 0.0");
			add(file, "  upgrade:");
			add(file, "    sound: ENTITY_PLAYER_LEVELUP");
			add(file, "    volume: 0.3");
			add(file, "    pitch: 0.0");
			Logger.info("FilesConverter: Added sounds settings to config file");
		}
		
		if (!config.contains("database"))
		{
			add(file, "#Database settings");
			add(file, "database:");
			add(file, "  #Possible options: SQLITE, MYSQL, YAML (not recommended for big servers)");
			add(file, "  #You can convert data from one database to another! More info here: https://github.com/Kryniowesegryderiusz/KGenerators/wiki/Converting-databases ");
			add(file, "  dbtype: YAML");
			add(file, "  #Settings for MYSQL");
			add(file, "  host: hostname");
			add(file, "  port: 3306");
			add(file, "  database: database");
			add(file, "  username: username");
			add(file, "  password: password");
			Logger.info("FilesConverter: Added database settings to config file");
			settings.setDbType(DatabaseType.YAML);
			Logger.warn("FilesConverter: Changed database type from default to YAML");
		}
	}
	
	private static void add(File file, String string)
	{
		FilesFunctions.addToFile(file, string);
	}	
}
