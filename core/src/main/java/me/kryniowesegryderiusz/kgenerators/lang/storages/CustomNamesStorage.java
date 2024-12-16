package me.kryniowesegryderiusz.kgenerators.lang.storages;

import java.util.HashMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;

public class CustomNamesStorage {

	private HashMap<String, String> customNames = new HashMap<String, String>();
	private Config config;

	public CustomNamesStorage(Config config) {
		this.config = config;
		this.reload();
	}

	public void reload() {
		for (String obj : config.getConfigurationSection("").getKeys(false)) {
			customNames.put(obj, config.getString(obj));
		}
	}

	public String getItemTypeName(ItemStack item) {
		String name = "";

		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
			name = item.getItemMeta().getDisplayName();
		else if (customNames.get(XMaterial.matchXMaterial(item).name()) != null)
			name = customNames.get(XMaterial.matchXMaterial(item).name());
		else {
			String type = item.getType().toString();
			type = type.toLowerCase().replaceAll("_", " ");
			type = type.substring(0, 1).toUpperCase() + type.substring(1);
			name = type;
		}
		return name;
	}

	@SuppressWarnings("unlikely-arg-type")
	public String getEnchantName(Enchantment enchant) {
		String name = "";

		if (customNames.get(XEnchantment.matchXEnchantment(enchant)) != null)
			name = customNames.get(XEnchantment.matchXEnchantment(enchant));
		else {
			String type = enchant.getKey().getKey();
			type = type.toLowerCase().replaceAll("_", " ");
			type = type.substring(0, 1).toUpperCase() + type.substring(1);
			name = type;
		}

		return name;
	}

}
