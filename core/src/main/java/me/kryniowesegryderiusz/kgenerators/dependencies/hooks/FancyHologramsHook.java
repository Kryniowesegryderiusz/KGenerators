package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.Color;

import de.oliver.fancyholograms.api.hologram.Hologram;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;

public class FancyHologramsHook {
	
	public static Color HOLOGRAM_COLOR;
	
	public static void loadConfigValues() {
		Config config;

		try {
			config = ConfigManager.getConfig("config.yml", (String) null, false, false);
			config.loadConfig();
		} catch (Exception e) {
			Logger.error("FancyHologramsHook: Cant load config. Using default values.");
			Logger.error(e);
			return;
		}

		if (config.contains("fancy-holograms")) {
			boolean transparent = config.getBoolean("fancy-holograms.background.transparent");
			int red = config.getInt("fancy-holograms.background.red");
			int green = config.getInt("fancy-holograms.background.green");
			int blue = config.getInt("fancy-holograms.background.blue");
			
			if (transparent) {
				HOLOGRAM_COLOR = Hologram.TRANSPARENT;
			} else if (red >= 0 && green >= 0 && blue >= 0) {
				HOLOGRAM_COLOR = Color.fromRGB(red, green, blue);
			}
			
		} else {
			Logger.error("FancyHologramsHook: Autogenerating additional config options.");
			FilesUtils.addToFile(config.getFile(), "");
			FilesUtils.addToFile(config.getFile(), "");
			FilesUtils.addToFile(config.getFile(), "#Autogenerated section for FancyHolograms hook settings");
			FilesUtils.addToFile(config.getFile(), "fancy-holograms:");
			FilesUtils.addToFile(config.getFile(), "  background:");
			FilesUtils.addToFile(config.getFile(), "    transparent: false");
			FilesUtils.addToFile(config.getFile(), "    red: -1");
			FilesUtils.addToFile(config.getFile(), "    green: -1");
			FilesUtils.addToFile(config.getFile(), "    blue: -1");
		}
	}

}
