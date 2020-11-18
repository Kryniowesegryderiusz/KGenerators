package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.kryniowesegryderiusz.KGenerators.KGenerators;
import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

public class BlocksUtils_1_8 implements BlocksUtils {

	@Override
	public ItemStack getItemStackByBlock(Block block) {
		
		Material material = XMaterial.matchXMaterial(block.getType()).parseMaterial();
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
			if (KGenerators.generatingWhitelist.contains(XMaterial.WATER.parseItem())) {
				return true;
			}
		} else if (block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA)) {
			if (KGenerators.generatingWhitelist.contains(XMaterial.LAVA.parseItem())) {
				return true;
			}
		} else
		{
			if (KGenerators.generatingWhitelist.contains(getItemStackByBlock(block))) {
				return true;
			}
		}
		return false;
	}
	
	
}
