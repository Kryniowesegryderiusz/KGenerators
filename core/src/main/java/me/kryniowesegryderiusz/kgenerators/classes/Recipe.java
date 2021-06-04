package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
