package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.inventory.ItemStack;

import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;

public class EcoItemsHook {
	
	public static ItemStack getItemStack(String material) {
		if (Main.getDependencies().isEnabled(Dependency.ECO_ITEMS)) {
			EcoItem ei = EcoItems.getByID(material);
			if (ei != null)
				return ei.getItemStack();
		}
		return null;
	}

}
