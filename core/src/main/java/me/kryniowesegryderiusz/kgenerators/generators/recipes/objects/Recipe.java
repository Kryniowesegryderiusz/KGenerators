package me.kryniowesegryderiusz.kgenerators.generators.recipes.objects;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.RecipesManager;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class Recipe {
	
	@Getter
	Generator generator;
	@Getter
	private HashMap<Character, ItemStack> ingredients = new HashMap<Character, ItemStack>();
	@Getter @Setter
	private ArrayList<String> shape = new ArrayList<String>();
	
	/**
	 * Loads recipe from file and adds it to RecipesManager
	 * @param recipesManager
	 * @param file
	 * @param generatorID aka path
	 */
	@SuppressWarnings("unchecked")
	public Recipe(RecipesManager recipesManager, Config file, String generatorID)
	{
		if (!generatorID.equals("example_generator") && !generatorID.equals("enabled"))	{
			Generator generator = Main.getGenerators().get(generatorID);
			if (generator == null){
				Logger.error("Recipes file: There isnt any `"+generatorID+"` generator in generators.yml! Recipe NOT LOADED!");
			} else {
				this.generator = generator;
				ingredients.put(' ', new ItemStack(Material.AIR));
				
	    		ShapedRecipe shapedRecipe = Main.getMultiVersion().getRecipesLoader().getShapedRecipe(generator);
	    		
	    		ArrayList<String> shape = new ArrayList<String>();
	    		shape = (ArrayList<String>) file.getList(generatorID+".shape");
	    		shapedRecipe.shape(shape.get(0),shape.get(1),shape.get(2));
	    		this.setShape(shape);
	    		
	    		ConfigurationSection ingredientsSection = file.getConfigurationSection(generatorID+".ingredients");
	            for (String ingredientsString : ingredientsSection.getKeys(false)) {
	                char ingredientsChar = ingredientsString.charAt(0);
	                
	                ItemStack item = FilesUtils.loadItemStack(file, generatorID + ".ingredients." + ingredientsString, false);

	                shapedRecipe = Main.getMultiVersion().getRecipesLoader().setIngredient(shapedRecipe, ingredientsChar, item);
	                this.addIngredient(ingredientsChar, item);
	            }
	            
	            recipesManager.add(generator, this);
	            
	        	Bukkit.addRecipe(shapedRecipe);
	        	
	        	Logger.debug("Recipes file: Loaded recipe for `"+generatorID+"`! Recipe: " + this.toString());
			}
		}
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
	
	public String toString() {
		ArrayList<String> string = new ArrayList<String>();
		for (String s : this.shape) {
			ArrayList<String> line = new ArrayList<String>();
			for (char c : s.toCharArray()) {
				if (this.ingredients.get(c).hasItemMeta() && this.ingredients.get(c).getItemMeta().hasDisplayName())
					line.add(this.ingredients.get(c).getItemMeta().getDisplayName());
				else
					line.add(this.ingredients.get(c).getType().name());
			}
			string.add(line.toString());
		}
		return string.toString();
	}
}
