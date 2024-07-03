package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class BlocksUtils_1_8 implements BlocksUtils {

	@Override
	public ItemStack getItemStackByBlock(Block block) {
		
		if (block.getType() == Material.GLOWING_REDSTONE_ORE)
			return new ItemStack(Material.REDSTONE_ORE);
		
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
	public void setBlock(Location location, XMaterial item) {
		this.setBlock(location, item.parseItem());
	}
	
	@Override
	public void setBlock(Location location, XMaterial xmaterial, boolean physics) {
		location.getBlock().setType(xmaterial.parseMaterial(), false);
	}

	@Override
	public void setBlock(Location location, Material material) {
		location.getBlock().setType(material);
		
	}

	@Override
	public boolean isOnWhitelist(Block block) {
		if (block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER)) {
			if (Main.getSettings().getGeneratingWhitelist().contains(XMaterial.WATER)) {
				return true;
			}
		} else if (block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA)) {
			if (Main.getSettings().getGeneratingWhitelist().contains(XMaterial.LAVA)) {
				return true;
			}
		} else
		{
			if (Main.getSettings().getGeneratingWhitelist().contains(XMaterial.matchXMaterial(block.getType()))) {
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
