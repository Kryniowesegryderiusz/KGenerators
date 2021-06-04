package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.Recipe;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Recipes;

public class PrepareItemCraftListener implements Listener {
	
	@EventHandler 
	public void onPrepareItemCraftEvent(PrepareItemCraftEvent  e)
	{
		if (e.getRecipe() == null)
			return;
		
		Generator generator = Generators.get(e.getRecipe().getResult());
		if (generator != null)
		{
			Recipe recipe = Recipes.get(generator);
			if (recipe != null)
			{
				ArrayList<ItemStack> recipeItems = recipe.getRecipe();
				
				//1 is the result
				for (int i = 1; i < 10; i++)
				{
					if (!e.getInventory().getContents()[i].getItemMeta().equals(recipeItems.get(i-1).getItemMeta()))
					{
						e.getInventory().setResult(new ItemStack(Material.AIR));
						return;
					}
				}
			}
		}
	}
}
