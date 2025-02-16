package me.kryniowesegryderiusz.kgenerators.multiversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.ChatUtils;
import net.md_5.bungee.api.ChatColor;

public class ChatUtils_1_16 implements ChatUtils {
	
    private static final Pattern HEX_PATTERN = Pattern.compile("#[A-Fa-f0-9]{6}");
	
	public String colorize(String message) {
		
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group();
            matcher.appendReplacement(sb, ChatColor.of(hex) + "");
        }
        matcher.appendTail(sb);

        return ChatColor.translateAlternateColorCodes('&', sb.toString());

	}
}
