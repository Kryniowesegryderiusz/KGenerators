package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.objects.CustomEntityData;

public class GeneratedEntity extends AbstractGeneratedObject {
	
	@Getter private CustomEntityData customEntityData;
	
	private int maxEntities = 1;
	private ArrayList<Entity> entities = new ArrayList<Entity>();

	public GeneratedEntity() {
		super("entity");
	}

	@Override
	public void regenerate(IGeneratorLocation generatorLocation) {
		Location generateLocation = generatorLocation.getGeneratedBlockLocation().clone().add(0.5, 0, 0.5);
		if (!Main.getMultiVersion().getBlocksUtils().isAir(generatorLocation.getGeneratedBlockLocation().getBlock()))
			generateLocation.add(0,1,0);
		generateLocation.setPitch(-90);
		entities.addAll(customEntityData.spawnEntities(generateLocation));
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
		
		if (generatedObjectConfig.containsKey("max-entities"))
			this.maxEntities = (int) generatedObjectConfig.get("max-entities");
		
		if (generatedObjectConfig.containsKey("entity")) {
			this.customEntityData = CustomEntityData.load(generatedObjectConfig, "entity");
			if (this.customEntityData != null)
				return true;
		}
		return false;
	}
	
	@Override
	public boolean isReady() {
		
		ListIterator<Entity> iter = this.entities.listIterator();
		while(iter.hasNext()) {
		    if(iter.next().isDead()) {
		        iter.remove();
		    }
		}
		
		return this.maxEntities < 0 || this.entities.size() < this.maxEntities;
	}
}
