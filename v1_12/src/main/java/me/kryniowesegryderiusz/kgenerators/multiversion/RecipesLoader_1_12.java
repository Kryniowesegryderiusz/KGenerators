package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.util.HashMap;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.objects.Recipe;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.RecipesLoader;

public class RecipesLoader_1_12 implements RecipesLoader {
	
	HashMap<Generator, Recipe> recipes = new HashMap<Generator, Recipe>();

	@Override
	public ShapedRecipe getShapedRecipe(Generator generator) {
		NamespacedKey key = new NamespacedKey(Main.getInstance(), "KGenerators_"+generator.getId());
		ShapedRecipe shapedRecipe = new ShapedRecipe(key, generator.getGeneratorItem());
		return shapedRecipe;
	}

	@Override
	public ShapedRecipe setIngredient(ShapedRecipe shaped, char c, ItemStack item) {
		shaped.setIngredient(c, item.getData());
		return shaped;
	}

}
