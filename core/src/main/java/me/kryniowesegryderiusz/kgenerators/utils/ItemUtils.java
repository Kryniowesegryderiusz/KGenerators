package me.kryniowesegryderiusz.kgenerators.utils;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.ItemsAdderHook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public abstract class ItemUtils {
	
    public static ItemStack parseItemStack(String s, String place, boolean isBlockCheck) {
    	
    	if (s.contains(":")) {
    		String[] splitted = s.split(":");
        	if (splitted[0].equals("itemsadder")
        			&& ItemsAdderHook.getItemStack(splitted[1]) != null)
        		return ItemsAdderHook.getItemStack(splitted[1]);
    	}
    	
    	Optional<XMaterial> oxm = XMaterial.matchXMaterial(s);
		try {
			XMaterial xm = oxm.get();
			if (isBlockCheck && !xm.parseMaterial().isBlock())
			{
				Logger.error(place+": " + s + " is not a block! Using STONE!");
				return XMaterial.STONE.parseItem();
			}
			return xm.parseItem();
		} catch (NoSuchElementException e) {
			Logger.error(place+": " + s + " is not a proper material or generator id! Using STONE!");
			//e.printStackTrace();
		}
		return XMaterial.STONE.parseItem();
    }
}
