package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.xseries.XBlock;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class BlocksUtils_1_13 implements BlocksUtils {

	@Override
	public ItemStack getItemStackByBlock(Block block) {
		XMaterial xm = XMaterial.matchXMaterial(block.getType());
		if (XBlock.isCrop(xm)) {
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
}
