package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;

public class RecipesLoader_1_8 implements RecipesLoader {
	
	//1.8 nie ma Namespace key
	@Override
	public ShapedRecipe getShapedRecipe(Generator generator) {
		// TODO Auto-generated method stub
		return new ShapedRecipe(generator.getGeneratorItem());
	}

	@Override
	public ShapedRecipe setIngredient(ShapedRecipe shaped, char c, ItemStack item) {
		shaped.setIngredient(c, item.getData());
		return shaped;
	}

}
