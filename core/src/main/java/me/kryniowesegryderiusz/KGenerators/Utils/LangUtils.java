package me.kryniowesegryderiusz.KGenerators.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import me.kryniowesegryderiusz.KGenerators.KGenerators;

public abstract class LangUtils {
	
	public static LinkedHashMap<String, String> lang = new LinkedHashMap<String, String>();
	public static ArrayList<String> help = new ArrayList<String>();
	private static HashMap<String, String> replecables = new HashMap<String, String>();

	@SuppressWarnings("unchecked")
	public static void loadMessages() throws IOException {
		
		Config config = KGenerators.getPluginMessages();

		addDefaults();
		
		for(Map.Entry<String, String> entry : lang.entrySet()) {
			
			String mpath = entry.getKey();
			String fpath = "messages." + mpath;
			
			if (!config.contains(fpath)) {
				config.set(fpath, entry.getValue());
				config.saveConfig();
				System.out.println("[KGenerators] Added missing " + mpath + " message to " + KGenerators.lang + " lang file.");
			}
			
			lang.put(mpath, ChatColor.translateAlternateColorCodes('&', config.getString(fpath)));
		}
	}
	
    @SuppressWarnings("rawtypes")
	public static void sendMessage (CommandSender sender, String key){
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
    			sender.sendMessage(s);
    		}
    		
    		replecables.clear();
		}
    }
    
	@SuppressWarnings("rawtypes")
	public static String getMessage (String key, boolean prefix){
    	String message = lang.get(key);
    	if (message != null && message.length() != 0){
    		
	        for (Map.Entry part : replecables.entrySet()) {
	        	String partKey = (String) part.getKey();
	        	String partValue = (String) part.getValue();
	        	message = message.replace(partKey, partValue);
	        }
    		replecables.clear();
    		
    		if (prefix) {
    			message = lang.get("prefix") + message;
    		}
    		
    		return message;
    	}
		return null;
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
    	
    	lang.put("prefix", "&8[&6KGenerators&8] ");
    	
    	lang.put("generators.cant-break", "&cYou should sneak to pick up this generator!");
    	lang.put("generators.cant-pick-up", "&cYou cant pick up generator here!");
    	lang.put("generators.cant-craft", "&cYou cant craft anything from generator!");
    	lang.put("generators.picked-up", "&e<generator> &apicked up");
    	lang.put("generators.no-craft-permission", "&cYou dont have permission &8(&7<permission>&8)&c to craft <generator>&c!");
    	
    	lang.put("commands.any.wrong", "&cWrong command! Type /kgenerators for help");
    	lang.put("commands.any.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to use KGenerators commands");
    	
    	lang.put("commands.list.listing", "&aGenerators possible: &e");
    	lang.put("commands.list.separator", "&a, &e");
    	lang.put("commands.list.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to list generators");
    	lang.put("commands.list.help", "List all generators");
    	
    	lang.put("commands.give.player-not-online", "&cPlayer is not online");
    	lang.put("commands.give.generator-doesnt-exist", "&cThat generator doesn't exist");
    	lang.put("commands.give.generator-given", "&aGenerator <generator> was given to <player>");
    	lang.put("commands.give.generator-recieved", "&aGenerator <generator> recieved!");
    	lang.put("commands.give.usage", "&cUsage: &e/kgenerators give <player> <generator>");
    	lang.put("commands.give.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to give generators");
    	lang.put("commands.give.help", "Give generator to player");
    	
    	lang.put("commands.getall.recieved", "&aGenerators recieved");
    	lang.put("commands.getall.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to get all generators");
    	lang.put("commands.getall.help", "Get all generators");
    	
    	lang.put("commands.reload.done", "&aConfig and messages reloaded");
    	lang.put("commands.reload.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to reload config");
    	lang.put("commands.reload.help", "Reload config and messages");
    	
    	lang.put("commands.help.label", "&6&lKGenerators &aCommands help:");
    	lang.put("commands.help.format", "&e/kgenerators <subcommand> &7<help>");

    }
}
