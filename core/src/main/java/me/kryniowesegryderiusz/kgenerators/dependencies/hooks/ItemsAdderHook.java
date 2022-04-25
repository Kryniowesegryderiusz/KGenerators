package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import dev.lone.itemsadder.api.CustomBlock;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class ItemsAdderHook {
	
	public static void handleGeneratorLocationRemove(GeneratorLocation gLoc) {
		if (Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) {
			CustomBlock.remove(gLoc.getGeneratedBlockLocation());
		}
	}

}