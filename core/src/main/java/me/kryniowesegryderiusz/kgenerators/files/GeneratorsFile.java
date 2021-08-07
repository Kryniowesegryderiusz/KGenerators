package me.kryniowesegryderiusz.kgenerators.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class GeneratorsFile {
	
	@SuppressWarnings("unchecked")
	public static void load()
	{
		Config config;

    	try {
    		config = ConfigManager.getConfig("generators.yml", (String) null, true, false);
			config.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error("Generators file: Cant load generators config. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
		
		Generators.clear();
		
    	ConfigurationSection mainSection = config.getConfigurationSection("");
    	for(String generatorID: mainSection.getKeys(false)){
    		
    		if (!generatorID.equals("example_generator"))
    		{
	    		Boolean error = false;
	    		
	    		String generatorBlock = config.getString(generatorID+".generator");
	    		int delay = config.getInt(generatorID+".delay");
	    		String name = Main.getChatUtils().colorize(config.getString(generatorID+".name"));
	    		String type = config.getString(generatorID+".type").toLowerCase();
	    		
	    		if (!type.equals("single") && !type.equals("double")) {
	    			Logger.error("Generators file: Type of " + generatorID + " is set to " + type + ". It should be single or double!");
	    			error = true;
	    		}
	    		
	    		Boolean glow = true;
	    		if (config.contains(generatorID+".glow")) {
	    			glow = config.getBoolean(generatorID+".glow");
	    		}
	
	    		ItemStack generatorItem = XUtils.parseItemStack(generatorBlock, "Generators file", true); 
	    		ArrayList<String> loreGot = new ArrayList<String>();
	    		ArrayList<String> lore = new ArrayList<String>();
	    		ItemMeta meta;
	    		meta = (ItemMeta) generatorItem.getItemMeta();
	    		meta.setDisplayName(name);
	    		loreGot = (ArrayList<String>) config.getList(generatorID+".lore");
	    	    for (String l : loreGot) {
	    	    	l = Main.getChatUtils().colorize(l);
	    	    	lore.add(l);
	    	      }
	    		meta.setLore(lore);
	    		lore.clear();
	    		if (glow)
	    			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
	    		generatorItem.setItemMeta(meta);
	    		if (glow)
	    			generatorItem.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
	    		
	    		int chancesAmount = 0;
	    		LinkedHashMap<ItemStack, Double> chances = new LinkedHashMap<ItemStack, Double>();
	    		ConfigurationSection chancesSection = config.getConfigurationSection(generatorID+".chances");
	    		for(String itemStack: chancesSection.getKeys(false)) {
	    			
	    			ItemStack item = XUtils.parseItemStack(itemStack, "Generators file", true);
	    			if (item.getType().isBlock())
	    			{
	    				chances.put(item, config.getDouble(generatorID+".chances."+itemStack));
	    			}
	    			else
	    			{
	    				error = true;
	    				Logger.error("Generators file: Type of " + generatorID + " has " + itemStack + " set in chances, which is not a block!");
	    			}
	    			chancesAmount++;
	    		}
	    		
	    		Generator generator = new Generator(generatorID, XUtils.parseItemStack(generatorBlock, "Generators file", true), generatorItem, delay, GeneratorType.Functions.getGeneratorTypeByString(type), chances);
	    		
	    		if (config.contains(generatorID+".placeholder") && !config.getString(generatorID+".placeholder").isEmpty()) {
	    			String placeholderString = config.getString(generatorID+".placeholder");	
	    			generator.setPlaceholder(XUtils.parseItemStack(placeholderString, "Generators file", true));
	    			if (generator.getChances().containsKey(generator.getPlaceholder()))
	    				Logger.warn("Generators file: Generator " + generatorID + " has placeholder block set same as one of generated blocks! Its not the best idea, as it'd look confusing! Consider changing it!");
	    		}
	    		
	    		if (config.contains(generatorID+".generate-immediately-after-place") && !config.getString(generatorID+".generate-immediately-after-place").isEmpty()) {
	    			Boolean bool = config.getBoolean(generatorID+".generate-immediately-after-place");
	    			if (bool) {
	    				generator.setAfterPlaceWaitModifier(0);
	    			}
	    			else 
	    			{
	    				generator.setAfterPlaceWaitModifier(1);
	    			}
	    		}
	    		
	    		if (config.contains(generatorID+".allow-piston-push")) {
	    			generator.setAllowPistonPush(config.getBoolean(generatorID+".allow-piston-push"));
	    		}
	    		
	    		if (config.contains(generatorID+".hologram"))
	    		{
	    			generator.setHologram(config.getBoolean(generatorID+".hologram"));
	    		}
	    		
	    		String doubledGeneratorId = Generators.exactGeneratorItemExists(generatorID, generator.getGeneratorItem());
	    		if (doubledGeneratorId != null)
	    			{
						Logger.error("Generators file: " + generatorID + " has same generator item as " + doubledGeneratorId);
	    				error = true;
	    			}
	    		
	    		if (Generators.exists(generatorID))
					{
	    				Logger.error("Generators file: generatorID of " + generatorID + "is duplicated!");
						error = true;
					}
	    		
	    		if (error) 
	    		{
	    			Logger.error("Generators file: Couldnt load " + generatorID);
	    		}
	    		else
	    		{
	    			Generators.add(generatorID, generator);        		
	        		Logger.info("Generators file: Loaded " + type + " "+ generatorID + " generating variety of " + chancesAmount + " blocks every " + delay + " ticks");
	    		}
    		}
    	}
	}

}
