package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.inventory.ItemStack;

import com.nexomc.nexo.api.NexoItems;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;

public class NexoHook {
	
	public static ItemStack getItemStack(String material) {
		if (!exists(material)) return null;
		return NexoItems.itemFromId(material).build();
	}

	public static boolean exists(String material) {
		return Main.getDependencies().isEnabled(Dependency.NEXO) && NexoItems.exists(material);
	}

}
