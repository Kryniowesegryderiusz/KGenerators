package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;

public class ChancesMenu {
	
	public static Inventory get(Player player, Generator generator)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.CHANCES_MENU_CHANCE);
		
		Inventory menu = Lang.getMenuInventory(MenuInventoryType.CHANCES).getInv(MenuInventoryType.CHANCES, player, exludedEnumMenuItems);

		MenuItem generatorMenuItem = Lang.getMenuItem(MenuItemType.CHANCES_MENU_CHANCE);
		
		ArrayList<Integer> slotList = generatorMenuItem.getSlots();
		int lastId = -1;
		for (Entry<ItemStack, Double> e : generator.getChances().entrySet())
		{
			ItemStack item = e.getKey().clone();
			MenuItem chanceMenuItem = generatorMenuItem.clone();
			if (chanceMenuItem.getItemType().contains("<block>"))
				chanceMenuItem.setItemStack(item);

			chanceMenuItem.replace("<block_name>", Lang.getItemTypeName(item));
			chanceMenuItem.replace("<chance>", String.valueOf(generator.getChancePercent(item)));
			
			lastId++;
			
			ItemStack readyItem = chanceMenuItem.build();
			try {
				menu.setItem(slotList.get(lastId), readyItem);
			} catch (Exception e1) {
				Logger.error("Lang: There is probably more generators than slots set in /lang/gui/chances.block");
				Logger.error(e1);
			}
		}
		
		return menu;
	}
	
	public static void onClick(Player p, int slot)
	{		
		if (Lang.getMenuItem(MenuItemType.CHANCES_MENU_BACK).getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.CHANCES_MENU_BACK).isEnabled())
		{
			Menus.openMainMenu(p, Menus.getMenuPlayer(p).getGenerator());
		}
	}
}
