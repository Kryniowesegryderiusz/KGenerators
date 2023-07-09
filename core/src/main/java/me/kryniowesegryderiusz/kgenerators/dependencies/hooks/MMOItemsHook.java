package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import net.Indyuce.mmoitems.MMOItems;

public class MMOItemsHook {

	public static ItemStack getItemStack(String string) {
		if (!Main.getDependencies().isEnabled(Dependency.MMOITEMS)) return null;
		return MMOItems.plugin.getItem(string.split("#")[0], string.split("#")[1]);
	}

}
