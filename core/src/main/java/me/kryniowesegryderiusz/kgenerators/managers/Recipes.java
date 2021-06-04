package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.HashMap;

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
		return recipes.get(generator);
	}
}
