package me.kryniowesegryderiusz.kgenerators.utils.objects;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import com.cryptomorin.xseries.XMaterial;

public class HeadCustomBlockData extends CustomBlockData  {
	
	private String skullBase64;
	
	public HeadCustomBlockData(XMaterial xMaterial, String skullBase64) {
		super(xMaterial);
		this.skullBase64 = skullBase64;
	}
	
	@Override
	public void setBlock(Location location) {
		Main.getMultiVersion().getSkullUtils().blockWithBase64(location.getBlock(), skullBase64);
	}
	
	@Override
	public void setBlock(Location location, boolean phyciss) {
		setBlock(location);
	}
	
	@Override
	public ItemStack getItem() {
		return Main.getMultiVersion().getSkullUtils().itemFromBase64(skullBase64);
	}
	
	@Override
	public String toString() {
		return "Player Head: " + this.skullBase64;
	}
}	
