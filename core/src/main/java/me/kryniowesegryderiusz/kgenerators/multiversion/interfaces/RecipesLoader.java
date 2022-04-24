package me.kryniowesegryderiusz.kgenerators.multiversion.interfaces;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;

public interface RecipesLoader {
	
	public ShapedRecipe getShapedRecipe(Generator generator);
	public ShapedRecipe setIngredient(ShapedRecipe shaped, char c, ItemStack item);
	
}
