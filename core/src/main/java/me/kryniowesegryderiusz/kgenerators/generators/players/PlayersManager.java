package me.kryniowesegryderiusz.kgenerators.generators.players;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;

public class PlayersManager {
	
	private GeneratorPlayer nullPlayer = new GeneratorPlayer(null, true);
	
	private HashMap<String, GeneratorPlayer> players = new HashMap<String, GeneratorPlayer>();
	
	public GeneratorPlayer getPlayer(String nick)
	{
		if (nick == null)
			return nullPlayer;
		
		players.putIfAbsent(nick, new GeneratorPlayer(nick));
		
		return players.get(nick);
			
	}
	
	public GeneratorPlayer getPlayer(Player p)
	{
		return getPlayer(p.getName());
	}
	
	public void createPlayer(String nick)
	{
		if (nick != null && !players.containsKey(nick))
			players.put(nick, new GeneratorPlayer(nick));
	}

	public void clear() {
		players.clear();
	}
	
	public void reload()
	{
		for (Entry<Location, GeneratorLocation> e : Main.getLocations().getEntrySet())
		{
			e.getValue().getOwner().addGeneratorToPlayer(e.getValue().getGenerator());
		}
		for (GeneratorPlayer gp : players.values())
		{
			ArrayList<Generator> toRemove = new ArrayList<Generator>();
			for (Generator g : gp.getPlayersGenerators().keySet())
			{
				if (!Main.getGenerators().exists(g.getId()))
					toRemove.add(g);
			}
			for (Generator g : toRemove)
			{
				gp.getPlayersGenerators().remove(g);
			}
		}
	}

}
