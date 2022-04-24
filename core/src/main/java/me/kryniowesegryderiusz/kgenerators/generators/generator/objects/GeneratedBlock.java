package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import java.util.Map;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class GeneratedBlock extends AbstractGeneratedObject {

	public GeneratedBlock() {
		super("block");
	}

	@Getter private XMaterial xMaterial;
	
	@Override
	protected boolean compareSameType(AbstractGeneratedObject generatedObject) {
		GeneratedBlock gb = (GeneratedBlock) generatedObject;
		return this.xMaterial == gb.getXMaterial();
	}

	@Override
	protected boolean loadTypeSpecific(Map<?, ?> generatedObjectConfig) {
		if (generatedObjectConfig.containsKey("material")) {
			Optional<XMaterial> ox = XMaterial.matchXMaterial((String) generatedObjectConfig.get("material"));
			if (ox.isPresent()) {
				this.xMaterial = ox.get();
				return true;
			} else
				Logger.error("Generators file: Cant load GeneratedBlock! Material " + (String) generatedObjectConfig.get("material") + " doesnt exist!");
		}
		return false;
	}

	@Override
	public void regenerate(GeneratorLocation generatorLocation) {
		  Main.getMultiVersion().getBlocksUtils().setBlock(generatorLocation.getGeneratedBlockLocation(), this.xMaterial.parseItem());
		  if (Main.getDependencies().isEnabled(Dependency.SUPERIOR_SKYBLOCK_2))
			  SuperiorSkyblock2Hook.handleBlockPlace(generatorLocation.getGeneratedBlockLocation().getBlock());
	}

	@Override
	public ItemStack getGuiItem() {
		return this.xMaterial.parseItem();
	}

	@Override
	protected String toStringSpecific() {
		return "Material: " + this.xMaterial.name();
	}
}
