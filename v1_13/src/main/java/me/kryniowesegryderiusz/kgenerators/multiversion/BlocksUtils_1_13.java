package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class BlocksUtils_1_13 implements BlocksUtils {
	
    public static final Set<XMaterial> CROPS = Collections.unmodifiableSet(EnumSet.of(
            XMaterial.CARROT, XMaterial.CARROTS, XMaterial.POTATO, XMaterial.POTATOES,
            XMaterial.NETHER_WART, XMaterial.PUMPKIN_SEEDS, XMaterial.WHEAT_SEEDS, XMaterial.WHEAT,
            XMaterial.MELON_SEEDS, XMaterial.BEETROOT_SEEDS, XMaterial.BEETROOTS, XMaterial.SUGAR_CANE,
            XMaterial.BAMBOO_SAPLING, XMaterial.BAMBOO, XMaterial.CHORUS_PLANT,
            XMaterial.KELP, XMaterial.KELP_PLANT, XMaterial.SEA_PICKLE, XMaterial.BROWN_MUSHROOM, XMaterial.RED_MUSHROOM,
            XMaterial.MELON_STEM, XMaterial.PUMPKIN_STEM, XMaterial.COCOA, XMaterial.COCOA_BEANS

    ));

	@Override
	public ItemStack getItemStackByBlock(Block block) {
		XMaterial xm = XMaterial.matchXMaterial(block.getType());
		if (CROPS.contains(xm)) {
			switch (xm) {
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
		return xm.parseItem();
	}

	@Override
	public void setBlock(Location location, ItemStack item) {
		location.getBlock().setType(item.getType());
	}

	@Override
	public void setBlock(Location location, XMaterial xmaterial) {
		location.getBlock().setType(xmaterial.parseMaterial());

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

		if (Main.getSettings().getGeneratingWhitelist().contains(XMaterial.matchXMaterial(block.getType()))) {
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
}
