package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.NBTAPIHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.GeneratorsManager;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.Upgrade;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.settings.objects.Actions;
import me.kryniowesegryderiusz.kgenerators.settings.objects.GeneratorItemMatcher;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.RandomSelector;
import me.kryniowesegryderiusz.kgenerators.utils.objects.CustomBlockData;
import com.cryptomorin.xseries.XMaterial;

public class Generator {

	@Getter
	private String id;
	private ItemStack generatorItem;
	@Getter
	@Setter
	private int delay;
	@Getter
	private GeneratorType type = GeneratorType.SINGLE;
	private LinkedHashMap<AbstractGeneratedObject, Double> chances = new LinkedHashMap<AbstractGeneratedObject, Double>();

	@Getter
	private CustomBlockData placeholder;
	@Getter
	private boolean generateImmediatelyAfterPlace = true;
	@Getter
	private boolean allowPistonPush = true;
	@Getter
	private boolean hologram = false;
	@Getter
	private Actions actions = null;
	@Getter
	private ArrayList<String> disabledWorlds = new ArrayList<String>();

	@Getter
	private boolean secondMultiple = false;
	private double fullChance = 0.0;
	@Getter
	boolean loaded = false;

	public Generator(GeneratorsManager generatorsManager, Config config, String generatorID) {
		if (!this.loadConfiguration(generatorsManager, config, generatorID)) {
			Logger.error("Generators file: Couldnt load " + generatorID);
		} else {
			generatorsManager.add(generatorID, this);
		}
	}

	/**
	 * 
	 * @param config
	 * @param generatorID
	 * @return true if generator loaded properly
	 */
	@SuppressWarnings("unchecked")
	public boolean loadConfiguration(GeneratorsManager generatorsManager, Config config, String generatorID) {

		Boolean error = false;

		/*
		 * Required options
		 */

		this.id = generatorID;

		this.delay = config.getInt(generatorID + ".delay");
		double sm = (double) delay / 20;
		if (sm == Math.floor(sm))
			secondMultiple = true;

		this.type = GeneratorType.getGeneratorTypeByString(config.getString(generatorID + ".type"));

		/*
		 * Generator item
		 */
		if (config.contains(generatorID + ".generator-item")) {
			this.generatorItem = FilesUtils.loadItemStack(config, generatorID, "generator-item", true);
		} else if (config.contains(generatorID + ".generator")) {
			Logger.error("Generator " + generatorID
					+ " uses old generator item configuration that will be not supported in the future!");
			Logger.error(
					"Check https://github.com/Kryniowesegryderiusz/KGenerators/blob/main/core/src/main/resources/generators.yml#L14 for new configuration options!");

			Boolean glow = true;
			if (config.contains(generatorID + ".glow")) {
				glow = config.getBoolean(generatorID + ".glow");
			}
			this.generatorItem = ItemUtils.parseItemStack(config.getString(generatorID + ".generator"),
					"Generators file", true);
			ArrayList<String> loreGot = new ArrayList<String>();
			ArrayList<String> lore = new ArrayList<String>();
			ItemMeta meta = (ItemMeta) this.generatorItem.getItemMeta();
			meta.setDisplayName(
					Main.getMultiVersion().getChatUtils().colorize(config.getString(generatorID + ".name")));
			loreGot = (ArrayList<String>) config.getList(generatorID + ".lore");
			for (String l : loreGot) {
				l = Main.getMultiVersion().getChatUtils().colorize(l);
				lore.add(l);
			}
			meta.setLore(lore);
			lore.clear();
			if (glow)
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			this.generatorItem.setItemMeta(meta);
			if (glow)
				this.generatorItem.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
		} else {
			Logger.error("Generator " + generatorID + " is lacking generator-item configuration");
			error = true;
		}

		if (this.generatorItem != null)
			NBTAPIHook.addNBT(generatorItem, generatorID, GeneratorItemMatcher.GENERATOR_ID_NBT, generatorID);

		/*
		 * Generates section
		 */

		chances.clear();
		fullChance = 0.0;

		if (config.contains(generatorID + ".generates"))
			for (Map<?, ?> generatedObjectConfig : (List<Map<?, ?>>) config.getMapList(generatorID + ".generates")) {

				AbstractGeneratedObject ago = generatorsManager.getGeneratedObjectsManager()
						.getNewObject((String) generatedObjectConfig.get("type"));
				if (ago != null && ago.load(generatedObjectConfig)) {
					this.chances.put(ago, ago.getChance());
					this.fullChance += ago.getChance();
				}

			}
		else if (config.contains(generatorID + ".chances")) {
			Logger.error("Generator " + generatorID
					+ " has OLD generates (chances) section set, which allow only block generation!");
			Logger.error(
					"Check https://github.com/Kryniowesegryderiusz/KGenerators/blob/main/core/src/main/resources/generators.yml#L32 for new generators.yml look!");
		} else {
			Logger.error("Generators file: " + generatorID + " doesnt have generating section!");
			error = true;
		}

		if (this.chances.isEmpty()) {
			Logger.error("Generators file: " + generatorID + " doesnt have any generating objects set!");
			error = true;
		}

		/*
		 * Optional options
		 */

		if (config.contains(generatorID + ".placeholder")
				&& !config.getString(generatorID + ".placeholder").isEmpty()) {
			this.placeholder = CustomBlockData.load(config.getString(generatorID + ".placeholder"), "Generators file");
			if (this.placeholder.getXMaterial() == XMaterial.AIR)
				this.placeholder = null;
		}

		if (config.contains(generatorID + ".generate-immediately-after-place")
				&& !config.getString(generatorID + ".generate-immediately-after-place").isEmpty()) {
			this.generateImmediatelyAfterPlace = config.getBoolean(generatorID + ".generate-immediately-after-place");
		}

		if (config.contains(generatorID + ".allow-piston-push")) {
			this.allowPistonPush = config.getBoolean(generatorID + ".allow-piston-push");
		}

		if (config.contains(generatorID + ".hologram")) {
			this.hologram = config.getBoolean(generatorID + ".hologram");
		}

		if (config.contains(generatorID + ".disabled-worlds")) {
			this.getDisabledWorlds().addAll((ArrayList<String>) config.getList(generatorID + ".disabled-worlds"));
		}

		if (config.contains(generatorID + ".actions")) {
			this.actions = new Actions();
			this.actions.load(config, generatorID);
		}

		String doubledGeneratorId = generatorsManager.exactGeneratorItemExists(generatorID, this.getGeneratorItem());
		if (doubledGeneratorId != null) {
			Logger.error("Generators file: " + generatorID + " has same generator item as " + doubledGeneratorId);
			Logger.error("Generators file: Couldnt load " + generatorID);
			error = true;
		}

		if (error) {
			Logger.error("Generators file: An error appeared, while loading configuration for " + generatorID);
			return false;
		} else {
			Logger.debugPluginLoad("Generators file: Loaded properly " + type + " " + generatorID
					+ " generating variety of " + this.chances.size() + " objects every " + delay + " ticks");
			return true;
		}

	}

	public boolean doesChancesContain(AbstractGeneratedObject generatedObject) {
		for (Entry<AbstractGeneratedObject, Double> e : chances.entrySet()) {
			if (e.getKey().equals(generatedObject))
				return true;
		}
		return false;
	}

	public double getChancePercent(AbstractGeneratedObject ago) {
		double chance = chances.get(ago);
		double fullchance = this.fullChance;
		double percent = (chance / fullchance) * 100;
		return percent;
	}

	public String getChancePercentFormatted(AbstractGeneratedObject ago) {
		Double percent = this.getChancePercent(ago);
		if (percent.isInfinite())
			return "100";
		return String.format("%.2f", percent);
	}

	public Set<Entry<AbstractGeneratedObject, Double>> getChancesEntryset() {
		return this.chances.entrySet();
	}

	public Set<AbstractGeneratedObject> getGeneratedObjects() {
		return this.chances.keySet();
	}

	public AbstractGeneratedObject drawGeneratedObject() {
		RandomSelector<AbstractGeneratedObject> selector = RandomSelector.weighted(this.chances.keySet(),
				s -> this.chances.get(s));
		return selector.next(new Random());
	}

	public ItemStack getGeneratorItem() {
		return this.generatorItem.clone();
	}

	public String getGeneratorItemName() {
		return this.generatorItem.getItemMeta().getDisplayName();
	}

	public Upgrade getUpgrade() {
		return Main.getUpgrades().getUpgrade(id);
	}

	public boolean isWorldDisabled(World w) {
		return Main.getSettings().isWorldDisabled(w) || this.disabledWorlds.contains(w.getName());
	}

	public boolean isPlaceholder(ItemStack item) {
		return this.placeholder != null && this.placeholder.isSimilar(item);
	}

	public int getGeneratedObjectId(AbstractGeneratedObject ago1) {

		int id = -1;
		for (AbstractGeneratedObject ago2 : this.chances.keySet()) {
			id++;
			if (ago1 == ago2) {
				return id;
			}
		}
		return -1;
	}

	public AbstractGeneratedObject getGeneratedObjectById(int id) {
		int i = -1;
		for (AbstractGeneratedObject ago : this.chances.keySet()) {
			i++;
			if (id == i)
				return ago;
		}
		return null;
	}
}
