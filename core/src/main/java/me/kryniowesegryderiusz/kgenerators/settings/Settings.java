package me.kryniowesegryderiusz.kgenerators.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.data.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.data.objects.SQLConfig;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.ActionType;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.settings.objects.Actions;
import me.kryniowesegryderiusz.kgenerators.settings.objects.Sound;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.xseries.XSound;

public class Settings {

	@Setter
	@Getter
	private ArrayList<ItemStack> generatingWhitelist = new ArrayList<ItemStack>();

	@Setter
	@Getter
	private String lang = "en";

	@Setter
	@Getter
	private boolean actionbarMessages = true;

	@Getter
	private Actions actions = new Actions();

	@Setter
	@Getter
	private short explosionHandler = 0;

	@Setter
	@Getter
	private boolean pickUpToEq = true;
	@Setter
	@Getter
	private boolean blockDropUpToEq = false;

	@Getter
	private ArrayList<String> disabledWorlds = new ArrayList<String>();

	@Setter
	@Getter
	private int generationCheckFrequency = 10;
	@Setter
	@Getter
	private int hologramUpdateFrequency = 20;
	@Setter
	@Getter
	private int guiUpdateFrequency = 20;

	@Setter
	@Getter
	private HashMap<ActionType, GeneratorAction> guis = new HashMap<ActionType, GeneratorAction>();

	@Setter
	@Getter
	private Sound placeSound = new Sound(XSound.BLOCK_ANVIL_LAND);
	@Setter
	@Getter
	private Sound pickupSound = new Sound(XSound.ENTITY_BAT_TAKEOFF);
	@Setter
	@Getter
	private Sound upgradeSound = new Sound(XSound.ENTITY_PLAYER_LEVELUP);

	@Setter
	@Getter
	private DatabaseType dbType = DatabaseType.SQLITE;
	@Setter
	@Getter
	private SQLConfig sqlConfig;

	@Getter
	private boolean adjustDelayOnUnloadedChunks = true;

	@Getter
	private boolean lazyChunkLoading = false;

	public Settings() {
		Logger.debug("Settings: Loading settings");

		Config config;

		if (!new File(Main.getInstance().getDataFolder(), "config.yml").exists()) {
			Logger.info("Config file: Generating config.yml");
			Main.getInstance().saveResource("config.yml", false);
		}
		try {
			config = ConfigManager.getConfig("config.yml", (String) null, false, false);
			config.loadConfig();
		} catch (Exception e) {
			Logger.error("Config file: Cant load config. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}

		if (config.contains("can-generate-instead")) {
			ArrayList<String> tempListString = new ArrayList<String>();
			ArrayList<ItemStack> generatingWhitelist = new ArrayList<ItemStack>();
			tempListString = (ArrayList<String>) config.getList("can-generate-instead");

			for (String s : tempListString) {
				ItemStack m = ItemUtils.parseItemStack(s, "Config file", true);
				generatingWhitelist.add(m);
			}
			this.setGeneratingWhitelist(generatingWhitelist);
		}

		if (config.contains("lang"))
			this.setLang(config.getString("lang"));

		if (config.contains("generators-actionbar-messages"))
			this.setActionbarMessages(config.getBoolean("generators-actionbar-messages"));

		this.getActions().load(config, "");

		if (config.contains("explosion-handler"))
			this.setExplosionHandler((short) config.getInt("explosion-handler"));

		if (config.contains("count-delay-on-unloaded-chunks"))
			this.adjustDelayOnUnloadedChunks = config.getBoolean("count-delay-on-unloaded-chunks");

		if (config.contains("intervals.hologram-update"))
			this.setHologramUpdateFrequency(config.getInt("intervals.hologram-update"));
		if (config.contains("intervals.generation-check"))
			this.setGenerationCheckFrequency(config.getInt("intervals.generation-check"));
		if (config.contains("intervals.gui-update"))
			this.setGuiUpdateFrequency(config.getInt("intervals.gui-update"));

		if (config.contains("pick-up-to-eq"))
			this.setPickUpToEq(config.getBoolean("pick-up-to-eq"));

		if (config.contains("drop-to-eq"))
			this.setBlockDropUpToEq(config.getBoolean("drop-to-eq"));
		else if (config.contains("block-drop-to-eq"))
			this.setBlockDropUpToEq(config.getBoolean("block-drop-to-eq"));

		if (config.contains("disabled-worlds"))
			this.getDisabledWorlds().addAll((ArrayList<String>) config.getList("disabled-worlds"));

		if (config.contains("sounds.place"))
			this.setPlaceSound(new Sound(config, "sounds.place"));

		if (config.contains("sounds.pick-up"))
			this.setPickupSound(new Sound(config, "sounds.pick-up"));

		if (config.contains("sounds.upgrade"))
			this.setUpgradeSound(new Sound(config, "sounds.upgrade"));

		if (config.contains("database.dbtype"))
			this.setDbType(DatabaseType.Functions.getTypeByString(config.getString("database.dbtype")));
		
		if (config.contains("lazy-chunk-loading"))
			this.lazyChunkLoading = config.getBoolean("lazy-chunk-loading");

		this.setSqlConfig(new SQLConfig(config));
	}

	public boolean isWorldDisabled(World world) {
		return this.disabledWorlds.contains(world.getName());
	}

}
