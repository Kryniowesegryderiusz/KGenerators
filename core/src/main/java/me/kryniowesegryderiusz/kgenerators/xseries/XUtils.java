package me.kryniowesegryderiusz.kgenerators.xseries;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public abstract class XUtils {
	
    public static ItemStack parseItemStack(String s, String place, boolean isBlockCheck) {
    	
    	if (Main.getGenerators() != null && Main.getGenerators().exists(s))
    		return Main.getGenerators().get(s).getGeneratorItem().clone();
    	
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
