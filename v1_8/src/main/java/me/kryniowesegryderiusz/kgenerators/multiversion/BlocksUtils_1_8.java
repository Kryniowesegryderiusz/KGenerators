package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;

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
	public void setBlock(Location location, Material material, boolean physics) {
		location.getBlock().setType(material, false);
	}

	@Override
	public void setBlock(Location location, Material material) {
		location.getBlock().setType(material);

	}

	@Override
	public boolean isOnWhitelist(Block block) {
		if (block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER)) {
			if (Main.getSettings().isOnWhitelist(Material.WATER)) {
				return true;
			}
		} else if (block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA)) {
			if (Main.getSettings().isOnWhitelist(Material.LAVA)) {
				return true;
			}
		} else {
			if (Main.getSettings().isOnWhitelist(block.getType())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAir(Block block) {
		if (block.getType() == Material.AIR)
			return true;
		return false;
	}

	@Override
	public boolean isWater(Block block) {
		return block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER);
	}

	@Override
	public boolean isAgeable(Block block) {
		return block.getType() == Material.WHEAT
				|| block.getType() == Material.CARROT || block.getType() == Material.POTATO || block.getState().getData() instanceof Crops;
	}

	@Override
	public int getMaximumAge(Block block) {
		return Integer.MAX_VALUE;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getAge(Block block) {
		return block.getState().getData().getData();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setAge(Block block, int age) {
        BlockState state = block.getState();
        MaterialData data = state.getData();
        data.setData((byte) age);
        state.update(true);

	}

}
