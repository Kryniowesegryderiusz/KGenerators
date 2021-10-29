package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Players;

public class PlacedGeneratorsLoader {
	
	int amount = 0;
	int notLoaded = 0;
	ArrayList<String> errNotSetId = new ArrayList<String>();
	ArrayList<String> errNotExist = new ArrayList<String>();
	ArrayList<String> errWorlds = new ArrayList<String>();
	
	public PlacedGeneratorsLoader()
	{
		
	}
	
	/**
	 * 
	 * @param generatorId
	 * @param location ("world,x,y,z")
	 * @param nick of owner or blank
	 */
	public void loadNext(String generatorId, String location, String nick)
	{
		boolean err = false;
		
		if (nick != null && !nick.isEmpty())
			Players.createPlayer(nick);
		
		GeneratorPlayer gPlayer = Players.getPlayer(nick);
		
		String world = location.split(",")[0];    		
		if (Main.getInstance().getServer().getWorld(world) == null)
		{
			if (!errWorlds.contains(world))
				errWorlds.add(world);
			err = true;
		}
		Location generatorLocation = Locations.stringToLocation(location);
		
		if (generatorId != null){
			if (!Generators.exists(generatorId))
			{
				if (!errNotExist.contains(generatorId))
					errNotExist.add(generatorId);
				err = true;	
			}
		}
		else
		{
			if (!errNotSetId.contains(location))
				errNotSetId.add(location);
			err = true;
		}
		
		if (!err)
		{
			Locations.add(generatorId, generatorLocation, gPlayer);
			gPlayer.addGeneratorToPlayer(Generators.get(generatorId));
			amount++;
		}
		else
		{
			notLoaded++;
		}
	}
	
	public void finish()
	{
		if (notLoaded != 0)
		{
			Logger.error("Database: An error occured, while loading " + String.valueOf(notLoaded) + " placed generators!");
			if (!errWorlds.isEmpty())
	    	{
	    		Logger.error("Database: Cant load worlds: " + errWorlds.toString());
	    		String possible = "";
	    		for (World world : Bukkit.getWorlds())
	    			possible += world.getName()+", ";
	    		Logger.error("Database: Possible worlds: " + possible);
	    	}
			if (!errNotExist.isEmpty())
	    	{
	    		Logger.error("Database: There are no generators with ids: " + errNotExist.toString());
	    		String possible = "";
	    		for (Entry<String, Generator> e : Generators.getEntrySet())
	    			possible += e.getKey()+", ";
	    		Logger.error("Database: Possible ids: " + possible);
	    	}
			if (!errNotSetId.isEmpty())
	    	{
	    		Logger.error("Database: Generator id is not set for: " + errNotSetId.toString());
	    	}
		}
		Logger.info("Database: Loaded properly " + amount + " placed generators");
	}
}
