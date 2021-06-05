package me.kryniowesegryderiusz.kgenerators.files;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.Recipe;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Recipes;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class RecipesFile {
	
	public static void load()
	{
		Config file;

    	try {
    		file = ConfigManager.getConfig("recipes.yml", (String) null, true, false);
    		file.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error("Recipes file: Cant load recipes config. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
    	
    	ConfigurationSection recipesSection = file.getConfigurationSection("");
    	
    	int amount = 0;
    	
    	for(String generatorID: recipesSection.getKeys(false)){
    		
    		if (!generatorID.equals("example_generator"))
    		{
    			Generator generator = Generators.get(generatorID);
	    		if (generator == null){
	    			Logger.error("Recipes file: There isnt any "+generatorID+" generator in generators.yml! Recipe NOT LOADED!");
	    		}
	    		else
	    		{
	    			Recipe recipe = new Recipe(generator);

		    		ShapedRecipe shapedRecipe = Main.getRecipesLoader().getShapedRecipe(generator);
		    		
		    		ArrayList<String> shape = new ArrayList<String>();
		    		shape = (ArrayList<String>) file.getList(generatorID+".shape");
		    		shapedRecipe.shape(shape.get(0),shape.get(1),shape.get(2));
		    		recipe.setShape(shape);
		    		
		    		ConfigurationSection ingredientsSection = file.getConfigurationSection(generatorID+".ingredients");
		            for (String ingredientsString : ingredientsSection.getKeys(false)) {
		                char ingredientsChar = ingredientsString.charAt(0);
		                ItemStack item = null;
		                if (file.contains(generatorID + ".ingredients." + ingredientsString + ".item")) {
		                  item = XUtils.parseItemStack(file.getString(generatorID + ".ingredients." + ingredientsString + ".item"), "Recipes file", false);
		                  ItemMeta meta = null;
		                  if (item.getItemMeta() != null) {
		                    meta = item.getItemMeta();
		                  } else {
		                    meta = Main.getInstance().getServer().getItemFactory().getItemMeta(item.getType());
		                  } 
		                  if (file.contains(generatorID + ".ingredients." + ingredientsString + ".name"))
		                    meta.setDisplayName(file.getString(generatorID + ".ingredients." + ingredientsString + ".name")); 
		                  if (file.contains(generatorID + ".ingredients." + ingredientsString + ".lore")) {
		                    ArrayList<String> lore = new ArrayList<>();
		                    for (String s : (ArrayList<String>)file.getList(generatorID + ".ingredients." + ingredientsString + ".lore")) {
		                      if (s != null)
		                        lore.add(ChatColor.translateAlternateColorCodes('&', s)); 
		                    } 
		                    meta.setLore(lore);
		                  } 
		                  item.setItemMeta(meta);
		                } else {
		                  item = XUtils.parseItemStack(file.getString(generatorID + ".ingredients." + ingredientsString), "Recipes file", false);
		                } 
		                shapedRecipe = Main.getRecipesLoader().setIngredient(shapedRecipe, ingredientsChar, item);
		                recipe.addIngredient(ingredientsChar, item);
		              } 
		            
		            Recipes.add(generator, recipe);
		        	Bukkit.addRecipe(shapedRecipe);
		        	amount++;
	    		}
    		}
    	}
    	Logger.info("Recipes file: Loaded " + String.valueOf(amount) + " generator recipes!");

	}

}
