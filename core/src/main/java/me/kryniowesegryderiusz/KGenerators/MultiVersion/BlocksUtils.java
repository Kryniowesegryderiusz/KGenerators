package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface BlocksUtils {
	
	public ItemStack getItemStackByBlock(Block block);
	
	public void setBlock(Location location, ItemStack item);

}