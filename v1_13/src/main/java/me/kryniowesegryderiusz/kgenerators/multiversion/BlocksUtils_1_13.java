package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;

public class BlocksUtils_1_13 implements BlocksUtils {

	@Override
	public ItemStack getItemStackByBlock(Block block) {
		return ItemUtils.getItemStackFromMaterial(block.getType());
	}

	@Override
	public void setBlock(Location location, ItemStack item) {
		location.getBlock().setType(item.getType());
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
		if (Main.getSettings().isOnWhitelist(block.getType())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isAir(Block block) {
		if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR)
			return true;
		return false;
	}

	@Override
	public boolean isWater(Block block) {
		return block.getType() == Material.WATER;
	}

	@Override
	public boolean isAgeable(Block block) {
		return block.getBlockData() instanceof Ageable;
	}

	@Override
	public int getMaximumAge(Block block) {
		return ((Ageable) block.getBlockData()).getMaximumAge();
	}

	@Override
	public int getAge(Block block) {
		return ((Ageable) block.getBlockData()).getAge();
	}

	@Override
	public void setAge(Block block, int age) {
		Ageable ageable = (Ageable) block.getBlockData();
		ageable.setAge(age);
		block.setBlockData(ageable);
	}
}
