package me.kryniowesegryderiusz.kgenerators.lang.storages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IMessage;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class MessageStorage {
	
	private HashMap<String, String> lang = new HashMap<String, String>();
	private Config config;
	
	public MessageStorage(Config config)
	{
		this.config = config;
	}
	
	public <T extends Enum<T> & IMessage> void register(Class<T> c)
	{
		for(T en : c.getEnumConstants())
        {
			checkLine(en.getKey(), en.getMessage());
        }
	}
	
	public void reload()
	{
		for (Entry<String, String> e : lang.entrySet())
		{
			checkLine(e.getKey(), e.getValue());
		}
	}
	
	private void checkLine(String key, String value)
	{
		String keyAdjusted = "messages."+key;
		if (config.contains(keyAdjusted))
		{
			lang.put(key, Main.getMultiVersion().getChatUtils().colorize(config.getString(keyAdjusted)));
		}
		else
		{
			try {
				lang.put(key, value);
				config.set(keyAdjusted, value);;
				config.saveConfig();
			} catch (IOException ex) {
				Logger.error("Lang: Cant save lang file!");
				Logger.error(ex);
			}
		}
	}
	
	public <T extends Enum<T> & IMessage> String get(T m, String... replacables){
		return get(m, false, replacables);
	}
	
	public <T extends Enum<T> & IMessage> String get(T m, boolean prefix, String... replacables){
		
    	String message = lang.get(m.getKey());
    	ArrayList<String> reps = new ArrayList<>(Arrays.asList(replacables));
    	
    	if (message != null && message.length() != 0)
    	{
    		
    		for (int i = 0; i < reps.size(); i=i+2)
    		{
    			message = message.replace(reps.get(i), reps.get(i+1));
    		}
    		
	        if (prefix)
	        	message = lang.get(Message.PREFIX.getKey()) + message;
    		
    		return message;
    	}
		return null;
    }
	
    public <T extends Enum<T> & IMessage> void send(CommandSender sender, T m, String... replacables){
    	send(sender, m, false, true, replacables);
    }
	
	public <T extends Enum<T> & IMessage> void send(CommandSender sender, T m, boolean forceChat, boolean prefix, String... replacables){
		
    	String message = this.get(m, true, replacables);
    	
    	if (message != null)
    	{	        
        	String[] splittedMessage = message.split("\n");
    		for (String s : splittedMessage) {
    			
    			if (m.getKey().split("\\.")[0].contains("generators") && Main.getSettings().isActionbarMessages() && !forceChat)
    			{
    				Main.getMultiVersion().getActionBar().sendActionBar((Player) sender, s);
    			}
    			else
    			{
    				sender.sendMessage(s);
    			}
    		}
    	}
    }
	
    public void sendHelp(CommandSender sender) {
    	
    	sender.sendMessage(lang.get("commands.help.label"));
    	
    	for(Map.Entry<String, String> entry : lang.entrySet()) {
    		String path = entry.getKey();
    		String[] spath = path.split("\\.");
    		
    		if (spath.length == 3 && spath[0].equals("commands") && spath[2].equals("help") && sender.hasPermission("kgenerators." + spath[1])) {
    			sender.sendMessage(lang.get("commands.help.format").replace("<subcommand>", spath[1]).replace("<help>", entry.getValue()));
    		}
    	}
    }
    
    /**
     * Gets command list
     * @param list for subcommands of commands or null if just regular commands
     * @return
     */
    public ArrayList<String> getCommands(CommandSender sender) {
    	
    	ArrayList<String> commands = new ArrayList<String>();
    	
    	for(Map.Entry<String, String> entry : lang.entrySet()) {
    		String path = entry.getKey();
    		String[] spath = path.split("\\.");
    		
    		if (spath.length == 3 && spath[0].equals("commands") && spath[2].equals("help") && sender.hasPermission("kgenerators." + spath[1])) {
    			commands.add(spath[1]);
    		}
    	}
		return commands;
    }
}
