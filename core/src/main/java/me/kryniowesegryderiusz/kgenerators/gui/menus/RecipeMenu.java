package me.kryniowesegryderiusz.kgenerators.gui.menus;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.objects.Recipe;
import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class RecipeMenu {
	
	public static Inventory get(Player player, Generator generator)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.RECIPE_MENU_INGREDIENS);
		exludedEnumMenuItems.add(MenuItemType.RECIPE_MENU_RESULT);
		
		Inventory menu = Lang.getMenuInventoryStorage().get(MenuInventoryType.RECIPE).getInv(MenuInventoryType.RECIPE, player, exludedEnumMenuItems);
		
		/*
		 * Ingredients
		 */
		
		MenuItem ingredientsItem = Lang.getMenuItemStorage().get(MenuItemType.RECIPE_MENU_INGREDIENS);
		ArrayList<Integer> slotList = ingredientsItem.getSlots();
		int lastId = -1;
		
		Recipe recipe = Main.getRecipes().get(generator);
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
				
				recipeMenuItem.replace("<block_name>", Lang.getCustomNamesStorage().getItemTypeName(item));
				
				lastId++;
				ItemStack readyItem = recipeMenuItem.build(player);
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
		
		MenuItem resultItem = Lang.getMenuItemStorage().get(MenuItemType.RECIPE_MENU_RESULT);
		
		resultItem.setItemStack(generator.getGeneratorItem());
		
		if (resultItem.getItemType().contains("<generator>"))
			resultItem.setItemStack(generator.getGeneratorItem());
		resultItem.replace("<generator_name>", generator.getGeneratorItem().getItemMeta().getDisplayName());
		
		ItemStack readyItem = resultItem.build(player);

		for (int i : resultItem.getSlots())
			menu.setItem(i, readyItem);
		
		return menu;
	}
	
	public static void onClick(Player p, int slot)
	{
		if (Lang.getMenuItemStorage().get(MenuItemType.RECIPE_MENU_BACK).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.RECIPE_MENU_BACK).isEnabled())
		{
			Main.getMenus().openMainMenu(p, Main.getMenus().getMenuPlayer(p).getGenerator());
		}
	}

}
