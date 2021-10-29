package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Recipes;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;

public class MainMenu {
	
	public static Inventory get(Player player, int page)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_GENERATOR);
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_LIMITS);
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_PAGE_PREVIOUS);
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_PAGE_NEXT);
		
		Inventory menu = Lang.getMenuInventoryStorage().get(MenuInventoryType.MAIN).getInv(MenuInventoryType.MAIN, player, exludedEnumMenuItems);
		
		if (Main.getSettings().isLimits() && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).isEnabled())
		{
			for (int i : Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).getSlots())
			{
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).build());
			}
		}
		
		if (page > 0)
		{
			for (int i : Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_PREVIOUS).getSlots())
			{
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_PREVIOUS).build());
			}
		}
		
		int nrOfGenerators = Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size();
		
		if (Generators.getEntrySet().size() > (page+1)*nrOfGenerators)
		{
			for (int i : Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_NEXT).getSlots())
			{
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_NEXT).build());
			}
		}
		
		MenuItem generatorItem = Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR);
		ArrayList<Integer> slotList = generatorItem.getSlots();
		int lastId = -1;
		for (Entry<String, Generator> e : Generators.getSpecifiedEntrySet(page*nrOfGenerators, nrOfGenerators))
		{
			MenuItem generatorMenuItem = generatorItem.clone();
			Generator generator = e.getValue();
			
			if (generatorMenuItem.getItemType().contains("<generator>"))
				generatorMenuItem.setItemStack(generator.getGeneratorItem());
			
			generatorMenuItem.replace("<generator_name>", generator.getGeneratorItem().getItemMeta().getDisplayName());
			
			if (Recipes.get(generator) != null)
			{
				generatorMenuItem.addLore(Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.RECIPE));
			}
			else if (Upgrades.couldBeObtained(generator.getId()))
			{
				generatorMenuItem.addLore(Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.UPGRADE));
			}
			
			lastId++;
			ItemStack readyItem = generatorMenuItem.build();
			try {
				menu.setItem(slotList.get(lastId), readyItem);
			} catch (Exception e1) {
				Logger.error("MainMenu: Something went wrong, while creating MainMenu index " + lastId + " on page " + page);
				Logger.error(e1);
			}
		}

		return menu;
	}
	
	public static void onClick(Player p, int slot, ClickType clickType)
	{
		if (Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_QUIT).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_QUIT).isEnabled())
		{
			Menus.closeInv(p);
		}
		else if (Main.getSettings().isLimits() && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).isEnabled())
		{
			Menus.openLimitsMenu(p);
		}
		else if (Menus.getMenuPlayer(p).getPage() > 0 && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_PREVIOUS).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_PREVIOUS).isEnabled())
		{
			Menus.openMainMenu(p, Menus.getMenuPlayer(p).getPage()-1);
		}
		else if (Generators.getEntrySet().size() > (Menus.getMenuPlayer(p).getPage()+1)*Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size() 
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_NEXT).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_NEXT).isEnabled())
		{
			Menus.openMainMenu(p, Menus.getMenuPlayer(p).getPage()+1);
		}
		else if (Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).isEnabled())
		{
			int lastId = -1;
			for (Entry<String, Generator> entry : Generators.getSpecifiedEntrySet(Menus.getMenuPlayer(p).getPage()*Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size(), Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size()))
			{
				lastId++;
				if (Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size() == lastId)
					break;
				
				if(Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().get(lastId) == slot)
				{
					if (clickType == ClickType.LEFT)
						Menus.openChancesMenu(p, entry.getValue());
					else if (clickType == ClickType.RIGHT)
					{
						Generator generator = entry.getValue();
						if (Recipes.get(generator) != null)
						{
							Menus.openRecipeMenu(p, generator);
						}
						else if (Upgrades.couldBeObtained(generator.getId()))
						{
							Menus.openUpgradeMenu(p, generator);
						}
					}
				}
			}
		}
	}
}
