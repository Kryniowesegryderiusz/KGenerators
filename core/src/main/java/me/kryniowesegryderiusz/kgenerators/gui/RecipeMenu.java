package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;

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
		
		MenuItem ingredientsItem = MenuItemType.RECIPE_MENU_INGREDIENS.getMenuItem();
		ArrayList<Integer> slotList = ingredientsItem.getSlots();
		int lastId = -1;
		List<Recipe> recipes = Main.getInstance().getServer().getRecipesFor(generator.getGeneratorItem());
		ShapedRecipe recipe = (ShapedRecipe) recipes.get(0);
		Map<Character,ItemStack> ingredients = recipe.getIngredientMap();
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
		
		MenuItem resultItem = MenuItemType.RECIPE_MENU_RESULT.getMenuItem();
		
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
		if (!Menus.isVieving((Player) e.getWhoClicked(), MenuInventoryType.RECIPE)) return;
		
		int slot = e.getSlot();
		if (MenuItemType.RECIPE_MENU_BACK.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.RECIPE_MENU_BACK).isEnabled())
		{
			Menus.openMainMenu((Player) e.getWhoClicked());
		}
		e.setCancelled(true);
	}

}
