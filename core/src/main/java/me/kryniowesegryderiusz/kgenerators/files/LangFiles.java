package me.kryniowesegryderiusz.kgenerators.files;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;

public class LangFiles {
	
	public static void loadLang()
	{
		Config config;
		Config configGui;
		Config configCustomNames;

    	FilesFunctions.mkdir("lang");

    	String[] langs = {"en", "pl", "ro", "vi"}; 
    	
    	/*
    	 * Adding resource lang files
    	 */
    	
    	for (String l : langs)
    	{
        	if (!new File(Main.getInstance().getDataFolder(), "lang/"+l+".yml").exists())
        		Main.getInstance().saveResource("lang/"+l+".yml", false);
        	if (!new File(Main.getInstance().getDataFolder(), "lang/"+l+"_gui.yml").exists())
        		Main.getInstance().saveResource("lang/"+l+"_gui.yml", false);
        	if (!new File(Main.getInstance().getDataFolder(), "lang/"+l+"_custom_names.yml").exists())
        		Main.getInstance().saveResource("lang/"+l+"_custom_names.yml", false);
    	}
    	
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
    		configCustomNames = (ConfigManager.getConfig(Main.getSettings().getLang()+"_custom_names.yml", "/lang", false, false));
    		configCustomNames.loadConfig();
		} catch (IOException | InvalidConfigurationException e1) {
			Logger.error("Lang: Cant find custom names lang file " + Main.getSettings().getLang());
			return;
		}
    	
    	try {
			Lang.setup(config, configGui, configCustomNames);
		} catch (IOException e) {
			Logger.error(e);
		}
    	Logger.info("Lang: Messages and Guis file loaded with lang: " + Main.getSettings().getLang());
	}

}
