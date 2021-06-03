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

import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.enums.HologramText;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.classes.MenuInventory;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class Lang {

	private static LinkedHashMap<String, String> lang = new LinkedHashMap<String, String>();
	
	private static HashMap<String, ArrayList<String>> holograms = new HashMap<String, ArrayList<String>>();
	
	private static HashMap<MenuItemType, MenuItem> menuItems = new HashMap<MenuItemType, MenuItem>();
	private static HashMap<MenuInventoryType, MenuInventory> menuInventories = new HashMap<MenuInventoryType, MenuInventory>();
	private static HashMap<String, ArrayList<String>> menuItemsAdditionalLines = new HashMap<String, ArrayList<String>>();
	
	private static LinkedHashMap<String, String> replecables = new LinkedHashMap<String, String>();

	public static void loadMessages(Config config, Config guiConfig) throws IOException {

		addDefaults();
		registerMessages(Message.class);
		
		/*
		 * Regular lang
		 */
		
		for(Map.Entry<String, String> entry : lang.entrySet())
		{
			
			String mpath = entry.getKey();
			String fpath = "messages." + mpath;
			
			if (!config.contains(fpath)) {
				config.set(fpath, entry.getValue());
			}
			
			lang.put(mpath, ChatColor.translateAlternateColorCodes('&', config.getString(fpath)));
			
			try {
				config.saveConfig();
			} catch (IOException e) {
				Logger.error("Lang: Cant save lang file!");
				Logger.error(e);
			}
		}
		
		/*
		 * Holograms
		 */
		
		for (Entry<String, ArrayList<String>> e : holograms.entrySet())
    	{
			
			String path = "holograms." + e.getKey();
			
			if (!config.contains(path)) {
				config.set(path, e.getValue());
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
		for (Entry<MenuInventoryType, MenuInventory> e : menuInventories.entrySet())
    	{
			e.getValue().load(e.getKey(), guiConfig);
    	}
		for (Entry<MenuItemType, MenuItem> e : menuItems.entrySet())
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
	public static <T extends Enum<T> & IMessage> void sendMessage (CommandSender sender, T m, boolean forceChat, boolean prefix){
    	String key = m.getKey();
    	String message = lang.get(key);
    	if (message != null && message.length() != 0){
	        for (Map.Entry part : replecables.entrySet()) {
	        	String partKey = (String) part.getKey();
	        	String partValue = (String) part.getValue();
	        	message = message.replace(partKey, partValue);
	        }
	        
	        if (prefix)
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
    
    public static <T extends Enum<T> & IMessage> void sendMessage (CommandSender sender, T m){
    	sendMessage(sender, m, false, true);
    }
    
	@SuppressWarnings("rawtypes")
	public static <T extends Enum<T> & IMessage> String getMessage (T m, boolean prefix, boolean cleanReplecables){
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
	
	public static <T extends Enum<T> & IMessage> String getMessage (T m){
		return getMessage(m, false, true);
	}
	
	public static ArrayList<String> getHologram(HologramText h)
	{
		return holograms.get(h.getKey());
	}
	
	public static ArrayList<String> getMenuItemAdditionalLines(MenuItemAdditionalLines a)
	{
		return menuItemsAdditionalLines.get(a.getKey());
	}
	
	public static MenuItem getMenuItem(MenuItemType m)
	{
		return menuItems.get(m).clone();
	}
	
	public static MenuInventory getMenuInventory(MenuInventoryType m)
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
    	
    	holograms.clear();
    	for (HologramText e : HologramText.values())
    	{
    		holograms.put(e.getKey(), e.getStringContent().getLines());
    	}
    	
    	menuItems.clear();
    	for (MenuItemType g : MenuItemType.values()) {
    		menuItems.put(g, g.getMenuItem());
    	}
    	
    	menuInventories.clear();
    	for (MenuInventoryType g : MenuInventoryType.values()) {
    		menuInventories.put(g, g.getMenuInventory());
    	}
    	
    	menuItemsAdditionalLines.clear();
    	for (MenuItemAdditionalLines e : MenuItemAdditionalLines.values())
    	{
    		menuItemsAdditionalLines.put(e.getKey(), e.getStringContent().getLines());
    	}
    }
    
    public static <T extends Enum<T> & IMessage> void registerMessages(Class<T> c)
    {
        for(IMessage im : c.getEnumConstants())
        {
        	lang.put(im.getKey(), im.getMessage());
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
