package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.inventory.ItemStack;

import io.th0rgal.oraxen.api.OraxenItems;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;

public class OraxenHook {

	public static ItemStack getItemStack(String material) {
		if (!Main.getDependencies().isEnabled(Dependency.ORAXEN) || !OraxenItems.exists(material)) return null;
		return OraxenItems.getItemById(material).build();
	}

}
