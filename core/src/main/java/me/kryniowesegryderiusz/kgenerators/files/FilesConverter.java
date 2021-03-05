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
import me.kryniowesegryderiusz.kgenerators.Enums.EnumAction;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumInteraction;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.managers.Settings;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class FilesConverter {
	
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
			if (config.contains("settings.per-player-generators.enabled"))
			{
				settings.setPerPlayerGenerators(config.getBoolean("settings.per-player-generators.enabled"));
			}
			
			if (config.contains("settings.per-player-generators.overall-place-limit"))
			{
				settings.setPerPlayerGeneratorsPlaceLimit(config.getInt("settings.per-player-generators.overall-place-limit"));
			}
			
			if (config.contains("settings.generators-actionbar-messages"))
			{
				settings.setActionbarMessages(config.getBoolean("settings.generators-actionbar-messages"));
			}
			
			if (config.contains("settings.explosion-handler"))
			{
				settings.setExplosionHandler((short) config.getInt("settings.explosion-handler"));
			}
			
			EnumInteraction interaction;
			ItemStack item;
			boolean sneak;
			
			if (config.contains("settings.pick-up.mode"))
			{
				if (config.getString("settings.pick-up.mode").equals("BREAK")) interaction = EnumInteraction.BREAK;
				else if (config.getString("settings.pick-up.mode").equals("RIGHT_CLICK")) interaction = EnumInteraction.RIGHT_CLICK;
				else interaction = EnumInteraction.LEFT_CLICK;
			}
			else
			{
				interaction = EnumInteraction.LEFT_CLICK;
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
			settings.addGeneratorAction(EnumAction.PICKUP, new GeneratorAction(EnumAction.PICKUP, interaction, item, sneak));
			settings.addGeneratorAction(EnumAction.OPENGUI, new GeneratorAction(EnumAction.OPENGUI, EnumInteraction.NONE, null, false));
			settings.addGeneratorAction(EnumAction.TIMELEFT, new GeneratorAction(EnumAction.TIMELEFT, EnumInteraction.NONE, null, false));
			
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
			
			addToFile(newGeneratorsFile, "#KGenerators made by Kryniowesegryderiusz (Krynio_ on spigot)");
			addToFile(newGeneratorsFile, "#https://www.spigotmc.org/resources/79246/");
			addToFile(newGeneratorsFile, "#Need help? Join KGenerators discord: https://discord.gg/BZRjhpP4Ab");
			addToFile(newGeneratorsFile, "");
			addToFile(newGeneratorsFile, "#Check why a lot of settings are optional and how does it work: https://github.com/Kryniowesegryderiusz/KGenerators/wiki/Configuration-disclaimer");
			addToFile(newGeneratorsFile, "");
			addToFile(newGeneratorsFile, "#!!! WARNING !!! This file was autoupdated from KGenerators v3 to Kgenerators v4!");
			addToFile(newGeneratorsFile, "#There are probably new generators functions that you will like to use");
			addToFile(newGeneratorsFile, "#Check them at: https://github.com/Kryniowesegryderiusz/KGenerators/blob/main/core/src/main/resources/generators.yml");
			addToFile(newGeneratorsFile, "");
			
			try {
				boolean now = false;
				ArrayList<String> allLines = (ArrayList<String>) Files.readAllLines(configFile.toPath());
				for (String l : allLines)
				{
					if(now)
					{
						if (l.length() >= 2)
							addToFile(newGeneratorsFile, l.substring(2));
						else
							addToFile(newGeneratorsFile, l);
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
			
			addToFile(newRecipesFile, "#KGenerators made by Kryniowesegryderiusz (Krynio_ on spigot)");
			addToFile(newRecipesFile, "#https://www.spigotmc.org/resources/79246/");
			addToFile(newRecipesFile, "#Need help? Join KGenerators discord: https://discord.gg/BZRjhpP4Ab");
			addToFile(newRecipesFile, "");
			addToFile(newRecipesFile, "#!!! WARNING !!! This file was autoupdated from KGenerators v3 to Kgenerators v4!");
			addToFile(newRecipesFile, "");
			
			try {
				boolean now = false;
				ArrayList<String> allLines = (ArrayList<String>) Files.readAllLines(oldRecipesFile.toPath());
				for (String l : allLines)
				{
					if(now)
					{
						if (l.length() >= 2)
							addToFile(newRecipesFile, l.substring(2));
						else
							addToFile(newRecipesFile, l);
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
			addToFile(file, "#KGenerators made by Kryniowesegryderiusz (Krynio_ on spigot)");
			addToFile(file, "#https://www.spigotmc.org/resources/79246/");
			addToFile(file, "#Need help? Join KGenerators discord: https://discord.gg/BZRjhpP4Ab");
			addToFile(file, "");
			addToFile(file, "#Choose lang file. Default possibilities are en, pl");
			addToFile(file, "lang: " + settings.getLang());
			Logger.info("FilesConverter: Added lang settings to config file");
		}
		
		if (!config.contains("can-generate-instead"))
		{
			addToFile(file, "");
			addToFile(file, "#By default generator will generate block only on air. Here you can add blocks, which will be replaced by generated block.");
			addToFile(file, "can-generate-instead:");
			for (ItemStack i : settings.getGeneratingWhitelist())
			{
				addToFile(file, "  - " + i.getType().toString());
			}
			Logger.info("FilesConverter: Added can-generate-instead settings to config file");
		}
		
		if (!config.contains("can-generate-instead"))
		{
			addToFile(file, "");
			addToFile(file, "#per-player-generators are explained more on: https://github.com/Kryniowesegryderiusz/KGenerators/wiki/Per-Player-Generators");
			addToFile(file, "per-player-generators:");
			addToFile(file, "  #Enables limit, usage and pickup checks");
			addToFile(file, "  enabled: " + settings.isPerPlayerGenerators());
			addToFile(file, "  #Set default overall place limit (count of all player's generators)");
			addToFile(file, "  #Set to -1 to disable that limit");
			addToFile(file, "  #Can be overwritten by permission: kgenerators.overallplacelimit.<limit>");
			addToFile(file, "  overall-place-limit: -1");
			Logger.info("FilesConverter: Added per-player-generators settings to config file");
		}
		
		if (!config.contains("actions"))
		{
			addToFile(file, "");
			addToFile(file, "#This configuration section is for configuring actions needed for particular features");
			addToFile(file, "#Possible modes: BREAK (avaible only for pick-up), LEFT_CLICK, RIGHT_CLICK, NONE (ex. because of gui usage)");
			addToFile(file, "#Item could be \"ANY\"");
			addToFile(file, "#Sneak indicates if shift pressed is required");
			addToFile(file, "actions:");
			addToFile(file, "  #Action, which will be used for picking up generators");
			addToFile(file, "  pick-up:");
			addToFile(file, "    mode: " + settings.getAction(EnumAction.PICKUP).getInteraction().toString());
			if (settings.getAction(EnumAction.PICKUP).getItem() != null)
				addToFile(file, "    item: " + settings.getAction(EnumAction.PICKUP).getItem().getType().toString());
			else
				addToFile(file, "    item: ANY");
			addToFile(file, "    sneak: " + String.valueOf(settings.getAction(EnumAction.PICKUP).isSneak()));
			addToFile(file, "  #Action, which will be used for opening generator gui");
			addToFile(file, "  open-gui:");
			addToFile(file, "    mode: " + settings.getAction(EnumAction.OPENGUI).getInteraction().toString());
			if (settings.getAction(EnumAction.PICKUP).getItem() != null)
				addToFile(file, "    item: " + settings.getAction(EnumAction.OPENGUI).getItem().getType().toString());
			else
				addToFile(file, "    item: ANY");
			addToFile(file, "    sneak: " + String.valueOf(settings.getAction(EnumAction.OPENGUI).isSneak()));
			addToFile(file, "  #Action, which will be used for checking how much time left before regeneration");
			addToFile(file, "  time-left-check:");
			addToFile(file, "    mode: " + settings.getAction(EnumAction.TIMELEFT).getInteraction().toString());
			if (settings.getAction(EnumAction.TIMELEFT).getItem() != null)
				addToFile(file, "    item: " + settings.getAction(EnumAction.PICKUP).getItem().getType().toString());
			else
				addToFile(file, "    item: ANY");
			addToFile(file, "    sneak: " + String.valueOf(settings.getAction(EnumAction.TIMELEFT).isSneak()));
			Logger.info("FilesConverter: Added actions settings to config file");
		}
		
		if (!config.contains("intervals"))
		{
			addToFile(file, "");
			addToFile(file, "#These settings are related to performance and its not recommended to set them too low");
			addToFile(file, "#Values are presented in ticks (20 ticks = 1 second)");
			addToFile(file, "#Changed values need server restart to affect plugin functioning");
			addToFile(file, "intervals:");
			addToFile(file, "  #How often should generators check if they should regenerate?");
			addToFile(file, "  #All generator delays should be a multiple of this number");
			addToFile(file, "  generation-check: 10");
			addToFile(file, "  #How often should holograms be updated?");
			addToFile(file, "  hologram-update: 20");
			addToFile(file, "  #How often should generators guis be updated?");
			addToFile(file, "  #Set -1 to not update GUI");
			addToFile(file, "  gui-update: 20");
			Logger.info("FilesConverter: Added intervals settings to config file");
		}
	}
	
	static void addToFile(File file, String string)
	{
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.APPEND)) {
		    writer.write(string);
		    writer.newLine();
		} catch (IOException ioe) {
		    System.err.format("IOException: %s%n", ioe);
		}
	}
	
}
