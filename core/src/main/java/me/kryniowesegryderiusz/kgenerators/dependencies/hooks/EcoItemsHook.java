package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.items.Items;
import com.willfp.eco.core.recipe.Recipes;
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.RecipesManager;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.objects.Recipe;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class EcoItemsHook {
	
	public static ItemStack getItemStack(String material) {
		if (Main.getDependencies().isEnabled(Dependency.ECO_ITEMS)) {
			EcoItem ei = EcoItems.getByID(material);
			if (ei != null)
				return ei.getCustomItem().getItem();
		}
		return null;
	}
	
	public static void processRecipes(RecipesManager recipesManager) {
		if (Main.getDependencies().isEnabled(Dependency.ECO_ITEMS)) {
			for (Recipe r : recipesManager.getAll()) {
				
				ArrayList<ItemStack> recipe = r.getRecipe();
				
				ShapedCraftingRecipe.Builder builder = ShapedCraftingRecipe.builder((EcoPlugin) Main.getInstance().getServer().getPluginManager().getPlugin("EcoItems"), "kgenerators_"+r.getGenerator().getId());
				builder.setOutput(r.getGeneratorItem());
				for (int i = 0; i < 9; i++) {
					builder.setRecipePart(i, Items.getCustomItem(recipe.get(i)));
				}
				
				Recipes.register(builder.build());
				
				Logger.debugPluginLoad("Dependencies: EcoItems: Registered new recipe: kgenerators_"+r.getGenerator().getId());
			}
		}
	}
}
