package me.kryniowesegryderiusz.kgenerators.multiversion;

import org.bukkit.ChatColor;

import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.ChatUtils;

public class ChatUtils_1_8 implements ChatUtils {
	
	public String colorize(String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);
    	return message;
	}
}
