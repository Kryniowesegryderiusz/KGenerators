package me.kryniowesegryderiusz.kgenerators.multiversion.interfaces;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface BlocksUtils {
	
	public ItemStack getItemStackByBlock(Block block);
	
	public void setBlock(Location location, ItemStack item);
	public void setBlock(Location location, Material xmaterial, boolean physics);
	public void setBlock(Location location, Material material);
	
	public boolean isOnWhitelist(Block block);
	
	public boolean isAir(Block block);
	
	public boolean isWater(Block block);

	public boolean isAgeable(Block block);
	public int getMaximumAge(Block block);
	public int getAge(Block block);
	public void setAge(Block block, int age);

}