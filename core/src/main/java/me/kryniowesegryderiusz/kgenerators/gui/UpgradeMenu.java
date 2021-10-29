package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;

public class UpgradeMenu {
	
	public static Inventory get(Player player, Generator generator)
	{		
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.UPGRADE_MENU_INGREDIENT);
		exludedEnumMenuItems.add(MenuItemType.UPGRADE_MENU_RESULT);
		exludedEnumMenuItems.add(MenuItemType.UPGRADE_MENU_MARKER);
		
		Generator previousGenerator = Generators.get(Upgrades.getPreviousGeneratorId(generator.getId()));
		
		Inventory menu = Lang.getMenuInventoryStorage().get(MenuInventoryType.UPGRADE).getInv(MenuInventoryType.UPGRADE, player, exludedEnumMenuItems);
		
		/*
		 * Ingredient item
		 */
		MenuItem ingredientItem = Lang.getMenuItemStorage().get(MenuItemType.UPGRADE_MENU_INGREDIENT);
		
		ingredientItem.setItemStack(previousGenerator.getGeneratorItem());
		
		if (ingredientItem.getItemType().contains("<generator>"))
			ingredientItem.setItemStack(previousGenerator.getGeneratorItem());
		ingredientItem.replace("<generator_name>", previousGenerator.getGeneratorItemName());
		
		ItemStack readyItem = ingredientItem.build();

		for (int i : ingredientItem.getSlots())
			menu.setItem(i, readyItem);
		
		/*
		 * Result Item
		 */
				
		MenuItem resultItem = Lang.getMenuItemStorage().get(MenuItemType.UPGRADE_MENU_RESULT);
		
		if (resultItem.getItemType().contains("<generator>"))
			resultItem.setItemStack(generator.getGeneratorItem());
		resultItem.replace("<generator_name>", generator.getGeneratorItemName());
		
		readyItem = resultItem.build();

		for (int i : resultItem.getSlots())
			menu.setItem(i, readyItem);
		
		/*
		 * Marker item
		 */
		
		MenuItem markerItem = Lang.getMenuItemStorage().get(MenuItemType.UPGRADE_MENU_MARKER);
		
		markerItem.replaceLore("<costs>", previousGenerator.getUpgrade().getCostsFormattedGUI());
		
		readyItem = markerItem.build();
		
		for (int i : markerItem.getSlots())
			menu.setItem(i, readyItem);
		
		/*
		 * End
		 */
		
		return menu;
	}
	
	public static void onClick(Player p, int slot)
	{
		if (Lang.getMenuItemStorage().get(MenuItemType.UPGRADE_MENU_BACK).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.UPGRADE_MENU_BACK).isEnabled())
		{
			Menus.openMainMenu(p, Menus.getMenuPlayer(p).getGenerator());
		}
	}

}
