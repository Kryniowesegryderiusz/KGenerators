package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBT;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class NBTAPIHook {

	public static ItemStack addNBT(ItemStack item, String place, String key, String value) {

		try {
			NBT.modify(item, nbt -> {
				nbt.setString(key, value);
			});
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot apply NBT " + key + ":" + value + " to "
					+ item.toString());
			Logger.error(e);
		}

		return item;
	}

	public static ItemStack addNBT(ItemStack item, String place, String key, int value) {

		try {
			NBT.modify(item, nbt -> {
				nbt.setInteger(key, value);
			});
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot apply NBT " + key + ":" + value + " to "
					+ item.toString());
			Logger.error(e);
		}

		return item;
	}

	public static ItemStack addNBT(ItemStack item, String place, String key, double value) {

		try {
			NBT.modify(item, nbt -> {
				nbt.setDouble(key, value);
			});
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot apply NBT " + key + ":" + value + " to "
					+ item.toString());
			Logger.error(e);
		}

		return item;
	}

	public static String getNBTString(ItemStack item, String place, String key) {

		try {
			return NBT.get(item, nbt -> {
				return nbt.getString(key);
			});
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot get NBT String " + key + " from "
					+ item.toString());
			Logger.error(e);
		}

		return null;
	}

	public static Integer getNBTInteger(ItemStack item, String place, String key) {

		try {
			return NBT.get(item, nbt -> {
				return nbt.getInteger(key);
			});

		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot get NBT String " + key + " from "
					+ item.toString());
			Logger.error(e);
		}

		return null;
	}

	public static Double getNBTDouble(ItemStack item, String place, String key) {

		try {
			return NBT.get(item, nbt -> {
				return nbt.getDouble(key);
			});
		} catch (Exception e) {
			Logger.error("Dependencies: NBTAPIHook: " + place + ": Cannot get NBT String " + key + " from "
					+ item.toString());
			Logger.error(e);
		}

		return null;
	}

}
