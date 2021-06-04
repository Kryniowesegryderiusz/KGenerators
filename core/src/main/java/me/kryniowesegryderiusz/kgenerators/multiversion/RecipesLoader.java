package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.inventory.ShapedRecipe;

import me.kryniowesegryderiusz.kgenerators.classes.Generator;

public interface RecipesLoader {
	
	public ShapedRecipe getShapedRecipe(Generator generator);
	
}
