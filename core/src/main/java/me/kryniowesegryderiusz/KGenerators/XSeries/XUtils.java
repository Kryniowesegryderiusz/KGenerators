package me.kryniowesegryderiusz.KGenerators.XSeries;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;

public abstract class XUtils {
	
    public static ItemStack parseItemStack(String s) {
    	
    	Optional<XMaterial> oxm = XMaterial.matchXMaterial(s);
		try {
			XMaterial xm = oxm.get();
			return xm.parseItem();
		} catch (NoSuchElementException e) {
			System.out.println("[KGenerators] !!! ERROR !!! " + s + " is not a proper material! Using STONE!");
			//e.printStackTrace();
		}
		return XMaterial.STONE.parseItem();
    }

}
