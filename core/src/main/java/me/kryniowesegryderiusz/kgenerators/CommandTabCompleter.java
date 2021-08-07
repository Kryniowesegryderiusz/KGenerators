package me.kryniowesegryderiusz.kgenerators;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;

public class CommandTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> c = new ArrayList<String>();

		if (args.length == 3)
		{
			if (args[0].equals("give"))
			{
				return this.getGenerators(args[2]);
			}
		}
		
		if (args.length == 2)
		{
			if (args[0].equals("give"))
			{
				return this.getPlayers(args[1]);
			}
		}
		
		if (args.length == 1)
		{
			ArrayList<String> help = Lang.getCommands(sender);
			if (isBlank(args[0]))
				c = help;
			else
				for (String s : help)
				{
					if (s.contains(args[0]))
						c.add(s);
				}
			return c;
		}
		
		return c;
	}
	
	ArrayList<String> getPlayers(String arg)
	{
		ArrayList<String> players = new ArrayList<String>();
		if (isBlank(arg))
		{
			for (Player p : Bukkit.getOnlinePlayers())
			{
				players.add(p.getName());
			}
		}
		else
		{
			for (Player p : Bukkit.getOnlinePlayers())
			{
				if (p.getName().contains(arg))
					players.add(p.getName());
			}
		}
		return players;
	}
	
	ArrayList<String> getGenerators(String arg)
	{
		ArrayList<String> generators = new ArrayList<String>();
		if (isBlank(arg))
		{
			for (Generator g : Generators.getAll())
			{
				generators.add(g.getId());
			}
		}
		else
		{
			for (Generator g : Generators.getAll())
			{
				if (g.getId().contains(arg))
					generators.add(g.getId());
			}
		}
		return generators;
	}
	
	boolean isBlank(String s)
	{
		if (s.isEmpty() || s.equals(" "))
			return true;
		return false;
	}
}
