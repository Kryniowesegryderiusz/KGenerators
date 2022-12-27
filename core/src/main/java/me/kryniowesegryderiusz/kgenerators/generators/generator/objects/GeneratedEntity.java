package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.objects.CustomEntityData;

public class GeneratedEntity extends AbstractGeneratedObject {
	
	@Getter private CustomEntityData customEntityData;

	public GeneratedEntity() {
		super("entity");
	}

	@Override
	public void regenerate(IGeneratorLocation generatorLocation) {
		Location generateLocation = generatorLocation.getGeneratedBlockLocation().clone().add(0.5, 0, 0.5);
		if (!Main.getMultiVersion().getBlocksUtils().isAir(generatorLocation.getGeneratedBlockLocation().getBlock()))
			generateLocation.add(0,1,0);
		generateLocation.setPitch(-90);
		customEntityData.spawnMob(generateLocation);
		generatorLocation.scheduleGeneratorRegeneration();
	}

	@Override
	public ItemStack getGuiItem() {
		return ItemUtils.parseItemStack(customEntityData.getEntityType().name()+"_SPAWN_EGG", "Generators file: GeneratedEntity", false);
	}

	@Override
	protected String toStringSpecific() {
		if (customEntityData == null)
			return "None";
		return customEntityData.toString();
	}

	@Override
	protected boolean compareSameType(AbstractGeneratedObject generatedObject) {
		GeneratedEntity ge = (GeneratedEntity) generatedObject;
		return customEntityData.isSimilar(ge.getCustomEntityData());
	}

	@Override
	protected boolean loadTypeSpecific(Map<?, ?> generatedObjectConfig) {
		if (generatedObjectConfig.containsKey("entity")) {
			this.customEntityData = CustomEntityData.load(generatedObjectConfig, "entity");
			if (this.customEntityData != null)
				return true;
		}
		return false;
	}
}
