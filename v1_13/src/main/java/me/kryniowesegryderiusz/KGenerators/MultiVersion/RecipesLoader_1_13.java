package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import me.kryniowesegryderiusz.KGenerators.KGenerators;
import me.kryniowesegryderiusz.KGenerators.Utils.Config;
import me.kryniowesegryderiusz.KGenerators.XSeries.XUtils;

public class RecipesLoader_1_13 implements RecipesLoader {

	@Override
	public void loadRecipes() {
		Config file = KGenerators.getRecipesFile();
		
		if (!file.contains("recipes")) {
    		return;
    	}
		
    	ConfigurationSection recipesSection = file.getConfigurationSection("recipes");
    	
    	for(String generatorID: recipesSection.getKeys(false)){
    		
    		NamespacedKey key = new NamespacedKey(KGenerators.getInstance(), "KGenerators_"+generatorID);
    		
    		if (!KGenerators.generators.containsKey(generatorID)){
    			System.out.println("[KGenerators] !!! ERROR !!! There isnt any "+generatorID+" generator in config.yml! Recipe NOT LOADED!");
    			return;
    		}
    		
    		ItemStack generatorItem = KGenerators.generators.get(generatorID).getGeneratorItem();
    		
    		ShapedRecipe recipe = new ShapedRecipe(key, generatorItem);
    		
    		ArrayList<String> shape = new ArrayList<String>();
    		shape = (ArrayList<String>) file.getList("recipes."+generatorID+".shape");
    		
    		//System.out.println(shape.get(0));
    		//System.out.println(shape.get(1));
    		//System.out.println(shape.get(2));
    		recipe.shape(shape.get(0),shape.get(1),shape.get(2));
    		
    		ConfigurationSection ingredientsSection = file.getConfigurationSection("recipes."+generatorID+".ingredients");
        	for(String ingredientsString: ingredientsSection.getKeys(false)){
        		char ingredientsChar = ingredientsString.charAt(0);
        		ItemStack item = XUtils.parseItemStack(file.getString("recipes."+generatorID+".ingredients."+ingredientsString));
        		//System.out.println(ingredientsChar + " has " + item);
        		recipe.setIngredient(ingredientsChar, item.getType());
        	}
        	//System.out.println(recipe.getShape());
        	//System.out.println(recipe.getIngredientMap());
        	Bukkit.addRecipe(recipe);
        	System.out.println("[KGenerators] Loaded recipe for " + generatorID);
    	}
		
	}

}
