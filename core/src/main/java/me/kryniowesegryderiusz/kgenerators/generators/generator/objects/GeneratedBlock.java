package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import java.util.Map;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.SkullCreator;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class GeneratedBlock extends AbstractGeneratedObject {

	public GeneratedBlock() {
		super("block");
	}

	@Getter private XMaterial xMaterial;
	
	@Getter private String skullBase64;
	
	@Override
	protected boolean compareSameType(AbstractGeneratedObject generatedObject) {
		GeneratedBlock gb = (GeneratedBlock) generatedObject;
		return this.xMaterial == gb.getXMaterial() && this.skullBase64.equals(gb.getSkullBase64());
	}

	@Override
	protected boolean loadTypeSpecific(Map<?, ?> generatedObjectConfig) {
		if (generatedObjectConfig.containsKey("material")) {
			if (((String) generatedObjectConfig.get("material")).contains("customhead:")) {
				this.xMaterial = XMaterial.PLAYER_HEAD;
				this.skullBase64 = ((String) generatedObjectConfig.get("material")).split(":")[1];
				return true;
			} else {
				Optional<XMaterial> ox = XMaterial.matchXMaterial((String) generatedObjectConfig.get("material"));
				if (ox.isPresent()) {
					this.xMaterial = ox.get();
					return true;
				} else
					Logger.error("Generators file: Cant load GeneratedBlock! Material " + (String) generatedObjectConfig.get("material") + " doesnt exist!");
			}
		}
		return false;
	}

	@Override
	public void regenerate(IGeneratorLocation generatorLocation) {
		if (isPlayerHead()) {
			SkullCreator.blockWithBase64(generatorLocation.getGeneratedBlockLocation().getBlock(), skullBase64);
		} else
			Main.getMultiVersion().getBlocksUtils().setBlock(generatorLocation.getGeneratedBlockLocation(), this.xMaterial.parseItem());
		
		if (Main.getDependencies().isEnabled(Dependency.SUPERIOR_SKYBLOCK_2))
			  SuperiorSkyblock2Hook.handleBlockPlace(generatorLocation.getGeneratedBlockLocation().getBlock());
	}

	@Override
	public ItemStack getGuiItem() {
		if (isPlayerHead())
			return SkullCreator.itemFromBase64(skullBase64);
		else
			return this.xMaterial.parseItem();
	}

	@Override
	protected String toStringSpecific() {
		if (isPlayerHead())
			return "CustomHead: " + this.skullBase64;
		else
			return "Material: " + this.xMaterial.name();
	}
	
	private boolean isPlayerHead() {
		return xMaterial == XMaterial.PLAYER_HEAD && skullBase64 != null;
	}
}
