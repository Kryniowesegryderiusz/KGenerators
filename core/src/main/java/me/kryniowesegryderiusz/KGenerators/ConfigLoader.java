package me.kryniowesegryderiusz.KGenerators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.kryniowesegryderiusz.KGenerators.Utils.Config;
import me.kryniowesegryderiusz.KGenerators.XSeries.XUtils;

abstract class ConfigLoader {
	
	@SuppressWarnings("unchecked")
	static void loadConfig() {
		Config config = KGenerators.getPluginConfig();
		
		/* Settings */
		if (config.contains("settings.can-generate-instead")) {
			ArrayList<String> tempListString = new ArrayList<String>();
			ArrayList<ItemStack> tempListItemStack = new ArrayList<ItemStack>();
			tempListString = (ArrayList<String>) config.getList("settings.can-generate-instead");
			
			for (String s : tempListString) {
				ItemStack m = XUtils.parseItemStack(s);
				tempListItemStack.add(m);
				System.out.println("[KGenerators] Added " + s + " to generating whitelist.");
			}
			KGenerators.generatingWhitelist = tempListItemStack;
		}
		
		if (config.contains("settings.lang")) {
			KGenerators.lang = config.getString("settings.lang");
		}
		
		/* Generator types loader */
		KGenerators.generators.clear();
    	ConfigurationSection mainSection = config.getConfigurationSection("generators");
    	for(String generatorID: mainSection.getKeys(false)){
    		Boolean error = false;
    		
    		String generatorBlock = config.getString("generators."+generatorID+".generator");
    		int delay = config.getInt("generators."+generatorID+".delay");
    		String name = ChatColor.translateAlternateColorCodes('&', config.getString("generators."+generatorID+".name"));
    		String type = config.getString("generators."+generatorID+".type");
    		
    		if (!type.equals("single") && !type.equals("double")) {
    			System.out.println("[KGenerators] !!! CONFIGURATION ERROR !!! Type of " + generatorID + " is set to " + type + ". It should be single or double!");
    			error = true;
    		}
    		
    		Boolean glow = true;
    		if (config.contains("generators."+generatorID+".glow")) {
    			glow = config.getBoolean("generators."+generatorID+".glow");
    		}

    		ItemStack generatorItem = new ItemStack(XUtils.parseItemStack(generatorBlock)); 
    		ArrayList<String> loreGot = new ArrayList<String>();
    		ArrayList<String> lore = new ArrayList<String>();
    		ItemMeta meta;
    		meta = (ItemMeta) generatorItem.getItemMeta();
    		meta.setDisplayName(name);
    		loreGot = (ArrayList<String>) config.getList("generators."+generatorID+".lore");
    	    for (String l : loreGot) {
    	    	l = ChatColor.translateAlternateColorCodes('&', l);
    	    	lore.add(l);
    	      }
    		meta.setLore(lore);
    		lore.clear();
    		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    		generatorItem.setItemMeta(meta);
    		if(glow){
    			generatorItem.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
    		}
    		
    		int chancesAmount = 0;
    		HashMap<ItemStack, Double> chances = new HashMap<ItemStack, Double>();
    		ConfigurationSection chancesSection = config.getConfigurationSection("generators."+generatorID+".chances");
    		for(String ItemStack: chancesSection.getKeys(false)) {
    			
    			chances.put(XUtils.parseItemStack(ItemStack), config.getDouble("generators."+generatorID+".chances."+ItemStack));
    			chancesAmount++;
    		}
    		
    		Generator generator = new Generator(XUtils.parseItemStack(generatorBlock), generatorItem, delay, type, chances);
    		
    		if (config.contains("generators."+generatorID+".placeholder") && !config.getString("generators."+generatorID+".placeholder").isEmpty()) {
    			String placeholderString = config.getString("generators."+generatorID+".placeholder");	
    			generator.setPlaceholder(XUtils.parseItemStack(placeholderString));
    		}
    		
    		if (config.contains("generators."+generatorID+".generate-immediately-after-place") && !config.getString("generators."+generatorID+".generate-immediately-after-place").isEmpty()) {
    			Boolean bool = config.getBoolean("generators."+generatorID+".generate-immediately-after-place");
    			if (bool) {
    				generator.setAfterPlaceWaitModifier(0);
    			}
    			else 
    			{
    				generator.setAfterPlaceWaitModifier(1);
    			}
    		}
    		
    		Boolean allowPistonPush = false;
    		if (config.contains("generators."+generatorID+".allow-piston-push")) {
    			generator.setPistonPushAllowed(config.getBoolean("generators."+generatorID+".allow-piston-push"));
    		}
    		
    		for (Entry<String, Generator> entry : KGenerators.generators.entrySet()) {
    			if (entry.getValue().getGeneratorItem().equals(generatorItem)) {
    				error = true;
    				System.out.println("[KGenerators] !!! ERROR !!! " + generatorID + " has same generator item as " + entry.getKey());
    			}
    		}
    		
    		if (error) 
    		{
    			System.out.println("[KGenerators] !!! ERROR !!! Couldnt load " + generatorID);
    		}
    		else
    		{
    			KGenerators.generators.put(generatorID, generator);
        		KGenerators.generatorsItemStacks.add(generator.getGeneratorBlock());
        		
        		System.out.println("[KGenerators] Loaded " + type + " "+ generatorID + " generating variety of " + chancesAmount + " blocks every " + delay + " ticks"); 
    		}
    	}
	}
	
	static void loadGenerators(){
		Config file = KGenerators.getPluginGeneratorsFile();
		int amount = 0;
		
		if (!file.contains("placedGenerators")){
			return;
		}
		
    	ConfigurationSection mainSection = file.getConfigurationSection("placedGenerators");
    	for(String generatorLocationString: mainSection.getKeys(false)){
    		
    		String generatorID = file.getString("placedGenerators."+generatorLocationString+".generatorID");
    		
    		//0world 1x 2y 3z
    		String coordinates[] = generatorLocationString.split(",");
    		
    		String world = coordinates[0];    		
    		int x = Integer.parseInt(coordinates[1]);
    		int y = Integer.parseInt(coordinates[2]);
    		int z = Integer.parseInt(coordinates[3]);
    		Location generatorLocation = new Location(KGenerators.getInstance().getServer().getWorld(world), x, y, z);
    		
    		if (generatorID != null){
    			//System.out.println(generatorLocation + " " + generator);
    			KGenerators.generatorsLocations.put(generatorLocation, generatorID);
    			//System.out.println(generatorLocation);
    			amount++;
    		} 	    
    	}
    	System.out.println("[KGenerators] Loaded " + amount + " placed generators");
	}
} 