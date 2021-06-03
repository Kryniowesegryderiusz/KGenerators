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
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;

public class MainMenu implements Listener {
	
	public static Inventory get(Player player)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_GENERATOR);
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_LIMITS);
		
		Inventory menu = Lang.getMenuInventory(MenuInventoryType.MAIN).getInv(MenuInventoryType.MAIN, player, exludedEnumMenuItems);
		
		if (Main.getSettings().isLimits() && Lang.getMenuItem(MenuItemType.MAIN_MENU_LIMITS).isEnabled())
		{
			for (int i : MenuItemType.MAIN_MENU_LIMITS.getMenuItem().getSlots())
			{
				menu.setItem(i, Lang.getMenuItem(MenuItemType.MAIN_MENU_LIMITS).build());
			}
		}
		
		MenuItem generatorItem = MenuItemType.MAIN_MENU_GENERATOR.getMenuItem();
		ArrayList<Integer> slotList = generatorItem.getSlots();
		int lastId = -1;
		for (Entry<String, Generator> e : Generators.getEntrySet())
		{
			MenuItem generatorMenuItem = generatorItem.clone();
			Generator generator = e.getValue();
			
			if (generatorMenuItem.getItemType().contains("<generator>"))
				generatorMenuItem.setItemStack(generator.getGeneratorItem());
			
			generatorMenuItem.replace("<generator_name>", generator.getGeneratorItem().getItemMeta().getDisplayName());
			
			List<Recipe> recipe = Main.getInstance().getServer().getRecipesFor(generator.getGeneratorItem());
			if (!recipe.isEmpty() && recipe.get(0).getResult().equals(generator.getGeneratorItem()))
			{
				generatorMenuItem.addLore(Lang.getMenuItemAdditionalLines(MenuItemAdditionalLines.RECIPE));
			}
			else if (Upgrades.couldBeObtained(generator.getId()))
			{
				generatorMenuItem.addLore(Lang.getMenuItemAdditionalLines(MenuItemAdditionalLines.UPGRADE));
			}
			
			lastId++;
			ItemStack readyItem = generatorMenuItem.build();
			try {
				menu.setItem(slotList.get(lastId), readyItem);
			} catch (Exception e1) {
				Logger.error("Lang: There is probably more generators than slots set in /lang/gui/main.generator");
				Logger.error(1);
			}
		}

		return menu;
	}
	
	@EventHandler
	public void onClick(final InventoryClickEvent e)
	{
		if(e.isCancelled()) return;
		if (!Menus.isVieving((Player) e.getWhoClicked(), MenuInventoryType.MAIN)) return;
		
		int slot = e.getSlot();
		
		if (MenuItemType.MAIN_MENU_QUIT.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.MAIN_MENU_QUIT).isEnabled())
		{
			Menus.closeInv((Player) e.getWhoClicked());
		}
		else if (Main.getSettings().isLimits() && MenuItemType.MAIN_MENU_LIMITS.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.MAIN_MENU_LIMITS).isEnabled())
		{
			Menus.openLimitsMenu((Player) e.getWhoClicked());
		}
		else if (MenuItemType.MAIN_MENU_GENERATOR.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.MAIN_MENU_GENERATOR).isEnabled())
		{
			int lastId = -1;
			for (Entry<String, Generator> entry : Generators.getEntrySet())
			{
				lastId++;
				if(MenuItemType.MAIN_MENU_GENERATOR.getMenuItem().getSlots().get(lastId) == slot)
				{
					if (e.getClick() == ClickType.LEFT)
						Menus.openChancesMenu((Player) e.getWhoClicked(), entry.getValue());
					else if (e.getClick() == ClickType.RIGHT)
					{
						Generator generator = entry.getValue();
						List<Recipe> recipe = Main.getInstance().getServer().getRecipesFor(generator.getGeneratorItem());
						if (!recipe.isEmpty() && recipe.get(0).getResult().equals(generator.getGeneratorItem()))
						{
							Menus.openRecipeMenu((Player) e.getWhoClicked(), generator);
						}
						else if (Upgrades.couldBeObtained(generator.getId()))
						{
							Menus.openUpgradeMenu((Player) e.getWhoClicked(), generator);
						}
					}
				}
			}
		}
		e.setCancelled(true);
	}
}
