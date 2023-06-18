package me.kryniowesegryderiusz.kgenerators.generators.recipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.EcoItemsHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.recipes.objects.Recipe;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.ConfigManager;

public class RecipesManager {
	
	private HashMap<Generator, Recipe> recipes = new HashMap<Generator, Recipe>();
	
	public RecipesManager(){
		Logger.debugPluginLoad("RecipesManager: Setting up manager");
		
		Config file;

    	try {
    		file = ConfigManager.getConfig("recipes.yml", (String) null, true, false);
    		file.loadConfig();
		} catch (Exception e) {
			Logger.error("Recipes file: Cant load recipes config. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
    	
		if (!file.contains("enabled") || file.getBoolean("enabled") != true)
		{
			Logger.info("Recipes file: Recipes are disabled. You can enable them in recipes.yml");
			return;
		}
    	
    	for(String generatorID: file.getConfigurationSection("").getKeys(false)) {
			try {
				new Recipe(this, file, generatorID);
			} catch (Exception e) {
				Logger.error("Recipes file: Cannot load recipe for " + generatorID + ": " + e.getMessage());
			}
    	}
    	
    	if (Main.getDependencies().isEnabled(Dependency.ECO_ITEMS))
    		EcoItemsHook.processRecipes(this);
    	
    	Logger.info("Recipes file: Loaded " + this.recipes.size() + " recipes");
	}
	
	public void add(Generator generator, Recipe recipe)
	{
		recipes.put(generator, recipe);
	}
	
	public Recipe get(Generator generator)
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
	
	public boolean hasRecipes() {
		return this.recipes.size() != 0;
	}
	
	public boolean isGeneratorRecipe(Generator generator, ArrayList<ItemStack> items) {
		if (items.size() == 9 && this.get(generator) != null) {
			ArrayList<ItemStack> recipeItems = this.get(generator).getRecipe();
			for (int i  = 0; i < 9; i++) {
				
				ItemStack item = items.get(i);
				if (item == null) item = new ItemStack(Material.AIR);
				if (!item.isSimilar(recipeItems.get(i)))
					return false;
			}
			return true;
		} else return false;
	}
	
	public Collection<Recipe> getAll() {
		return this.recipes.values();
	}

	public boolean isGeneratorRecipe(Generator g, ItemStack[] items) {
		ArrayList<ItemStack> recipeItemsArray = new ArrayList<>();
		for (ItemStack i : items) {
			recipeItemsArray.add(i);
		}
		return this.isGeneratorRecipe(g, recipeItemsArray);
	}
}
