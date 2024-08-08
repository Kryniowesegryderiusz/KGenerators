package me.kryniowesegryderiusz.kgenerators.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.data.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.data.objects.SQLConfig;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.ActionType;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.settings.objects.Actions;
import me.kryniowesegryderiusz.kgenerators.settings.objects.GeneratorItemMatcher;
import me.kryniowesegryderiusz.kgenerators.settings.objects.Sound;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;
import me.kryniowesegryderiusz.kgenerators.xseries.XSound;

public class Settings {

    @Setter
    @Getter
    private ArrayList<XMaterial> generatingWhitelist = new ArrayList<XMaterial>();

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
    private boolean blockDropToEq = false;
    @Setter
    @Getter
    private boolean expDropToEq = false;

    @Getter
    private ArrayList<String> disabledWorlds = new ArrayList<String>();

    @Getter
    private HashMap<String, Integer> materialsDelays = new HashMap<>();

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
    private boolean asyncChunkLoading = true;

    @Setter
    @Getter
    private GeneratorItemMatcher generatorItemMatcher = new GeneratorItemMatcher();


    @Getter
    private boolean pluginLoadDebug = true;
    @Getter
    private boolean playersDebug = true;
    @Getter
    private boolean placedGeneratorsManagerDebug = false;
    @Getter
    private boolean schedulesManagerDebug = false;
    @Getter
    private boolean multiVersionManagerDebug = false;

    @Getter
    private boolean warnings = true;

    @SuppressWarnings("unchecked")
    public void reload() {
        Logger.info("Settings: Loading settings here");

        Config config;

        if (!new File(Main.getInstance().getDataFolder(), "config.yml").exists()) {
            Logger.info("Config file: Generating config.yml");
            Main.getInstance().saveResource("config.yml", false);
        }
        try {
            config = ConfigManager.getConfig("config.yml", (String) null, false, false);
            config.loadConfig();
        } catch (InvalidConfigurationException e) {
            Logger.error("Generators file: You've missconfigured config.yml file. Check your spaces! More info below. Disabling plugin.");
            Logger.error(e);
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        } catch (Exception e) {
            Logger.error("Config file: Cant load config. Disabling plugin.");
            Logger.error(e);
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        }

        if (config.contains("debug.plugin-load"))
            this.pluginLoadDebug = config.getBoolean("debug.plugin-load");
        if (config.contains("debug.players"))
            this.playersDebug = config.getBoolean("debug.players");
        if (config.contains("debug.placed-generators-manager"))
            this.placedGeneratorsManagerDebug = config.getBoolean("debug.placed-generators-manager");
        if (config.contains("debug.schedules"))
            this.schedulesManagerDebug = config.getBoolean("debug.schedules");
        if (config.contains("debug.multi-version"))
            this.multiVersionManagerDebug = config.getBoolean("debug.multi-version");
        if (config.contains("debug.warnings"))
            this.warnings = config.getBoolean("debug.warnings");

        if (config.contains("can-generate-instead")) {
            ArrayList<String> tempListString = new ArrayList<String>();
            ArrayList<XMaterial> generatingWhitelist = new ArrayList<XMaterial>();
            tempListString = (ArrayList<String>) config.getList("can-generate-instead");

            for (String s : tempListString) {
                XMaterial xm = ItemUtils.getXMaterial(s, "config.yml can-generate-instead", true);
                generatingWhitelist.add(xm);
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
            this.setBlockDropToEq(config.getBoolean("drop-to-eq"));
        else if (config.contains("block-drop-to-eq"))
            this.setBlockDropToEq(config.getBoolean("block-drop-to-eq"));

        if (config.contains("exp-drop-to-eq"))
            this.setExpDropToEq(config.getBoolean("exp-drop-to-eq"));

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

        if (config.contains("async-chunk-loading"))
            this.asyncChunkLoading = config.getBoolean("async-chunk-loading");

        this.generatorItemMatcher.load(config);

        this.setSqlConfig(new SQLConfig(config));

        Config delays;
        try {
            delays = ConfigManager.getConfig("delays.yml", (String) null, false, false);
            delays.loadConfig();
            for (Map.Entry<String, Object> entry : delays.getValues(false).entrySet()) {
                String material_name = entry.getKey();
                materialsDelays.put(material_name, (Integer) entry.getValue());
            }
//            for (Map.Entry<String, Integer> entry : materialsDelays.entrySet()) {
//                Logger.warn(entry.getKey() + ": " + entry.getValue());
//            }
        } catch (InvalidConfigurationException e) {
            Logger.error("Generators file: You've missconfigured delays.yml file. Check your spaces! More info below. Disabling plugin.");
            Logger.error(e);
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        } catch (Exception e) {
            Logger.error("Config file: Cant load config. Disabling plugin.");
            Logger.error(e);
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        }
    }

    public boolean isWorldDisabled(World world) {
        return this.disabledWorlds.contains(world.getName());
    }

}
