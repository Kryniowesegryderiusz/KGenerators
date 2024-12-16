package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratedNexoBlock extends AbstractGeneratedObject {
	
	@Getter private String material;

	public GeneratedNexoBlock() {
		super("nexo_block");
	}

	@Override
	public void regenerate(IGeneratorLocation generatorLocation) {
		NexoBlocks.place(material, generatorLocation.getGeneratedBlockLocation());
		
	}

	@Override
	public ItemStack getGuiItem() {
		return NexoItems.itemFromId(this.material).build();
	}

	@Override
	protected String toStringSpecific() {
		return "Material: " + this.material;
	}

	@Override
	protected boolean compareSameType(AbstractGeneratedObject generatedObject) {
		return this.material.equals(((GeneratedNexoBlock) generatedObject).getMaterial());
	}

	@Override
	protected boolean loadTypeSpecific(Map<?, ?> generatedObjectConfig) {
		if (generatedObjectConfig.containsKey("material")) {
			this.material = (String) generatedObjectConfig.get("material");
		} else {
			Logger.error("Generators file: Cant load GeneratedNexoBlock. Material is not set!");
			return false;
		}
		return true;
	}

}
