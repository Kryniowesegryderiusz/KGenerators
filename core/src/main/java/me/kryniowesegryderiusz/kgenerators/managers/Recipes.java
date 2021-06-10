package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.HashMap;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.Recipe;

public class Recipes {
	
	private static HashMap<Generator, Recipe> recipes = new HashMap<Generator, Recipe>();
	
	public static void add(Generator generator, Recipe recipe)
	{
		recipes.put(generator, recipe);
	}
	
	public static Recipe get(Generator generator)
	{
		if (recipes.containsKey(generator))
			return recipes.get(generator);
		else
		{
			for (org.bukkit.inventory.Recipe bukkitRecipe : Main.getInstance().getServer().getRecipesFor(generator.getGeneratorItem()))
			{
				if (bukkitRecipe.getResult().equals(generator.getGeneratorItem()))
				{
					if (bukkitRecipe instanceof ShapedRecipe)
						return new Recipe(generator, (ShapedRecipe) bukkitRecipe);
					else if (bukkitRecipe instanceof ShapelessRecipe)
						return new Recipe(generator, (ShapelessRecipe) bukkitRecipe);
				}
			}
			return null;
		}
	}
}
