package me.kryniowesegryderiusz.KGenerators.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.KGenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.KGenerators.Main;

public abstract class LangUtils {
	
	public static LinkedHashMap<String, String> lang = new LinkedHashMap<String, String>();
	public static ArrayList<String> help = new ArrayList<String>();
	private static HashMap<String, String> replecables = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	public static void loadMessages() throws IOException {
		
		Config config = Main.getPluginMessages();

		addDefaults();
		
		for(Map.Entry<String, String> entry : lang.entrySet()) {
			
			String mpath = entry.getKey();
			String fpath = "messages." + mpath;
			
			if (!config.contains(fpath)) {
				config.set(fpath, entry.getValue());
				config.saveConfig();
				System.out.println("[KGenerators] Added missing " + mpath + " message to " + Main.lang + " lang file.");
			}
			
			lang.put(mpath, ChatColor.translateAlternateColorCodes('&', config.getString(fpath)));
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
    			
    			if (key.split("\\.")[0].contains("generators") && Main.generatorsActionbarMessages && !forceChat)
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
    }
}
