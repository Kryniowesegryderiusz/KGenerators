package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;

public class UpgradeMenu implements Listener {
	
	public static Inventory get(Player player, Generator generator)
	{		
		ArrayList<EnumMenuItem> exludedEnumMenuItems = new ArrayList<EnumMenuItem>();
		exludedEnumMenuItems.add(EnumMenuItem.UpgradeMenuIngredient);
		exludedEnumMenuItems.add(EnumMenuItem.UpgradeMenuResult);
		
		Generator previousGenerator = Generators.get(Upgrades.getPreviousGeneratorId(generator.getId()));
		
		Inventory menu = Lang.getMenuInventory(EnumMenuInventory.Upgrade).getInv(EnumMenuInventory.Upgrade, player, exludedEnumMenuItems, "<cost>", String.valueOf(previousGenerator.getUpgrade().getCost()));
		
		/*
		 * Ingredient item
		 */
		MenuItem ingredientItem = EnumMenuItem.UpgradeMenuIngredient.getMenuItem();
		
		ingredientItem.setItemStack(previousGenerator.getGeneratorItem());
		
		if (ingredientItem.getItemType().contains("<generator>"))
			ingredientItem.setItemStack(previousGenerator.getGeneratorItem());
		ingredientItem.replace("<generator_name>", previousGenerator.getGeneratorItem().getItemMeta().getDisplayName());
		
		ItemStack readyItem = ingredientItem.build();

		for (int i : ingredientItem.getSlots())
			menu.setItem(i, readyItem);
		
		/*
		 * Result Item
		 */
				
		MenuItem resultItem = EnumMenuItem.UpgradeMenuResult.getMenuItem();
		
		resultItem.setItemStack(generator.getGeneratorItem());
		
		if (resultItem.getItemType().contains("<generator>"))
			resultItem.setItemStack(generator.getGeneratorItem());
		resultItem.replace("<generator_name>", generator.getGeneratorItem().getItemMeta().getDisplayName());
		
		readyItem = resultItem.build();

		for (int i : resultItem.getSlots())
			menu.setItem(i, readyItem);
		
		return menu;
	}
	
	@EventHandler
	public void onClick(final InventoryClickEvent e)
	{
		if(e.isCancelled()) return;
		if (!Menus.isVieving((Player) e.getWhoClicked(), EnumMenuInventory.Upgrade)) return;
		
		int slot = e.getSlot();
		if (EnumMenuItem.UpgradeMenuBack.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(EnumMenuItem.UpgradeMenuBack).isEnabled())
		{
			Menus.openMainMenu((Player) e.getWhoClicked());
		}
		e.setCancelled(true);
	}

}
