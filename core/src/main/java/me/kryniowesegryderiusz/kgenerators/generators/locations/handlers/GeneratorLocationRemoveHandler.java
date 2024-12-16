package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomBlock;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.GeneratorRemoveEvent;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.ItemsAdderHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.utils.PlayerUtils;

public class GeneratorLocationRemoveHandler {
	
	public void handle(GeneratorLocation gLocation, boolean drop, @Nullable Player toWho) {
		Location location = gLocation.getLocation();
		Generator generator = gLocation.getGenerator();
		
		Main.getSchedules().remove(gLocation);
		Main.getDatabases().getDb().removeSchedule(gLocation);
		
		Main.getPlacedGenerators().removeLoaded(gLocation);
		Main.getDatabases().getDb().removeGenerator(location);
		
		gLocation.getOwner().removeGeneratorFromPlayer(gLocation.getGenerator());
		
		ItemStack generatorItem = generator.getGeneratorItem();
		
		Main.getInstance().getServer().getPluginManager().callEvent(new GeneratorRemoveEvent(gLocation, generatorItem, drop, toWho));
		
		if (drop) {
			PlayerUtils.dropToInventory(Main.getSettings().isPickUpToEq() ? toWho : null, location, generatorItem);
		}
		
		if (Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) {
			CustomBlock cb = CustomBlock.byAlreadyPlaced(gLocation.getGeneratedBlockLocation().getBlock());
			if (cb != null)
				cb.remove();
		}
		
		SuperiorSkyblock2Hook.handleGeneratorLocationRemove(gLocation);
		
		Main.getMultiVersion().getBlocksUtils().setBlock(location, Material.AIR);
		Main.getMultiVersion().getBlocksUtils().setBlock(gLocation.getGeneratedBlockLocation(), Material.AIR);
		
		ItemsAdderHook.handleGeneratorLocationRemove(gLocation);
		
	}
}
