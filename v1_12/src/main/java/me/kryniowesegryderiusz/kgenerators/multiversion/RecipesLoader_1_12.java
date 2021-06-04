package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.Recipe;

public class RecipesLoader_1_12 implements RecipesLoader {
	
	HashMap<Generator, Recipe> recipes = new HashMap<Generator, Recipe>();

	@Override
	public ShapedRecipe getShapedRecipe(Generator generator) {
		NamespacedKey key = new NamespacedKey(Main.getInstance(), "KGenerators_"+generator.getId());
		ShapedRecipe shapedRecipe = new ShapedRecipe(key, generator.getGeneratorItem());
		return shapedRecipe;
	}

}
