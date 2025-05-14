package me.kryniowesegryderiusz.kgenerators.multiversion.interfaces;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface SkullUtils {

	public ItemStack itemFromName(String name);

	public ItemStack itemFromBase64(String string);

	public void blockWithBase64(Block block, String skullBase64);

}
