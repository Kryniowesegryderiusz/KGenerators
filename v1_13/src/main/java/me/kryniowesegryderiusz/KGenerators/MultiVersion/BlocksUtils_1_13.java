package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

public class BlocksUtils_1_13 implements BlocksUtils {

	@Override
	public ItemStack getItemStackByBlock(Block block) {
		return XMaterial.matchXMaterial(block.getType()).parseItem();
	}

	@Override
	public void setBlock(Location location, ItemStack item) {
		location.getBlock().setType(item.getType());
		
	}

	@Override
	public boolean isOnWhitelist(Block block) {

		if (Main.generatingWhitelist.contains(getItemStackByBlock(block))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAir(Block block) {
		if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) return true;
		return false;
	}

}
