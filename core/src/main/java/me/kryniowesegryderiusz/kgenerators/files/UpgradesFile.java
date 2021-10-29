package me.kryniowesegryderiusz.kgenerators.files;

import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Upgrade;
import me.kryniowesegryderiusz.kgenerators.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;

public class UpgradesFile {
	
	public static void load()
	{
		Config config;

    	try {
    		config = ConfigManager.getConfig("upgrades.yml", (String) null, true, false);
			config.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error("Upgrades file: Cant load upgrades. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
		
		Upgrades.clear();
		
		int amount = 0;
		
    	ConfigurationSection mainSection = config.getConfigurationSection("");
    	for(String generatorId: mainSection.getKeys(false)){
    		
    		if (!generatorId.equals("example_generator_id_level_1"))
    		{
				try {
					Upgrades.addUpgrade(generatorId, new Upgrade(generatorId, config));
					amount++;
				} catch (CannnotLoadUpgradeException e) {
					Logger.error("Upgrades file: Couldnt load " + generatorId + " upgrade!");
					Logger.error(e.getMessage());
				}
    		}
    	}
    	
    	Logger.info("Upgrades file: Loaded " + String.valueOf(amount) + " upgrades");
	}

}
