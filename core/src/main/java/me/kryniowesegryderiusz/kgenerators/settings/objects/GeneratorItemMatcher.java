package me.kryniowesegryderiusz.kgenerators.settings.objects;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.NBTAPIHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class GeneratorItemMatcher {
	
	public static String GENERATOR_ID_NBT = "kgenerators-generator-id";
	
	@Getter boolean nbtCheck = true;
	@Getter boolean itemMetaCheck = true;
	
	public Generator getGenerator(ItemStack item) {
		
		String id = NBTAPIHook.getNBTString(item, "GeneratorItemMatcher", GENERATOR_ID_NBT);
		if (id != null)
			return Main.getGenerators().get(id);
		else if (itemMetaCheck) {
			for (Generator generator : Main.getGenerators().getAll()) {
				if (ItemUtils.compareSafe(item, generator.getGeneratorItem()))
					return generator;
			}
		}
		
		return null;
		
	}

	public void load(Config config) {
		
		if (config.contains("generator-item-matcher")) {
			if (config.contains("generator-item-matcher.check-by-nbt"))
				this.nbtCheck = config.getBoolean("generator-item-matcher.check-by-nbt");
			if (config.contains("generator-item-matcher.check-by-item-meta"))
				this.itemMetaCheck = config.getBoolean("generator-item-matcher.check-by-item-meta");
		}
	}

}
