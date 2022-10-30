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
			Logger.error(e);
		}
		
		return item;
	}
	
	public static ItemStack addNBT(ItemStack item, String place, String key, int value) {
		
		try {
			NBTItem nbti = new NBTItem(item);
			nbti.setInteger(key, value);
			nbti.applyNBT(item);
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot apply NBT " + key + ":" + value + " to " + item.toString());
			Logger.error(e);
		}
		
		return item;
	}
	
	public static String getNBTString(ItemStack item, String place, String key) {
		
		try {
			NBTItem nbti = new NBTItem(item);
			
			if (nbti.getKeys().contains(key))
				return nbti.getString(key);
			else return null;
			
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot get NBT " + key + " from " + item.toString());
			Logger.error(e);
		}
		
		return null;
	}
	
	public static Integer getNBTInteger(ItemStack item, String place, String key) {
		
		try {
			NBTItem nbti = new NBTItem(item);
			
			if (nbti.getKeys().contains(key))
				return nbti.getInteger(key);
			else return null;
			
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot get NBT " + key + " from " + item.toString());
			Logger.error(e);
		}
		
		return null;
	}

}
