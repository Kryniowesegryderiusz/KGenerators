package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Limits;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;

public class GeneratorPlayer {
	
	@Getter
	private boolean isNone = false;
	
	private String nick;
	
	public GeneratorPlayer(String nick)
	{
		this.nick = nick;
	}
	
	public GeneratorPlayer(String nick, boolean isNone)
	{
		this.nick = nick;
		this.isNone = isNone;
	}
	
	public Player getOnlinePlayer()
	{
		return Bukkit.getPlayer(this.nick);
	}
	
	@SuppressWarnings("deprecation")
	public OfflinePlayer getOfflinePlayer()
	{
		return Bukkit.getOfflinePlayer(this.nick);
	}
	
	public String getName()
	{
		if (!isNone)
			return this.nick;
		else
			return Lang.getMessage(Message.GENERATORS_ANY_OWNER_NONE, false, false);
	}
	
	/*
	 * Per player generators
	 */
	
	private HashMap<Generator, Integer> playersGenerators = new HashMap<Generator, Integer>();
	
	public void addGeneratorToPlayer(Generator generator)
	{
		if (isNone)
			return;
		
		if (playersGenerators.containsKey(generator))
		{
			int nr = playersGenerators.get(generator);
			nr ++;
			playersGenerators.put(generator, nr);
		}
		else
		{
			playersGenerators.put(generator, 1);
		}
	}
	
	public void removeGeneratorFromPlayer(Generator generator)
	{
		if (isNone)
			return;
		
		if (playersGenerators.containsKey(generator))
		{
			int nr = playersGenerators.get(generator);
			nr--;
			playersGenerators.put(generator, nr);
		}
	}
	
	public Integer getAllGeneratorsCount()
	{
		if (isNone)
			return 0;
		
		int nr = 0;
		for (Entry<Generator, Integer> e : playersGenerators.entrySet())
		{
			nr = nr + e.getValue();
		}
		return nr;
	}
	
	public Integer getGeneratorsCount(Generator generator)
	{
		if (isNone)
			return 0;
		
		if (playersGenerators.containsKey(generator))
			return playersGenerators.get(generator);
		else
			return 0;
	}
	
	
	/**
	 * Online player only
	 * @param player
	 * @param generatorId
	 * @return
	 */
	public Boolean canPlace(GeneratorLocation gLoc)
	{	
		if (!Main.getSettings().isLimits())
			return true;
		
		PlayerLimits pl = new PlayerLimits(this.getOnlinePlayer());
		for (Limit limit : Limits.getValues())
		{
			if (!limit.fulfillsPlaceLimit(this, gLoc, pl))
				return false;
		}
		return true;
	}
	
	/**
	 * Online player only
	 * @param player
	 * @param gLocation
	 * @return
	 */
	public Boolean canPickUp(GeneratorLocation gLoc)
	{
		if (!Main.getSettings().isLimits())
			return true;
		
		for (Limit limit : Limits.getValues())
		{
			if (!limit.fulfillsOnlyOwnerPickUp(this, gLoc))
				return false;
		}
		return true;
	}

	public Boolean canUse(GeneratorLocation gLoc){
		if (!Main.getSettings().isLimits())
			return true;
		
		for (Limit limit : Limits.getValues())
		{
			if (!limit.fulfillsOnlyOwnerUse(this, gLoc))
				return false;
		}
		return true;
	}
}
