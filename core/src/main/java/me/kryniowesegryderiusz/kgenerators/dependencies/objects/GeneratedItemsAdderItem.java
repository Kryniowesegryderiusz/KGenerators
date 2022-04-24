package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.iridium.iridiumcore.dependencies.xseries.XMaterial;

import dev.lone.itemsadder.api.CustomStack;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratedItemsAdderItem extends AbstractGeneratedObject {

	@Getter private String material;
	
	public GeneratedItemsAdderItem() {
		super("items_adder_item");
	}

	@Override
	protected boolean compareSameType(AbstractGeneratedObject generatedObject) {
		GeneratedItemsAdderItem giab = (GeneratedItemsAdderItem) generatedObject;
		return giab.getMaterial().equals(this.getMaterial());
	}

	@Override
	protected boolean loadTypeSpecific(Map<?, ?> generatedObjectConfig) {
		if (generatedObjectConfig.containsKey("material")) {
			this.material = (String) generatedObjectConfig.get("material");
		} else {
			Logger.error("Generators file: Cant load GeneratedItemsAdderItem. Material is not set!");
			return false;
		}
		return true;
	}

	@Override
	public void regenerate(GeneratorLocation generatorLocation) {
		CustomStack customStack = this.getCustomStack();
		if (customStack != null) {
			customStack.drop(generatorLocation.getGeneratedBlockLocation());
		}
		
		generatorLocation.scheduleGeneratorRegeneration();
	}

	@Override
	public ItemStack getGuiItem() {
		CustomStack customStack = this.getCustomStack();
		if (customStack == null)
			return XMaterial.STONE.parseItem();
		return this.getCustomStack().getItemStack();
	}

	@Override
	protected String toStringSpecific() {
		return "Material: " + material;
	}
	
	private CustomStack getCustomStack() {
		CustomStack customStack = CustomStack.getInstance(material);
		if (customStack == null) {
			Logger.error("Generators file: Cant use GeneratedItemsAdderItem! Material " + this.material + " doesnt exist!");
		}
		return customStack;
	}

}
