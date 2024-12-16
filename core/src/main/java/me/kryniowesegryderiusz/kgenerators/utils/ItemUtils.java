package me.kryniowesegryderiusz.kgenerators.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.EcoItemsHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.ItemsAdderHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.MMOItemsHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.MythicMobsHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.NexoHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.OraxenHook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.PlayerHeadUtils;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public abstract class ItemUtils {
	
    public static ItemStack parseItemStack(String material, String place, boolean isBlockCheck) { 	
    	if (material.contains(":")) {
    		String[] splitted = material.split(":");
        	if (splitted[0].equals("generator")
        			&& Main.getGenerators() != null
        			&& Main.getGenerators().exists(splitted[1]))
        		return Main.getGenerators().get(splitted[1]).getGeneratorItem().clone();
        	else if (splitted[0].equals("customhead")
        			&& PlayerHeadUtils.itemFromBase64(splitted[1]) != null)
        		return PlayerHeadUtils.itemFromBase64(splitted[1]);
        	else if (splitted[0].equals("itemsadder")
        			&& ItemsAdderHook.getItemStack(splitted[1]) != null)
        		return ItemsAdderHook.getItemStack(splitted[1]);
        	else if (splitted[0].equals("ecoitems")
        			&& EcoItemsHook.getItemStack(splitted[1]) != null)
        		return EcoItemsHook.getItemStack(splitted[1]);
        	else if (splitted[0].equals("oraxen")
        			&& OraxenHook.getItemStack(splitted[1]) != null)
        		return OraxenHook.getItemStack(splitted[1]);
        	else if (splitted[0].equals("nexo")
        			&& NexoHook.exists(splitted[1]))
        		return NexoHook.getItemStack(splitted[1]);
        	else if (splitted[0].equals("mmoitems")
        			&& MMOItemsHook.getItemStack(splitted[1]) != null)
        		return MMOItemsHook.getItemStack(splitted[1]);
        	else if (splitted[0].equals("mythicmobs")
        			&& MythicMobsHook.getItemStack(splitted[1]) != null)
        		return MythicMobsHook.getItemStack(splitted[1]);
        	
    	}
		return getXMaterial(material, place, isBlockCheck).parseItem();
    }
    
	public static XMaterial getXMaterial(String material, String place, boolean isBlockCheck) {
		Optional<XMaterial> oxm = XMaterial.matchXMaterial(material);
		try {
			XMaterial xm = oxm.get();
			if (isBlockCheck && !xm.get().isBlock()) {
				Logger.error(place + ": " + material + " is not a block! Using STONE!");
				return XMaterial.STONE;
			}
			return xm;
		} catch (NoSuchElementException e) {
			Logger.error(place + ": " + material + " is not a proper material or generator id! Using STONE!");
		}
		return XMaterial.STONE;
	}
	
	/**
	 * Compares ItemStacks ignoring "internal" NBT and itemstack amount
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static boolean compareSafe(ItemStack is1, ItemStack is2) {
		
		ItemStack is1c = is1.clone(), is2c = is2.clone();
		is1c.setAmount(1);
		is2c.setAmount(1);
		
        String is1String = splitInternal(is1c.toString()), is2String = splitInternal(is2c.toString());

        return is1String.equals(is2String);
	}
	
	private static String splitInternal(String s) {
        if (s.contains(", internal=")) {
            String[] split = s.split(", internal=");
            if (split[1].split(", ", 2).length > 1)
            	s = split[0] + ", " + split[1].split(", ", 2)[1];
            else
            	s = split[0] + "}}";
        }
		return s;
	}
	
	public static ItemStack replaceLore(ItemStack item, String... replacables) {

		if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return item;
		
		ArrayList<String> reps = new ArrayList<>(Arrays.asList(replacables));
		ArrayList<String> newLore = new ArrayList<String>();
		
		for (String s : item.getItemMeta().getLore()) {
			String message = s;
			for (int i = 0; i < reps.size(); i = i + 2) {
				message = message.replace(reps.get(i), reps.get(i + 1));
			}
			newLore.add(message);
		}
		
		ItemMeta meta = item.getItemMeta();
		meta.setLore(newLore);
		item.setItemMeta(meta);
		
		return item;
	}
}
