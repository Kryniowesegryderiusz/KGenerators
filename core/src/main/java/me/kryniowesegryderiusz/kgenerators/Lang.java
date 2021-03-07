package me.kryniowesegryderiusz.kgenerators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItemAdditional;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumHologram;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.classes.MenuInventory;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public abstract class Lang {
	
	private static LinkedHashMap<String, String> lang = new LinkedHashMap<String, String>();
	
	private static HashMap<String, ArrayList<String>> holograms = new HashMap<String, ArrayList<String>>();
	
	private static HashMap<EnumMenuItem, MenuItem> menuItems = new HashMap<EnumMenuItem, MenuItem>();
	private static HashMap<EnumMenuInventory, MenuInventory> menuInventories = new HashMap<EnumMenuInventory, MenuInventory>();
	private static HashMap<String, ArrayList<String>> menuItemsAdditionalLines = new HashMap<String, ArrayList<String>>();
	
	private static LinkedHashMap<String, String> replecables = new LinkedHashMap<String, String>();

	public static void loadMessages(Config config, Config guiConfig) throws IOException {

		addDefaults();
		
		/*
		 * Regular lang
		 */
		
		for(Map.Entry<String, String> entry : lang.entrySet()) {
			
			String mpath = entry.getKey();
			String fpath = "messages." + mpath;
			
			if (!config.contains(fpath)) {
				config.set(fpath, entry.getValue());
				config.saveConfig();
			}
			
			lang.put(mpath, ChatColor.translateAlternateColorCodes('&', config.getString(fpath)));
		}
		
		/*
		 * Holograms
		 */
		
		for (Entry<String, ArrayList<String>> e : holograms.entrySet())
    	{
			
			String path = "holograms." + e.getKey();
			
			if (!config.contains(path)) {
				config.set(path, e.getValue());
				config.saveConfig();
			}
			ArrayList<String> gotHolo = (ArrayList<String>) config.getStringList(path);
			ArrayList<String> holo = new ArrayList<String>();
			for (String s : gotHolo)
			{
				holo.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			holograms.put(e.getKey(), holo);
    	}
		
		/*
		 * Guis
		 */
		for (Entry<EnumMenuInventory, MenuInventory> e : menuInventories.entrySet())
    	{
			e.getValue().load(e.getKey(), guiConfig);
    	}
		for (Entry<EnumMenuItem, MenuItem> e : menuItems.entrySet())
    	{
			e.getValue().load(e.getKey(), guiConfig);
    	}
		
		try {
			guiConfig.saveConfig();
		} catch (IOException e) {
			Logger.error("Lang: Cant save gui lang file!");
			Logger.error(e);
		}
		
		for (Entry<String, ArrayList<String>> e : menuItemsAdditionalLines.entrySet())
    	{
			
			String path = "additional-lines." + e.getKey();
			
			if (!guiConfig.contains(path)) {
				guiConfig.set(path, e.getValue());
				guiConfig.saveConfig();
			}
			ArrayList<String> gotLines = (ArrayList<String>) guiConfig.getStringList(path);
			ArrayList<String> lines = new ArrayList<String>();
			for (String s : gotLines)
			{
				lines.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			menuItemsAdditionalLines.put(e.getKey(), lines);
    	}
		
	}
	
    @SuppressWarnings("rawtypes")
	public static void sendMessage (CommandSender sender, EnumMessage m, boolean forceChat){
    	String key = m.getKey();
    	String message = lang.get(key);
    	if (message != null && message.length() != 0){
	        for (Map.Entry part : replecables.entrySet()) {
	        	String partKey = (String) part.getKey();
	        	String partValue = (String) part.getValue();
	        	message = message.replace(partKey, partValue);
	        }
	        
	        message = lang.get("prefix") + message;
	        
        	String[] splittedMessage = message.split("\n");
    		for (String s : splittedMessage) {
    			
    			if (key.split("\\.")[0].contains("generators") && Main.getSettings().isActionbarMessages() && !forceChat)
    			{
    				Main.getActionBar().sendActionBar((Player) sender, s);
    			}
    			else
    			{
    				sender.sendMessage(s);
    			}
    		}
    		
    		replecables.clear();
		}
    }
    
    public static void sendMessage (CommandSender sender, EnumMessage m){
    	sendMessage (sender, m, false);
    }
    
	@SuppressWarnings("rawtypes")
	public static String getMessage (EnumMessage m, boolean prefix, boolean cleanReplecables){
		String message = lang.get(m.getKey());
    	if (message != null && message.length() != 0){
    		
	        for (Map.Entry part : replecables.entrySet()) {
	        	String partKey = (String) part.getKey();
	        	String partValue = (String) part.getValue();
	        	message = message.replace(partKey, partValue);
	        }
    		
	        if (cleanReplecables) replecables.clear();
    		
    		if (prefix) {
    			message = lang.get("prefix") + message;
    		}
    		
    		return message;
    	}
		return null;
    }
	
	public static String getMessage (EnumMessage m){
		return getMessage(m, false, true);
	}
	
	public static ArrayList<String> getHologram(EnumHologram h)
	{
		return holograms.get(h.getKey());
	}
	
	public static ArrayList<String> getMenuItemAdditionalLines(EnumMenuItemAdditional a)
	{
		return menuItemsAdditionalLines.get(a.getKey());
	}
	
	public static MenuItem getMenuItem(EnumMenuItem m)
	{
		return menuItems.get(m).clone();
	}
	
	public static MenuInventory getMenuInventory(EnumMenuInventory m)
	{
		return menuInventories.get(m);
	}
    
    public static void sendHelpMessage (CommandSender sender) {
    	
    	sender.sendMessage(lang.get("commands.help.label"));
    	
    	for(Map.Entry<String, String> entry : lang.entrySet()) {
    		String path = entry.getKey();
    		String[] spath = path.split("\\.");
    		
    		if (spath.length == 3 && spath[0].equals("commands") && spath[2].equals("help") && sender.hasPermission("kgenerators." + spath[1])) {
    			sender.sendMessage(lang.get("commands.help.format").replace("<subcommand>", spath[1]).replace("<help>", entry.getValue()));
    		}
    		
    	}
    }
    
    public static void addReplecable(String key, String value) {
    	replecables.put(key, value);
    }
    
    private static void addDefaults()
    {
    	lang.clear();
    	for (EnumMessage m : EnumMessage.values()) {
    		lang.put(m.getKey(), m.getDefaultMessage());
    	}
    	
    	holograms.clear();
    	for (EnumHologram e : EnumHologram.values())
    	{
    		holograms.put(e.getKey(), e.getStringContent().getLines());
    	}
    	
    	menuItems.clear();
    	for (EnumMenuItem g : EnumMenuItem.values()) {
    		menuItems.put(g, g.getMenuItem());
    	}
    	
    	menuInventories.clear();
    	for (EnumMenuInventory g : EnumMenuInventory.values()) {
    		menuInventories.put(g, g.getMenuInventory());
    	}
    	
    	menuItemsAdditionalLines.clear();
    	for (EnumMenuItemAdditional e : EnumMenuItemAdditional.values())
    	{
    		menuItemsAdditionalLines.put(e.getKey(), e.getStringContent().getLines());
    	}
    }
    
    /*
     * Other
     */
    
    public static String getItemTypeName(ItemStack item)
    {
    	String name = "";
    	
    	if (item.hasItemMeta() && item.getItemMeta().hasLocalizedName())
    		name = item.getItemMeta().getLocalizedName();
    	else
    	{
    		String type = item.getType().toString();
    		type = type.toLowerCase().replaceAll("_", " ");
    		type = type.substring(0, 1).toUpperCase() + type.substring(1);
    		name = type;
    	}
    	return name;
    }
}
