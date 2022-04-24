package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.RecipesLoader;

public class RecipesLoader_1_13 implements RecipesLoader {

	@Override
	public ShapedRecipe getShapedRecipe(Generator generator) {
		NamespacedKey key = new NamespacedKey(Main.getInstance(), "KGenerators_"+generator.getId());
		ShapedRecipe shapedRecipe = new ShapedRecipe(key, generator.getGeneratorItem());
		return shapedRecipe;
	}

	@Override
	public ShapedRecipe setIngredient(ShapedRecipe shaped, char c, ItemStack item) {
		shaped.setIngredient(c, new RecipeChoice.ExactChoice(item));
		return shaped;
	}

}
