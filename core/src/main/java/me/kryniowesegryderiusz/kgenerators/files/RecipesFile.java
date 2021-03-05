package me.kryniowesegryderiusz.kgenerators.files;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;

public class RecipesFile {
	
	public static void loadRecipes()
	{
		
		Config config;

    	try {
    		config = ConfigManager.getConfig("recipes.yml", (String) null, true, false);
			config.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error("Recipes file: Cant load recipes config. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
    	
    	Main.getRecipesLoader().loadRecipes(config);
	}

}
