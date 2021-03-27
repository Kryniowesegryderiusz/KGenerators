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

import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItemAdditional;
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
		ArrayList<EnumMenuItem> exludedEnumMenuItems = new ArrayList<EnumMenuItem>();
		exludedEnumMenuItems.add(EnumMenuItem.MainMenuGenerator);
		
		Inventory menu = Lang.getMenuInventory(EnumMenuInventory.Main).getInv(EnumMenuInventory.Main, player, exludedEnumMenuItems);
		
		MenuItem generatorItem = EnumMenuItem.MainMenuGenerator.getMenuItem();
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
				generatorMenuItem.addLore(Lang.getMenuItemAdditionalLines(EnumMenuItemAdditional.Recipe));
			}
			else if (Upgrades.couldBeObtained(generator.getId()))
			{
				generatorMenuItem.addLore(Lang.getMenuItemAdditionalLines(EnumMenuItemAdditional.Upgrade));
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
		if (!Menus.isVieving((Player) e.getWhoClicked(), EnumMenuInventory.Main)) return;
		
		int slot = e.getSlot();
		
		ArrayList<Integer> slotList = EnumMenuItem.MainMenuGenerator.getMenuItem().getSlots();
		if (slotList.contains(slot) && Lang.getMenuItem(EnumMenuItem.MainMenuGenerator).isEnabled())
		{
			int lastId = -1;
			for (Entry<String, Generator> entry : Generators.getEntrySet())
			{
				lastId++;
				if(slotList.get(lastId) == slot)
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
		if (EnumMenuItem.MainMenuQuit.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(EnumMenuItem.MainMenuQuit).isEnabled())
		{
			Menus.closeInv((Player) e.getWhoClicked());
		}
		e.setCancelled(true);
	}
}
