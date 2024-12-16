package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;

import dev.lone.itemsadder.api.CustomBlock;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratedItemsAdderBlock extends AbstractGeneratedObject {
	
	@Getter private String material;

	public GeneratedItemsAdderBlock() {
		super("itemsadder_block");
	}

	@Override
	protected boolean compareSameType(AbstractGeneratedObject generatedObject) {
		GeneratedItemsAdderBlock giab = (GeneratedItemsAdderBlock) generatedObject;
		return giab.getMaterial().equals(this.material);
	}

	@Override
	protected boolean loadTypeSpecific(Map<?, ?> generatedObjectConfig) {
		if (generatedObjectConfig.containsKey("material")) {
			this.material = (String) generatedObjectConfig.get("material");
		} else {
			Logger.error("Generators file: Cant load GeneratedItemsAdderBlock. Material is not set!");
			return false;
		}
		return true;
	}

	@Override
	public void regenerate(IGeneratorLocation generatorLocation) {
		CustomBlock customBlock = this.getCustomBlock();
		if (customBlock != null)
			customBlock.place(generatorLocation.getGeneratedBlockLocation());
		else
			generatorLocation.scheduleGeneratorRegeneration();
	}

	@Override
	public ItemStack getGuiItem() {
		CustomBlock customBlock = this.getCustomBlock();
		if (customBlock == null)
			return XMaterial.STONE.parseItem();
		return this.getCustomBlock().getItemStack();
	}

	@Override
	protected String toStringSpecific() {
		return "Material: " + this.material;
	}
	
	private CustomBlock getCustomBlock() {
		CustomBlock customBlock = CustomBlock.getInstance(material);
		if (customBlock == null) {
			Logger.error("Generators file: Cant load block GeneratedItemsAdderBlock! Material " + material + " doesnt exist!");
		}
		return customBlock;
	}
}
