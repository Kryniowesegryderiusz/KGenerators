package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class RecipesLoader_1_8 implements RecipesLoader {
	
	//1.8 nie ma Namespace key

	@Override
	public void loadRecipes(Config file) {		
		
    	ConfigurationSection recipesSection = file.getConfigurationSection("recipes");
    	
    	int amount = 0;
    	
    	for(String generatorID: recipesSection.getKeys(false)){
    		
    		if (!generatorID.equals("example_generator"))
    		{
	    		if (!Generators.exists(generatorID)){
	    			Logger.error("Recipes file: There isnt any "+generatorID+" generator in generators.yml! Recipe NOT LOADED!");
	    		}
	    		else
	    		{
		    		ItemStack generatorItem = Generators.get(generatorID).getGeneratorItem();
		    		
		    		ShapedRecipe recipe = new ShapedRecipe(generatorItem);
		    		
		    		ArrayList<String> shape = new ArrayList<String>();
		    		shape = (ArrayList<String>) file.getList(generatorID+".shape");
		    		
		    		recipe.shape(shape.get(0),shape.get(1),shape.get(2));
		    		
		    		ConfigurationSection ingredientsSection = file.getConfigurationSection(generatorID+".ingredients");
		        	for(String ingredientsString: ingredientsSection.getKeys(false)){
		        		char ingredientsChar = ingredientsString.charAt(0);
		        		ItemStack item = XUtils.parseItemStack(file.getString(generatorID+".ingredients."+ingredientsString), "Recipes file", false);
		        		recipe.setIngredient(ingredientsChar, item.getData());
		        	}
		        	Bukkit.addRecipe(recipe);
		        	amount++;
	    		}
	    	}
		}
    	
    	Logger.info("Recipes file: Loaded " + String.valueOf(amount) + " generator recipes!");
	}

}
