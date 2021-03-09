package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.HashMap;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;

public class Players {
	
	private static GeneratorPlayer nullPlayer = new GeneratorPlayer(null, true);
	
	private static HashMap<String, GeneratorPlayer> players = new HashMap<String, GeneratorPlayer>();
	
	public static GeneratorPlayer getPlayer(String nick)
	{
		if (nick == null)
			return nullPlayer;
		
		if (!players.containsKey(nick))
			players.put(nick, new GeneratorPlayer(nick));
		
		return players.get(nick);
			
	}
	
	public static GeneratorPlayer getPlayer(Player p)
	{
		return getPlayer(p.getName());
	}
	
	public static void createPlayer(String nick)
	{
		if (nick != null && !players.containsKey(nick))
			players.put(nick, new GeneratorPlayer(nick));
	}

	public static void clear() {
		players.clear();
		
	}

}
