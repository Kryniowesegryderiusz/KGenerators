package me.kryniowesegryderiusz.KGenerators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import me.kryniowesegryderiusz.KGenerators.Enums.EnumLog;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumPickUpMode;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.PerPlayerGenerators;
import me.kryniowesegryderiusz.KGenerators.Utils.Config;
import me.kryniowesegryderiusz.KGenerators.XSeries.XUtils;

public abstract class Files {
	
	@SuppressWarnings("unchecked")
	static void loadConfig() {
		Config config = Main.getConfigFile();
		
		/* Settings */
		if (config.contains("settings.can-generate-instead")) {
			ArrayList<String> tempListString = new ArrayList<String>();
			ArrayList<ItemStack> tempListItemStack = new ArrayList<ItemStack>();
			tempListString = (ArrayList<String>) config.getList("settings.can-generate-instead");
			
			for (String s : tempListString) {
				ItemStack m = XUtils.parseItemStack(s);
				tempListItemStack.add(m);
				Logger.info("Added " + s + " to generating whitelist.");
			}
			Main.generatingWhitelist = tempListItemStack;
		}
		
		if (config.contains("settings.lang")) {
			Main.lang = config.getString("settings.lang");
		}
		
		if (config.contains("settings.per-player-generators.enabled"))
		{
			Main.overAllPerPlayerGeneratorsEnabled = config.getBoolean("settings.per-player-generators.enabled");
		}
		
		if (config.contains("settings.per-player-generators.overall-place-limit"))
		{
			Main.overAllPerPlayerGeneratorsPlaceLimit = config.getInt("settings.per-player-generators.overall-place-limit");
		}
		
		if (config.contains("settings.generators-actionbar-messages"))
		{
			Main.generatorsActionbarMessages = config.getBoolean("settings.generators-actionbar-messages");
		}
		
		if (config.contains("settings.restrict-mining-by-permission"))
		{
			Main.restrictMiningByPermission = config.getBoolean("settings.restrict-mining-by-permission");
		}
		
		if (config.contains("settings.enable-world-guard-flags"))
		{
			Main.enableWorldGuardChecks = config.getBoolean("settings.enable-world-guard-flags");
		}
		
		if (config.contains("settings.pick-up.mode"))
		{
			Main.pickUpMode = Enums.getModeByString(config.getString("settings.pick-up.mode"));
		}
		
		if (config.contains("settings.pick-up.item"))
		{
			String item = (config.getString("settings.pick-up.item"));
			if (item.toLowerCase().equals("any"))
			{
				Main.pickUpItem = null;
			}
			else
			{
				Main.pickUpItem = XUtils.parseItemStack(item);
			}
		}
		
		if (config.contains("settings.pick-up.sneak"))
		{
			Main.pickUpSneak = config.getBoolean("settings.pick-up.sneak");
		}
		
		if (config.contains("settings.explosion-handler"))
		{
			Main.explosionHandler = (short) config.getInt("settings.explosion-handler");
		}
		
		/* Generator types loader */
		Main.generators.clear();
    	ConfigurationSection mainSection = config.getConfigurationSection("generators");
    	for(String generatorID: mainSection.getKeys(false)){
    		Boolean error = false;
    		
    		String generatorBlock = config.getString("generators."+generatorID+".generator");
    		int delay = config.getInt("generators."+generatorID+".delay");
    		String name = ChatColor.translateAlternateColorCodes('&', config.getString("generators."+generatorID+".name"));
    		String type = config.getString("generators."+generatorID+".type").toLowerCase();
    		
    		if (!type.equals("single") && !type.equals("double")) {
    			Logger.error("!!! ERROR !!! Type of " + generatorID + " is set to " + type + ". It should be single or double!");
    			error = true;
    		}
    		
    		Boolean glow = true;
    		if (config.contains("generators."+generatorID+".glow")) {
    			glow = config.getBoolean("generators."+generatorID+".glow");
    		}

    		ItemStack generatorItem = XUtils.parseItemStack(generatorBlock); 
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
    		
    		Generator generator = new Generator(generatorID, XUtils.parseItemStack(generatorBlock), generatorItem, delay, type, chances);
    		
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
    		
    		if (config.contains("generators."+generatorID+".per-player-generators.place-limit"))
    		{
    			generator.setPlaceLimit(config.getInt("generators."+generatorID+".per-player-generators.place-limit"));
    		}
    		
    		if (config.contains("generators."+generatorID+".per-player-generators.only-owner-pickup"))
    		{
    			generator.setOnlyOwnerPickUp(config.getBoolean("generators."+generatorID+".per-player-generators.only-owner-pickup"));
    		}
    		
    		if (config.contains("generators."+generatorID+".per-player-generators.only-owner-use"))
    		{
    			generator.setOnlyOwnerUse(config.getBoolean("generators."+generatorID+".per-player-generators.only-owner-use"));
    		}
    		
    		for (Entry<String, Generator> entry : Main.generators.entrySet()) {
    			if (entry.getValue().getGeneratorItem().equals(generatorItem)) {
    				error = true;
    				Logger.error(generatorID + " has same generator item as " + entry.getKey());
    			}
    		}
    		
    		if (error) 
    		{
    			Logger.error("Couldnt load " + generatorID);
    		}
    		else
    		{
    			Main.generators.put(generatorID, generator);
        		Main.generatorsItemStacks.add(generator.getGeneratorBlock());
        		
        		Logger.info("Loaded " + type + " "+ generatorID + " generating variety of " + chancesAmount + " blocks every " + delay + " ticks");
    		}
    	}
	}
	
	static void loadGenerators() {
		Config file = Main.getGeneratorsFile();
		int amount = 0;
				
		if (!file.contains("placedGenerators")){
			return;
		}
		
		ArrayList<String> errWorlds = new ArrayList<String>();
		
    	ConfigurationSection mainSection = file.getConfigurationSection("placedGenerators");
    	for(String generatorLocationString: mainSection.getKeys(false)){
    		
    		String generatorID = file.getString("placedGenerators."+generatorLocationString+".generatorID");
    		
    		Player owner;
    		
    		if (file.contains("placedGenerators."+generatorLocationString+".owner"))
    		{
    			owner = Bukkit.getPlayer(file.getString("placedGenerators."+generatorLocationString+".owner"));
    		}
    		else
    		{
    			owner = null;
    		}   		
    		
    		//0world 1x 2y 3z
    		String coordinates[] = generatorLocationString.split(",");
    		
    		String world = coordinates[0];    		
    		int x = Integer.parseInt(coordinates[1]);
    		int y = Integer.parseInt(coordinates[2]);
    		int z = Integer.parseInt(coordinates[3]);
    		
    		World bukkitWorld = Main.getInstance().getServer().getWorld(world);
    		
    		if (bukkitWorld == null)
    		{
    			if (!errWorlds.contains(world))
    			{
    				errWorlds.add(world);
    			}
    		}
    		
    		Location generatorLocation = new Location(bukkitWorld, x, y, z);
    		
    		if (generatorID != null){
    			Main.generatorsLocations.put(generatorLocation, new GeneratorLocation(generatorID, owner));
    			PerPlayerGenerators.addGeneratorToPlayer(owner, generatorID);
    			amount++;
    		} 	    
    	}
    	
    	if (!errWorlds.isEmpty())
    	{
    		Logger.error("An error occured, while loading some placed generators!");
    		Logger.error("Cant load worlds: " + errWorlds.toString());
    		Logger.error("Possible worlds: " + Bukkit.getWorlds().toString());
    	}
    	
    	Logger.info("Loaded " + amount + " placed generators");
	}
	
	public static void saveGeneratorToFile(Location location, Player player, String generatorID)
	{
		Config file = Main.getGeneratorsFile();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world  = location.getWorld().getName();
		file.set("placedGenerators."+world+","+x+","+y+","+z+".generatorID", generatorID);
		file.set("placedGenerators."+world+","+x+","+y+","+z+".owner", player.getName());
		
		try {
			file.saveConfig();
		} catch (IOException e) {
			Logger.error(e);
		}
		
	}
	
	public static void removeGeneratorFromFile(Location location)
	{
		Config file = Main.getGeneratorsFile();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world  = location.getWorld().getName();
		
		file.set("placedGenerators."+world+","+x+","+y+","+z+".generatorID", null);
		file.set("placedGenerators."+world+","+x+","+y+","+z+".owner", null);
		file.set("placedGenerators."+world+","+x+","+y+","+z, null);
		
		try {
			file.saveConfig();
		} catch (IOException e) {
			Logger.error(e);
		}
	}
} 