package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.block.BlockMechanicFactory;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.OraxenHook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratedOraxenBlock extends AbstractGeneratedObject {
	
	@Getter private String material;

	public GeneratedOraxenBlock() {
		super("oraxen_block");
	}

	@Override
	protected boolean compareSameType(AbstractGeneratedObject generatedObject) {
		GeneratedOraxenBlock giab = (GeneratedOraxenBlock) generatedObject;
		return giab.getMaterial().equals(this.material);
	}

	@Override
	protected boolean loadTypeSpecific(Map<?, ?> generatedObjectConfig) {
		if (generatedObjectConfig.containsKey("material")) {
			this.material = (String) generatedObjectConfig.get("material");
		} else {
			Logger.error("Generators file: Cant load GeneratedOraxenBlock. Material is not set!");
			return false;
		}
		return true;
	}

	@Override
	public void regenerate(IGeneratorLocation generatorLocation) {
		BlockMechanicFactory.setBlockModel(generatorLocation.getGeneratedBlockLocation().getBlock(), this.material);
	}

	@Override
	public ItemStack getGuiItem() {
		return OraxenHook.getItemStack(this.material);
	}

	@Override
	protected String toStringSpecific() {
		return "Material: " + this.material;
	}
}
