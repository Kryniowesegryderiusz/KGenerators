package me.kryniowesegryderiusz.kgenerators.generators.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.GeneratedItemsAdderBlock;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.GeneratedNexoBlock;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.GeneratedOraxenBlock;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratedBlock;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratedEntity;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratedItem;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;

public class GeneratorsManager {

	private LinkedHashMap<String, Generator> generators = new LinkedHashMap<String, Generator>();

	@Getter
	private GeneratedObjectsManager generatedObjectsManager;

	public GeneratorsManager() {
		Logger.debugPluginLoad("GeneratorsManager: Setting up manager");
		this.generatedObjectsManager = new GeneratedObjectsManager();
		this.generatedObjectsManager.registerGeneratedObject(GeneratedBlock.class);
		this.generatedObjectsManager.registerGeneratedObject(GeneratedItem.class);
		this.generatedObjectsManager.registerGeneratedObject(GeneratedEntity.class);
		if (Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER))
			this.generatedObjectsManager.registerGeneratedObject(GeneratedItemsAdderBlock.class);
		if (Main.getDependencies().isEnabled(Dependency.ORAXEN))
			this.generatedObjectsManager.registerGeneratedObject(GeneratedOraxenBlock.class);
		if (Main.getDependencies().isEnabled(Dependency.NEXO))
			this.generatedObjectsManager.registerGeneratedObject(GeneratedNexoBlock.class);
		this.reload();
	}

	public void add(String id, Generator generator) {
		generators.put(id, generator);
	}

	@Nullable
	public Generator get(String id) {
		return generators.get(id);
	}

	@Nullable
	public Generator get(ItemStack item) {
		return Main.getSettings().getGeneratorItemMatcher().getGenerator(item);
	}

	public Collection<Generator> getAll() {
		return generators.values();
	}

	public Set<Entry<String, Generator>> getEntrySet() {
		return generators.entrySet();
	}

	public Set<Entry<String, Generator>> getSpecifiedEntrySet(int firstGeneratorNr, int numberOfGenerators) {
		LinkedHashMap<String, Generator> gens = new LinkedHashMap<String, Generator>();
		int nr = 0;
		for (Entry<String, Generator> e : generators.entrySet()) {
			if (nr >= firstGeneratorNr) {
				if (nr < firstGeneratorNr + numberOfGenerators)
					gens.put(e.getKey(), e.getValue());
				else
					break;
			}
			nr++;
		}
		return gens.entrySet();
	}

	public boolean exists(String id) {
		if (generators.containsKey(id))
			return true;
		return false;
	}

	/**
	 * Checks if generator item exists in other generator
	 * 
	 * @param generatorId
	 * @param item
	 * @return generatorId of doubled recipe, otherwise null
	 */
	public String exactGeneratorItemExists(String generatorId, ItemStack item) {
		for (Entry<String, Generator> entry : getEntrySet()) {
			if (entry.getValue().getGeneratorItem().equals(item) && !entry.getKey().equals(generatorId))
				return entry.getKey();
		}
		return null;
	}

	public int getAmount() {
		return generators.size();
	}

	public int getAmount(GeneratorType type) {
		int amount = 0;
		for (Entry<String, Generator> g : getEntrySet()) {
			if (g.getValue().getType() == type) {
				amount++;
			}
		}
		return amount;
	}

	public void reload() {
		
		LinkedHashMap<String, Generator> oldGenerators = new LinkedHashMap<String, Generator>();
		oldGenerators.putAll(generators);

		generators.clear();

		Config config;

		try {
			config = ConfigManager.getConfig("generators.yml", (String) null, true, false);
			config.loadConfig();
		} catch (InvalidConfigurationException e) {
			Logger.error("Generators file: You've missconfigured generators.yml file. Check your spaces! More info below. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		} catch (Exception e) {
			Logger.error("Generators file: Cant load generators config. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}

		ConfigurationSection mainSection = config.getConfigurationSection("");
		for (String generatorID : mainSection.getKeys(false)) {
			if (!generatorID.equals("example_generator")) {
				if (oldGenerators.containsKey(generatorID)) {
					Logger.debugPluginLoad("Generators file: " + generatorID + " is already loaded - updating it!");
					oldGenerators.get(generatorID).loadConfiguration(this, config, generatorID);
					generators.put(generatorID, oldGenerators.get(generatorID));
				} else
					new Generator(this, config, generatorID);
			}
		}

		Logger.info("Generators file: Loaded " + this.generators.size() + " generators!");
	}

	public class GeneratedObjectsManager {

		private HashMap<String, Class<?>> generatedObjects = new HashMap<String, Class<?>>();

		public <T extends AbstractGeneratedObject> void registerGeneratedObject(Class<T> c) {
			try {
				AbstractGeneratedObject ago = c.newInstance();
				this.generatedObjects.put(ago.getType(), c);
				Logger.debugPluginLoad("GeneratedObjectsManager: Loaded " + c.getSimpleName() + " with type " + ago.getType());
			} catch (Exception e) {
				Logger.error("GeneratedObjectsManager: Cannot initialise GeneratedObject: " + c.getSimpleName());
				Logger.error(e);
			} catch (Error e) {
				Logger.error("GeneratedObjectsManager: Cannot initialise GeneratedObject: " + c.getSimpleName());
				Logger.error(e);
			}
		}

		public AbstractGeneratedObject getNewObject(String type) {
			if (type != null && !type.isEmpty() && this.generatedObjects.containsKey(type)) {
				try {
					return (AbstractGeneratedObject) this.generatedObjects.get(type).newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					Logger.error("Generators file: Cant create new GeneratedObject with type: " + type);
					Logger.error(e);
				}
			} else {
				Logger.error("Generators file: There isnt any possible object with type: " + type
						+ "! GeneratedObject not loaded. Possible types: " + this.generatedObjects.keySet());
			}
			return null;
		}
	}
}
