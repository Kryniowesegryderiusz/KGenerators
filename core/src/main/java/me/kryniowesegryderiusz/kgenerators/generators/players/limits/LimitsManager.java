package me.kryniowesegryderiusz.kgenerators.generators.players.limits;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.Limit;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;

public class LimitsManager {

	private LinkedHashMap<String, Limit> limits = new LinkedHashMap<String, Limit>();

	public LimitsManager() {
		Config config;

		try {
			config = ConfigManager.getConfig("limits.yml", (String) null, true, false);
			config.loadConfig();
		} catch (Exception e) {
			Logger.error("Limits file: Cant load limits. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}

		if (!config.contains("enabled") || config.getBoolean("enabled") != true) {
			Logger.info("Limits file: Limits are disabled. You can enable them in limits.yml!");
			return;
		}

		ConfigurationSection mainSection = config.getConfigurationSection("");
		for (String id : mainSection.getKeys(false)) {
			if (!id.equals("example_limit_group") && !id.equals("enabled")) {
				new Limit(this, config, id);
			}
		}
		Logger.info("Limits file: Loaded " + this.limits.size() + " limits");
	}

	public void add(Limit limit) {
		limits.put(limit.getId(), limit);
	}

	public Limit get(String id) {
		return limits.get(id);
	}

	public Set<Entry<String, Limit>> getEntrySet() {
		return limits.entrySet();
	}

	public Collection<Limit> getValues() {
		return limits.values();
	}

	/**
	 * Checks if any limit has only owner use in context of generator
	 * 
	 * @param generator
	 * @return
	 */
	public boolean isOnlyOwnerUse(Generator g) {
		for (Limit l : limits.values()) {
			if (l.getGenerators().contains(g) && l.isOnlyOwnerUse())
				return true;
		}
		return false;
	}

	public void clear() {
		limits.clear();
	}

	public boolean hasLimits() {
		return this.limits.size() != 0;
	}

}
