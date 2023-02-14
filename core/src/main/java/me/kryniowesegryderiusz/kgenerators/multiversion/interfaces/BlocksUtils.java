package me.kryniowesegryderiusz.kgenerators.multiversion.interfaces;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public interface BlocksUtils {
	
	public ItemStack getItemStackByBlock(Block block);
	
	public void setBlock(Location location, ItemStack generatorItem);
	public void setBlock(Location location, XMaterial xmaterial, boolean physics);
	public void setBlock(Location location, XMaterial xmaterial);
	public void setBlock(Location location, Material material);
	
	public boolean isOnWhitelist(Block block);
	
	public boolean isAir(Block block);

}