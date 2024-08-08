package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import java.util.Map;

import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.mechanics.provided.gameplay.block.BlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.stringblock.StringBlockMechanicFactory;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.OraxenHook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratedOraxenBlock extends AbstractGeneratedObject {
	
	@Getter @Setter
	private String material;

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
		
		String oraxenmaterial = this.material;
		if (oraxenmaterial.contains("oraxen:"))
			oraxenmaterial = oraxenmaterial.split(":")[1];
		
		if (OraxenBlocks.isOraxenNoteBlock(oraxenmaterial))
			NoteBlockMechanicFactory.setBlockModel(generatorLocation.getGeneratedBlockLocation().getBlock(), oraxenmaterial);
		else if (OraxenBlocks.isOraxenStringBlock(oraxenmaterial))
			StringBlockMechanicFactory.setBlockModel(generatorLocation.getGeneratedBlockLocation().getBlock(), oraxenmaterial);
		else if (OraxenBlocks.isOraxenBlock(oraxenmaterial))
			BlockMechanicFactory.setBlockModel(generatorLocation.getGeneratedBlockLocation().getBlock(), oraxenmaterial);
		else
			Logger.error("GeneratedOraxenBlock: Unsupported Oraxen mechanic factory for " + oraxenmaterial + "! Currently supported are only block, noteblock and stringblock!");

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
