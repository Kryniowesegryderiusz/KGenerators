package me.kryniowesegryderiusz.kgenerators.generators.upgrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.PlayerPointsHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.Upgrade;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.UpgradeCostExp;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.UpgradeCostExpLevel;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.UpgradeCostItems;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.UpgradeCostMoney;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;

public class UpgradesManager {

	private HashMap<String, Upgrade> upgrades = new HashMap<String, Upgrade>();

	@Getter
	private UpgradesCostsManager upgradesCostsManager = new UpgradesCostsManager();

	public UpgradesManager() {
		this.reload();
	}

	public void addUpgrade(String generatorId, Upgrade upgrade) {
		upgrades.put(generatorId, upgrade);
	}

	@Nullable
	public Upgrade getUpgrade(String generatorId) {
		return upgrades.get(generatorId);
	}

	@Nullable
	public Upgrade getUpgrade(ItemStack item) {
		ItemStack i = item.clone();
		i.setAmount(1);

		Generator g = Main.getGenerators().get(item);
		if (g == null)
			return null;

		if (getUpgrade(g.getId()) == null)
			return null;
		return getUpgrade(g.getId());
	}

	/**
	 * Checks if generator could be obtained by upgrade
	 * 
	 * @param generatorId
	 * @return
	 */
	public boolean couldBeObtained(String generatorId) {
		for (Entry<String, Upgrade> e : upgrades.entrySet()) {
			if (e.getValue().getNextGeneratorId().equals(generatorId))
				return true;
		}
		return false;
	}

	public String getPreviousGeneratorId(String generatorId) {
		for (Entry<String, Upgrade> e : upgrades.entrySet()) {
			if (e.getValue().getNextGeneratorId().equals(generatorId))
				return e.getKey();
		}
		return "";
	}

	public boolean hasUpgrades() {
		return this.upgrades.size() != 0;
	}

	public void reload() {

		Logger.debugPluginLoad("UpgradesManager: Setting up manager");

		upgrades.clear();

		Config config;

		try {
			config = ConfigManager.getConfig("upgrades.yml", (String) null, true, false);
			config.loadConfig();
		} catch (InvalidConfigurationException e) {
			Logger.error("Generators file: You've missconfigured upgrades.yml file. Check your spaces! More info below. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		} catch (Exception e) {
			Logger.error("Upgrades file: Cant load upgrades. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}

		if (!config.contains("enabled") || config.getBoolean("enabled") != true) {
			Logger.info("Upgrades file: Upgrades are disabled. You can enable them in upgrades.yml");
			return;
		}

		ConfigurationSection mainSection = config.getConfigurationSection("");
		for (String generatorId : mainSection.getKeys(false)) {
			try {
				new Upgrade(this, config, generatorId);
			} catch (CannnotLoadUpgradeException e) {
				Logger.error("Upgrades file: Couldnt load " + generatorId + " upgrade! " + e.getMessage());
			}
		}
		Logger.info("Upgrades file: Loaded " + this.upgrades.size() + " upgrades!");
	}

	public class UpgradesCostsManager {
		private ArrayList<Class<? extends IUpgradeCost>> upgradeCosts = new ArrayList<Class<? extends IUpgradeCost>>();

		public UpgradesCostsManager() {
			this.registerUpgradeCost(UpgradeCostMoney.class);
			this.registerUpgradeCost(UpgradeCostExp.class);
			this.registerUpgradeCost(UpgradeCostExpLevel.class);
			this.registerUpgradeCost(UpgradeCostItems.class);
			if (Main.getDependencies().isEnabled(Dependency.PLAYERPOINTS))
				PlayerPointsHook.registerUpgradeCost(this);
		}

		public <T extends IUpgradeCost> void registerUpgradeCost(Class<T> c) {
			if (!upgradeCosts.contains(c)) {
				upgradeCosts.add(c);
				Logger.debugPluginLoad("Upgrades: Loaded UpgradeCost: " + c.getSimpleName());
			}

		}

		public ArrayList<IUpgradeCost> getUpgradeCosts() {
			ArrayList<IUpgradeCost> newUpgradeCosts = new ArrayList<IUpgradeCost>();

			for (Class<? extends IUpgradeCost> c : upgradeCosts) {
				try {
					newUpgradeCosts.add(c.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					Logger.error("Upgrades: Cannot prepare upgrade cost list for " + c.getSimpleName());
					Logger.error(e);
				}
			}

			return newUpgradeCosts;
		}
	}
}
