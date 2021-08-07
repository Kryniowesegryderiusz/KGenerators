package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils_1_16 implements ChatUtils {
	
	private static final Pattern pattern = Pattern.compile("(?<!\\\\)(#[a-fA-F0-9]{6})");

	public String colorize(String message) {
    	message = ChatColor.translateAlternateColorCodes('&', message);
    	
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, "" + ChatColor.of(color));
        }
    	
		return message;
	}
}
