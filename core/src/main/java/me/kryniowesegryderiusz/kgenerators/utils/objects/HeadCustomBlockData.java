package me.kryniowesegryderiusz.kgenerators.utils.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.utils.immutable.PlayerHeadUtils;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class HeadCustomBlockData extends CustomBlockData  {
	
	private String skullBase64;
	
	HeadCustomBlockData(XMaterial xMaterial, String skullBase64) {
		super(xMaterial);
		this.skullBase64 = skullBase64;
	}
	
	@Override
	public void setBlock(Location location) {
		PlayerHeadUtils.blockWithBase64(location.getBlock(), skullBase64);
	}
	
	@Override
	public ItemStack getItem() {
		return PlayerHeadUtils.itemFromBase64(skullBase64);
	}
	
	@Override
	public String toString() {
		return "Player Head: " + this.skullBase64;
	}
}	
