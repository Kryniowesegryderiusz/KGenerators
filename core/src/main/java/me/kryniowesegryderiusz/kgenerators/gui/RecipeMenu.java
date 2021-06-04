package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.managers.Recipes;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.classes.Recipe;

public class RecipeMenu implements Listener {
	
	public static Inventory get(Player player, Generator generator)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.RECIPE_MENU_INGREDIENS);
		exludedEnumMenuItems.add(MenuItemType.RECIPE_MENU_RESULT);
		
		Inventory menu = Lang.getMenuInventory(MenuInventoryType.RECIPE).getInv(MenuInventoryType.RECIPE, player, exludedEnumMenuItems);
		
		/*
		 * Ingredients
		 */
		
		MenuItem ingredientsItem = Lang.getMenuItem(MenuItemType.RECIPE_MENU_INGREDIENS);
		ArrayList<Integer> slotList = ingredientsItem.getSlots();
		int lastId = -1;

		Recipe recipe = Recipes.get(generator);
		HashMap<Character,ItemStack> ingredients = recipe.getIngredients();
		for (String s : recipe.getShape())
		{
			for (char c : s.toCharArray())
			{
				MenuItem recipeMenuItem = ingredientsItem.clone();
				
				ItemStack item = new ItemStack(Material.AIR);
				
				if (ingredients.containsKey(c) && ingredients.get(c) != null)
					item = ingredients.get(c);
				
				if (recipeMenuItem.getItemType().contains("<block>"))
					recipeMenuItem.setItemStack(item);
				
				recipeMenuItem.replace("<block_name>", Lang.getItemTypeName(item));
				
				lastId++;
				ItemStack readyItem = recipeMenuItem.build();
				try {
					menu.setItem(slotList.get(lastId), readyItem);
				} catch (Exception e1) {
					Logger.error("Lang: There is probably more ingredients than slots set in /lang/gui/recipe.ingredients");
					Logger.error(1);
				}
			}
		}
		
		/*
		 * Result Item
		 */
		
		MenuItem resultItem = Lang.getMenuItem(MenuItemType.RECIPE_MENU_RESULT);
		
		resultItem.setItemStack(generator.getGeneratorItem());
		
		if (resultItem.getItemType().contains("<generator>"))
			resultItem.setItemStack(generator.getGeneratorItem());
		resultItem.replace("<generator_name>", generator.getGeneratorItem().getItemMeta().getDisplayName());
		
		ItemStack readyItem = resultItem.build();

		for (int i : resultItem.getSlots())
			menu.setItem(i, readyItem);
		
		return menu;
	}
	
	@EventHandler
	public void onClick(final InventoryClickEvent e)
	{
		if(e.isCancelled()) return;
		Player p = (Player) e.getWhoClicked();
		if (!Menus.isVieving(p, MenuInventoryType.RECIPE)) return;
		
		int slot = e.getSlot();
		if (Lang.getMenuItem(MenuItemType.RECIPE_MENU_BACK).getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.RECIPE_MENU_BACK).isEnabled())
		{
			Menus.openMainMenu(p, Menus.getMenuPlayer(p).getGenerator());
		}
		e.setCancelled(true);
	}

}
