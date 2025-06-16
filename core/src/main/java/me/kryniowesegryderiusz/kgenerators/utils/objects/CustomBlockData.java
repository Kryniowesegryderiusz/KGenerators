package me.kryniowesegryderiusz.kgenerators.utils.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import com.cryptomorin.xseries.XMaterial;

public class CustomBlockData {

	@Getter XMaterial xMaterial;
	
	public CustomBlockData(XMaterial xMaterial){
		this.xMaterial = xMaterial;
	}
	
	public void setBlock(Location location) {
		Main.getMultiVersion().getBlocksUtils().setBlock(location, xMaterial.get());
	}
	
	public void setBlock(Location location, boolean physics) {
		Main.getMultiVersion().getBlocksUtils().setBlock(location, xMaterial.get(), physics);
	}
	
	public ItemStack getItem() {
		return ItemUtils.getItemStackFromMaterial(xMaterial);
	}
	
	public String toString() {
		return "Material: " + this.xMaterial.name();
	}
	
	/**
	 * Checks xMaterial
	 * @param item
	 * @return true if xMaterials are same
	 */
	public boolean isSimilar(ItemStack is) {
		return XMaterial.matchXMaterial(is) == xMaterial;
	}
	
	public static CustomBlockData load(String configString, String place) {
		if (configString.contains(":")) {
			String[] splitted = configString.split(":");
			if (splitted[0].contains("customhead")) {
				return new HeadCustomBlockData(XMaterial.PLAYER_HEAD, splitted[1]);
			}
		}
		
		return new CustomBlockData(ItemUtils.getXMaterial(configString, place+" CustomBlockData:", true));
		
	}
}
