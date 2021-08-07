package me.kryniowesegryderiusz.kgenerators.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
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
	
	public static void reload()
	{
		for (Entry<Location, GeneratorLocation> e : Locations.getEntrySet())
		{
			e.getValue().getOwner().addGeneratorToPlayer(e.getValue().getGenerator());
		}
		for (GeneratorPlayer gp : players.values())
		{
			ArrayList<Generator> toRemove = new ArrayList<Generator>();
			for (Generator g : gp.getPlayersGenerators().keySet())
			{
				if (!Generators.exists(g.getId()))
					toRemove.add(g);
			}
			for (Generator g : toRemove)
			{
				gp.getPlayersGenerators().remove(g);
			}
		}
	}

}
