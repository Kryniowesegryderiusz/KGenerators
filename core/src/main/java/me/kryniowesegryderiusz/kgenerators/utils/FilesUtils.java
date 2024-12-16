package me.kryniowesegryderiusz.kgenerators.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.NBTAPIHook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import com.cryptomorin.xseries.XEnchantment;

public class FilesUtils {

	public static void mkdir(String dir) {
		File mainFolder = new File(Main.getInstance().getDataFolder() + "");
		if (!mainFolder.exists())
			mainFolder.mkdir();

		File file = new File(Main.getInstance().getDataFolder() + "/" + dir);

		if (file.exists())
			return;

		try {
			if (!file.mkdir()) {
				Logger.error("FilesUtils: Directory for " + dir + "wasnt created!");
			}
		} catch (Exception e) {
			Logger.error("FilesUtils: Can not create directory for " + dir);
			Logger.error(e);
		}
	}

	public static void changeText(File file, String... keys) {

		ArrayList<String> replacables = new ArrayList<>(Arrays.asList(keys));

		try {
			List<String> content = Files.readAllLines(file.toPath());
			ArrayList<String> newContent = new ArrayList<String>();
			for (String s : content) {
				String line = s;

				for (int i = 0; i < replacables.size(); i = i + 2) {
					line = line.replace(replacables.get(i), replacables.get(i + 1));
				}

				newContent.add(line);
			}

			Files.write(file.toPath(), newContent, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Logger.error("FilesUtils: Cannot replace text in " + file.getPath());
			Logger.error(e);
		}
	}

	public static void addToFile(File file, String string) {
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.APPEND)) {
			writer.write(string);
			writer.newLine();
		} catch (IOException e) {
			Logger.error("FilesUtils: Cannot add text to " + file.getPath());
			Logger.error(e);
		}
	}

	/**
	 * Loads ItemStack from config
	 * 
	 * @param config
	 * @param path         - path of object with item key
	 * @param key          - config key of item configuration
	 * @param isBlockCheck - should this item be a block
	 * @return ItemStack
	 */
	public static ItemStack loadItemStack(Config config, String path, String key, boolean isBlockCheck) {
		return loadItemStack(config.getConfigurationSection(path).getValues(false), key, config.getName() + "#" + path,
				isBlockCheck);
	}

	/*
	 * public static ItemStack loadItemStack(Map<?,?> generalConfig, String place,
	 * boolean isBlockCheck) { if (generalConfig.get("item") != null) if
	 * (generalConfig.get("item") instanceof String) return
	 * ItemUtils.parseItemStack((String) generalConfig.get("item"), place, false);
	 * else return FilesUtils.loadItemStack((Map<?, ?>) generalConfig.get("item"),
	 * place, false); else if (generalConfig.get("material") != null) if
	 * (generalConfig.get("material") instanceof String) return
	 * ItemUtils.parseItemStack((String) generalConfig.get("material"), place,
	 * false); else return FilesUtils.loadItemStack((Map<?, ?>)
	 * generalConfig.get("material"), place, false); else return null; }
	 */

	/**
	 * Loads ItemStack from mapped config
	 * 
	 * @param map          - map of object with item key
	 * @param key          - config key of item configuration
	 * @param place        - where is this method fired from
	 * @param isBlockCheck - should this item be a block
	 * @return ItemStack
	 */
	public static ItemStack loadItemStack(Map<?, ?> map, String key, String place, boolean isBlockCheck) {
		if (map.get(key) instanceof String)
			return ItemUtils.parseItemStack((String) map.get(key), place, isBlockCheck);
		else if (map.get(key) instanceof MemorySection)
			return loadItemStack((Map<?, ?>) ((MemorySection) map.get(key)).getValues(false), place, isBlockCheck);
		else
			return loadItemStack((Map<?, ?>) map.get(key), place, isBlockCheck);
	}

	/**
	 * Loads ItemStack from mapped item
	 * 
	 * @param map          - map of item
	 * @param place        - where is this method fired from
	 * @param isBlockCheck - should this item be a block
	 * @return ItemStack
	 */
	@SuppressWarnings("unchecked")
	private static ItemStack loadItemStack(Map<?, ?> map, String place, boolean isBlockCheck) {

		ItemStack item = null;
		try {
			if (map.containsKey("item"))
				item = ItemUtils.parseItemStack((String) map.get("item"), place, isBlockCheck);
			else if (map.containsKey("type"))
				item = ItemUtils.parseItemStack((String) map.get("type"), place, isBlockCheck);
			else if (map.containsKey("material"))
				item = ItemUtils.parseItemStack((String) map.get("material"), place, isBlockCheck);
			
			if (map.containsKey("enchants")) {
				for (String s : (ArrayList<String>) map.get("enchants")) {
					if (s != null) {
						String[] splitted = s.split(":");
						Optional<XEnchantment> xeo = XEnchantment.of(splitted[0]);
						if (xeo.isPresent()) {
							item.addUnsafeEnchantment(xeo.get().get(), Integer.valueOf(splitted[1]));
						} else
							Logger.error(place + ": Cannot load enchantment! " + splitted[0] + " doesnt exist!");
					}
				}
			}

			ItemMeta meta = null;
			if (item.getItemMeta() != null) {
				meta = item.getItemMeta();
			} else {
				meta = Main.getInstance().getServer().getItemFactory().getItemMeta(item.getType());
			}

			if (map.containsKey("name"))
				meta.setDisplayName(Main.getMultiVersion().getChatUtils().colorize((String) map.get("name")));

			if (map.containsKey("lore")) {
				ArrayList<String> lore = new ArrayList<>();
				for (String s : (ArrayList<String>) map.get("lore")) {
					if (s != null)
						lore.add(Main.getMultiVersion().getChatUtils().colorize(s));
				}
				meta.setLore(lore);
			}

			if (map.containsKey("item-flags")) {
				for (String s : (ArrayList<String>) map.get("item-flags")) {
					if (s != null) {
						try {
							meta.addItemFlags(ItemFlag.valueOf(s));
						} catch (Exception e) {
							Logger.error("FilesUtils: There isnt any ItemFlag like: " + s);
						}
					}
				}
			}

			if (map.containsKey("custom-model-data"))
				meta.setCustomModelData((int) map.get("custom-model-data"));

			item.setItemMeta(meta);

			if (map.containsKey("custom-nbt")) {
				for (String s : (ArrayList<String>) map.get("custom-nbt")) {
					if (s != null) {
						String[] splitted = s.split(":");
						NBTAPIHook.addNBT(item, place, splitted[0], splitted[1]);
					}
				}
			}

			if (map.containsKey("amount"))
				item.setAmount((int) map.get("amount"));

		} catch (Exception e) {
			Logger.error("FilesUtils: Cannot parse ItemStack for: " + place);
			Logger.error(e);
		}
		return item;
	}
}
