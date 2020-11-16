package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

import me.kryniowesegryderiusz.KGenerators.KGenerators;
import me.kryniowesegryderiusz.KGenerators.Utils.Config;
import me.kryniowesegryderiusz.KGenerators.XSeries.XUtils;

public class RecipesLoader_1_8 implements RecipesLoader {
	
	//1.8 nie ma Namespace key

	@Override
	public void loadRecipes() {
		Config file = KGenerators.getRecipesFile();
		
		if (!file.contains("recipes")) {
    		return;
    	}
		
    	ConfigurationSection recipesSection = file.getConfigurationSection("recipes");
    	
    	for(String generatorID: recipesSection.getKeys(false)){
    		
    		if (!KGenerators.generators.containsKey(generatorID)){
    			System.out.println("[KGenerators] !!! ERROR !!! There isnt any "+generatorID+" generator in config.yml! Recipe NOT LOADED!");
    			return;
    		}
    		
    		ItemStack generatorItem = KGenerators.generators.get(generatorID).getGeneratorItem();
    		
    		ShapedRecipe recipe = new ShapedRecipe(generatorItem);
    		
    		ArrayList<String> shape = new ArrayList<String>();
    		shape = (ArrayList<String>) file.getList("recipes."+generatorID+".shape");
    		
    		recipe.shape(shape.get(0),shape.get(1),shape.get(2));
    		
    		ConfigurationSection ingredientsSection = file.getConfigurationSection("recipes."+generatorID+".ingredients");
        	for(String ingredientsString: ingredientsSection.getKeys(false)){
        		char ingredientsChar = ingredientsString.charAt(0);
        		ItemStack item = XUtils.parseItemStack(file.getString("recipes."+generatorID+".ingredients."+ingredientsString));
        		recipe.setIngredient(ingredientsChar, item.getData());
        	}
        	Bukkit.addRecipe(recipe);
        	System.out.println("[KGenerators] Loaded recipe for " + generatorID);
    	}
		
	}

}
