package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.inventory.ItemStack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class NBTAPIHook {
	
	public static ItemStack addNBT(ItemStack item, String place, String key, String value) {
		
		try {
			NBTItem nbti = new NBTItem(item);
			nbti.setString(key, value);
			nbti.applyNBT(item);
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot apply NBT " + key + ":" + value + " to " + item.toString());
			e.printStackTrace();
		}
		
		return item;
	}

}
