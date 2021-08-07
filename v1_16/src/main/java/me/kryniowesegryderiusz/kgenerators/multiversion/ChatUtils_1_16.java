package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils_1_16 implements ChatUtils {
	
	public String colorize(String message) {
		
        Matcher matcher = Pattern.compile("#[A-Fa-f0-9]{6}").matcher(message);
        int hexAmount = 0;
        while (matcher.find()) {
            matcher.region(matcher.end() - 1, message.length());
            hexAmount++;
        }
        int startIndex = 0;
        for (int hexIndex = 0; hexIndex < hexAmount; hexIndex++) {
            int msgIndex = message.indexOf("#", startIndex);
            String hex = message.substring(msgIndex, msgIndex + 7);
            startIndex = msgIndex + 1;
            message = message.replace(hex, ChatColor.of(hex) + "");
        }
    	
    	message = ChatColor.translateAlternateColorCodes('&', message);
        
		return message;
	}
}
