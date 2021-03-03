package me.kryniowesegryderiusz.kgenerators.files;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;

public class LangFiles {
	
	public static void loadLang()
	{
		Config config;
		Config configGui;

    	FilesFunctions.mkdir("lang");

    	/*
    	 * Adding resource lang files
    	 */
    	if (!new File(Main.getInstance().getDataFolder(), "lang/en.yml").exists())
    		Main.getInstance().saveResource("lang/en.yml", false);
    	if (!new File(Main.getInstance().getDataFolder(), "lang/pl.yml").exists())
    		Main.getInstance().saveResource("lang/pl.yml", false);
    	if (!new File(Main.getInstance().getDataFolder(), "lang/en_gui.yml").exists())
    		Main.getInstance().saveResource("lang/en_gui.yml", false);
    	if (!new File(Main.getInstance().getDataFolder(), "lang/pl_gui.yml").exists())
    		Main.getInstance().saveResource("lang/pl_gui.yml", false);
    	
    	/*
    	 * Loading configs
    	 */
    	
    	try {
    		config = (ConfigManager.getConfig(Main.getSettings().getLang()+".yml", "/lang", false, false));
    		config.loadConfig();
		} catch (IOException | InvalidConfigurationException e1) {
			Logger.error("Lang: Cant find lang file " + Main.getSettings().getLang());
			return;
		}
    	
    	try {
    		configGui = (ConfigManager.getConfig(Main.getSettings().getLang()+"_gui.yml", "/lang", false, false));
    		configGui.loadConfig();
		} catch (IOException | InvalidConfigurationException e1) {
			Logger.error("Lang: Cant find gui lang file " + Main.getSettings().getLang());
			return;
		}
    	
    	try {
			Lang.loadMessages(config, configGui);
		} catch (IOException e) {
			Logger.error(e);
		}
    	Logger.info("Lang: Messages and Guis file loaded with lang: " + Main.getSettings().getLang());
	}

}
