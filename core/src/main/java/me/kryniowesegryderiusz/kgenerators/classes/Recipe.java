package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import lombok.Getter;
import lombok.Setter;

public class Recipe {
	
	@Getter
	Generator generator;
	@Getter
	private HashMap<Character, ItemStack> ingredients = new HashMap<Character, ItemStack>();
	@Getter @Setter
	private ArrayList<String> shape = new ArrayList<String>();
	
	public Recipe(Generator generator)
	{
		this.generator = generator;
		ingredients.put(' ', new ItemStack(Material.AIR));
	}
	
	public Recipe(Generator generator, ShapedRecipe shaped)
	{
		this.generator = generator;
		ingredients.putAll(shaped.getIngredientMap());
		for (String s : shaped.getShape())
		{
			shape.add(s);
		}
	}
	
	public Recipe(Generator generator, ShapelessRecipe shapeless) {
		this.generator = generator;
		char[] letters = {'A','B','C','D','E','F','G','H','I'};
		shape.add("ABC");
		shape.add("DEF");
		shape.add("GHI");
		for (char l : letters)
		{
			ingredients.put(l, new ItemStack(Material.AIR));
		}
		
		for (int i = 0; i < shapeless.getIngredientList().size(); i++)
		{
			ingredients.put(letters[i], shapeless.getIngredientList().get(i));
		}
	}

	public void addIngredient(char charr, ItemStack item)
	{
		ingredients.put(charr, item);
	}
	
	public ItemStack getGeneratorItem()
	{
		return this.generator.getGeneratorItem();
	}
	
	/**
	 * Get 9 full items from recipe
	 * @return
	 */
	public ArrayList<ItemStack> getRecipe()
	{
		ArrayList<ItemStack> recipe = new ArrayList<ItemStack>();
		for (String s : shape)
		{
			for (int i = 0; i < 3; i++)
			{
				recipe.add(ingredients.get(s.charAt(i)));
			}
		}
		return recipe;
	}
}
