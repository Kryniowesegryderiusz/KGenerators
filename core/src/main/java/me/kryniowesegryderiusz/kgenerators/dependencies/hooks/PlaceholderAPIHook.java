package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import me.clip.placeholderapi.PlaceholderAPI;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;

public class PlaceholderAPIHook {

	public static String translatePlaceholders(OfflinePlayer op, String text) {
	
		if (op != null && Main.getDependencies().isEnabled(Dependency.PLACEHOLDERAPI)) {
			return PlaceholderAPI.setPlaceholders(op, text);
		} else
			return text;
	}

	public static ItemStack translatePlaceholders(OfflinePlayer op, ItemStack item) {
		
		if (op != null && Main.getDependencies().isEnabled(Dependency.PLACEHOLDERAPI) && item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName())
				item.getItemMeta()
						.setDisplayName(PlaceholderAPI.setPlaceholders(op, item.getItemMeta().getDisplayName()));
			if (item.getItemMeta().hasLore())
				item.getItemMeta().setLore(PlaceholderAPI.setPlaceholders(op, item.getItemMeta().getLore()));
		}
		return item;
	}
}
