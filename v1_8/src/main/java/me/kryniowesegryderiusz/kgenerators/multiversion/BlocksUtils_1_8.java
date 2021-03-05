package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.multiversion.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class BlocksUtils_1_8 implements BlocksUtils {

	@Override
	public ItemStack getItemStackByBlock(Block block) {
		MaterialData data = block.getState().getData();
		ItemStack item = data.toItemStack();
		item.setAmount(1);
		return item;
	}

	@Override
	public void setBlock(Location location, ItemStack item) {
		
		Block block = location.getBlock();
		
		block.setType(item.getType());

		BlockState state = block.getState();
		state.setData(item.getData());
		state.update(true);
		
	}

	@Override
	public boolean isOnWhitelist(Block block) {
		if (block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER)) {
			if (Main.getSettings().getGeneratingWhitelist().contains(XMaterial.WATER.parseItem())) {
				return true;
			}
		} else if (block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA)) {
			if (Main.getSettings().getGeneratingWhitelist().contains(XMaterial.LAVA.parseItem())) {
				return true;
			}
		} else
		{
			if (Main.getSettings().getGeneratingWhitelist().contains(getItemStackByBlock(block))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAir(Block block) {
		if (block.getType() == Material.AIR) return true;
		return false;
	}
	
	
}
