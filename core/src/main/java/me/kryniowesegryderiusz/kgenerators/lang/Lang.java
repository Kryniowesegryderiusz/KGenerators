package me.kryniowesegryderiusz.kgenerators.lang;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.lang.enums.HologramText;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.lang.storages.CustomNamesStorage;
import me.kryniowesegryderiusz.kgenerators.lang.storages.HologramTextStorage;
import me.kryniowesegryderiusz.kgenerators.lang.storages.MenuInventoryStorage;
import me.kryniowesegryderiusz.kgenerators.lang.storages.MenuItemAdditionalLinesStorage;
import me.kryniowesegryderiusz.kgenerators.lang.storages.MenuItemStorage;
import me.kryniowesegryderiusz.kgenerators.lang.storages.MessageStorage;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;

public class Lang {

	@Getter private static MessageStorage messageStorage;
	
	@Getter private static MenuInventoryStorage menuInventoryStorage;
	@Getter private static MenuItemStorage menuItemStorage;
	@Getter private static MenuItemAdditionalLinesStorage menuItemAdditionalLinesStorage;
	
	@Getter private static HologramTextStorage hologramTextStorage;
	@Getter private static CustomNamesStorage customNamesStorage;
	
	public static void loadFromFiles() {
		Config config;
		Config configGui;
		Config configCustomNames;

    	FilesUtils.mkdir("lang");

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
    	
    	messageStorage = new MessageStorage(config);
		messageStorage.register(Message.class);
		
		menuInventoryStorage = new MenuInventoryStorage(configGui);
		menuInventoryStorage.register(MenuInventoryType.class);
		menuItemStorage = new MenuItemStorage(configGui);
		menuItemStorage.register(MenuItemType.class);
		menuItemAdditionalLinesStorage = new MenuItemAdditionalLinesStorage(configGui);
		menuItemAdditionalLinesStorage.register(MenuItemAdditionalLines.class);
		
		hologramTextStorage = new HologramTextStorage(config);
		hologramTextStorage.register(HologramText.class);
		customNamesStorage = new CustomNamesStorage(configCustomNames);
		
    	Logger.info("Lang: Messages and Guis file loaded with lang: " + Main.getSettings().getLang());
	}	
}
