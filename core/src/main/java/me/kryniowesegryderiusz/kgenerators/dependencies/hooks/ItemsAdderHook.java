package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class ItemsAdderHook {
	
	private static boolean enabled = false;
	
	public static void handleGeneratorLocationRemove(GeneratorLocation gLoc) {
		if (!Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) return;
		CustomBlock.remove(gLoc.getGeneratedBlockLocation());
	}
	
	public static boolean isCustomBlock(Location location) {
		if (!Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) return false;
		return CustomBlock.byAlreadyPlaced(location.getBlock()) != null;
	}
	
	public static void removeCustomBlock(Location location) {
		if (!Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) return;
		CustomBlock.remove(location);
	}
	
	/**
	 * @return ArrayList of CustomBlock drops if ItemsAdder is enabled and its CustomBlock
	 */
	public static ArrayList<ItemStack> getCustomBlockDrops(Location location, ItemStack tool) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		if (Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)
				&& CustomBlock.byAlreadyPlaced(location.getBlock()) != null) {
			CustomBlock.byAlreadyPlaced(location.getBlock()).getLoot(tool, true);
		}
		return drops;
	}
	
	public static ItemStack getItemStack(String material) {
		if (!Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) return null;
		
		CustomStack customStack = CustomStack.getInstance(material);
		if (customStack == null)
			return null;
		return customStack.getItemStack();
	}
	
	public class ItemsAdderListeners implements Listener {
		@EventHandler
		public void onItemsAdderLoadDataEvent(ItemsAdderLoadDataEvent e) {
			if (!enabled) {
				enabled = true;
				Main.getInstance().enable();
			}
		}
	}

}