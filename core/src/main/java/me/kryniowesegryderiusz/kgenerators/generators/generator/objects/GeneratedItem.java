package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;

public class GeneratedItem extends AbstractGeneratedObject {
	
	public GeneratedItem() {
		super("item");
	}

	@Getter private ItemStack item;

	@Override
	protected boolean compareSameType(AbstractGeneratedObject generatedObject) {
		GeneratedItem gi = (GeneratedItem) generatedObject;
		return gi.getItem().equals(this.getItem());
	}

	@Override
	protected boolean loadTypeSpecific(Map<?, ?> generatedObjectConfig) {
		this.item = FilesUtils.loadItemStack((Map<?, ?>) generatedObjectConfig, "item", "Generators file: GeneratedItem", false);
		if (this.item != null)
			return true;
		return false;
	}

	@Override
	public void regenerate(IGeneratorLocation generatorLocation) {
		Location generateLocation = generatorLocation.getGeneratedBlockLocation().clone().add(0.5, 0, 0.5);
		if (!Main.getMultiVersion().getBlocksUtils().isAir(generatorLocation.getGeneratedBlockLocation().getBlock()))
			generateLocation.add(0,1,0);
		generateLocation.setPitch(-90);
		generateLocation.getWorld().dropItem(generateLocation, item);
		generatorLocation.scheduleGeneratorRegeneration();
	}

	@Override
	public ItemStack getGuiItem() {
		return this.item.clone();
	}

	@Override
	protected String toStringSpecific() {
		if (this.getItem() == null)
			return "None";
		return "Item: " + this.getItem().toString();
	}

}
