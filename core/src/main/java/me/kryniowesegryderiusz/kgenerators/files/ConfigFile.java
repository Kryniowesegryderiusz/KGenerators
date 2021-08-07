package me.kryniowesegryderiusz.kgenerators.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.enums.Action;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Settings;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.classes.Sound;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class ConfigFile {
	
	@SuppressWarnings("unchecked")
	public static void globalSettingsLoader()
	{
		Config config;
		
    	if (!new File(Main.getInstance().getDataFolder(), "config.yml").exists()){
    		Logger.info("Config file: Generating config.yml");
    		Main.getInstance().saveResource("config.yml", false);
    	}
    	try {
    		config = ConfigManager.getConfig("config.yml", (String) null, false, false);
			config.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error("Config file: Cant load config. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
		
		Settings settings = new Settings();

		if (config.contains("can-generate-instead")) {
			ArrayList<String> tempListString = new ArrayList<String>();
			ArrayList<ItemStack> generatingWhitelist = new ArrayList<ItemStack>();
			tempListString = (ArrayList<String>) config.getList("can-generate-instead");
			
			for (String s : tempListString) {
				ItemStack m = XUtils.parseItemStack(s, "Config file", true);
				generatingWhitelist.add(m);
			}
			settings.setGeneratingWhitelist(generatingWhitelist);
		}
		
		if (config.contains("lang")) {
			settings.setLang(config.getString("lang"));
		}
		
		if (config.contains("generators-actionbar-messages"))
		{
			settings.setActionbarMessages(config.getBoolean("generators-actionbar-messages"));
		}
		
		settings.addGeneratorAction(Action.PICKUP, new GeneratorAction(Action.PICKUP, config, "actions.pick-up"));
		settings.addGeneratorAction(Action.OPENGUI, new GeneratorAction(Action.OPENGUI, config, "actions.open-gui"));
		settings.addGeneratorAction(Action.TIMELEFT, new GeneratorAction(Action.TIMELEFT, config, "actions.time-left-check"));
		
		if (config.contains("explosion-handler"))
		{
			settings.setExplosionHandler((short) config.getInt("explosion-handler"));
		}
		
		if (config.contains("intervals.hologram-update"))
			settings.setHologramUpdateFrequency(config.getInt("intervals.hologram-update"));
		if (config.contains("intervals.generation-check"))
			settings.setHologramUpdateFrequency(config.getInt("intervals.generation-check"));
		if (config.contains("intervals.gui-update"))
			settings.setHologramUpdateFrequency(config.getInt("intervals.gui-update"));
		
		if (config.contains("pick-up-to-eq"))
		{
			settings.setPickUpToEq(config.getBoolean("pick-up-to-eq"));
		}
		
		if (config.contains("disabled-worlds") )
		{
			settings.getDisabledWorlds().addAll((ArrayList<String>) config.getList("disabled-worlds"));
		}
		
		if (config.contains("disabled-worlds") )
		{
			settings.getDisabledWorlds().addAll((ArrayList<String>) config.getList("disabled-worlds"));
		}
		
		if (config.contains("sounds.place"))
			settings.setPlaceSound(new Sound(config, "sounds.place"));
		
		if (config.contains("sounds.pick-up"))
			settings.setPickupSound(new Sound(config, "sounds.pick-up"));
		
		if (config.contains("sounds.upgrade"))
			settings.setUpgradeSound(new Sound(config, "sounds.upgrade"));
		
		Main.setSettings(settings);
	}

}
