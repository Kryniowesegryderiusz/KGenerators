package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;

public class BlocksUtils_1_13 implements BlocksUtils {
	
    public static final Set<Material> CROPS = Collections.unmodifiableSet(EnumSet.of(
            Material.CARROT, Material.CARROTS, Material.POTATO, Material.POTATOES,
            Material.NETHER_WART, Material.PUMPKIN_SEEDS, Material.WHEAT_SEEDS, Material.WHEAT,
            Material.MELON_SEEDS, Material.BEETROOT_SEEDS, Material.BEETROOTS, Material.SUGAR_CANE,
            Material.CHORUS_PLANT, //Material.BAMBOO_SAPLING, Material.BAMBOO, 
            Material.KELP, Material.KELP_PLANT, Material.SEA_PICKLE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM,
            Material.MELON_STEM, Material.PUMPKIN_STEM, Material.COCOA, Material.COCOA_BEANS

    ));

	@Override
	public ItemStack getItemStackByBlock(Block block) {
		Material material = block.getType();
		if (CROPS.contains(material)) {
			switch (material) {
				case CARROTS:
					return new ItemStack(Material.CARROT);
				case POTATOES:
					return new ItemStack(Material.POTATO);
				case BEETROOTS:
					return new ItemStack(Material.BEETROOT);
				case PUMPKIN_STEM:
					return new ItemStack(Material.PUMPKIN_SEEDS);
				case MELON_STEM:
					return new ItemStack(Material.MELON_SEEDS);
				default:
					break;
			}
		}
		return new ItemStack(material);
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
