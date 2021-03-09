package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
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
			return Lang.getMessage(EnumMessage.GeneratorsPPGOwnerNone, false, false);
	}
	
	/*
	 * Per player generators
	 */
	
	private HashMap<String, Integer> playersGenerators = new HashMap<String, Integer>();
	
	public void addGeneratorToPlayer(String generatorId)
	{
		if (isNone)
			return;
		
		if (playersGenerators.containsKey(generatorId))
		{
			int nr = playersGenerators.get(generatorId);
			nr ++;
			playersGenerators.put(generatorId, nr);
		}
		else
		{
			playersGenerators.put(generatorId, 1);
		}
	}
	
	public void removeGeneratorFromPlayer(String generatorId)
	{
		if (isNone)
			return;
		
		if (playersGenerators.containsKey(generatorId))
		{
			int nr = playersGenerators.get(generatorId);
			nr--;
			playersGenerators.put(generatorId, nr);
		}
	}
	
	public Integer getAllGeneratorsCount()
	{
		if (isNone)
			return 0;
		
		int nr = 0;
		for (Entry<String, Integer> e : playersGenerators.entrySet())
		{
			nr = nr + e.getValue();
		}
		return nr;
	}
	
	public Integer getGeneratorsCount(String generatorId)
	{
		if (isNone)
			return 0;
		
		if (playersGenerators.containsKey(generatorId))
			return playersGenerators.get(generatorId);
		else
			return 0;
	}
	
	
	/**
	 * Online player only
	 * @param player
	 * @param generatorId
	 * @return
	 */
	public Boolean canPlace(String generatorId)
	{		
		if (Main.getSettings().isPerPlayerGenerators())
		{
			if (this.isNone)
				return true;
			
			Player player = getOnlinePlayer();
			PlayerLimits pLimits = new PlayerLimits(player);
			int globalLimit = pLimits.getGlobalLimit();
			int generatorLimit = pLimits.getLimit(generatorId);
						
			/* Checking global limit */
			if (globalLimit >= 0)
			{
				if (getAllGeneratorsCount() >= globalLimit)
				{
					Lang.addReplecable("<number>", String.valueOf(globalLimit));
					Lang.sendMessage(player, EnumMessage.GeneratorsPPGCantPlaceMore);
					return false;
				}
			}
			
			/* Checking per generator limit */
			if (generatorLimit >= 0)
			{
				if (getGeneratorsCount(generatorId) >= generatorLimit)
				{
					Lang.addReplecable("<number>", String.valueOf(generatorLimit));
					Lang.addReplecable("<generator>", Generators.get(generatorId).getGeneratorItem().getItemMeta().getDisplayName());
					Lang.sendMessage(player, EnumMessage.GeneratorsPPGCantPlaceMoreType);
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Online player only
	 * @param player
	 * @param gLocation
	 * @return
	 */
	public Boolean canPickUp(GeneratorLocation gLocation)
	{
		return ownerCheck(gLocation, EnumMessage.GeneratorsPPGCantPickUp);
	}

	public Boolean canUse(GeneratorLocation gLocation){
		return ownerCheck(gLocation, EnumMessage.GeneratorsPPGCantUse);
	}
	
	private boolean ownerCheck(GeneratorLocation gLocation, EnumMessage message)
	{
		if (Main.getSettings().isPerPlayerGenerators())
		{
			Player player = getOnlinePlayer();
			
			if (player.hasPermission("kgenerators.bypass.onlyownerchecks"))
				return true;
			
			/* To prevent errors if generator havent any owner */
			if (gLocation.getOwner().isNone())
				return true;
			/* end */
			
			if (gLocation.getGenerator().getOnlyOwnerPickUp())
			{				
				if (this != gLocation.getOwner())
				{
					Lang.addReplecable("<owner>", gLocation.getOwner().getName());
					Lang.sendMessage(player, message);
					return false;
				}
			}
		}
		return true;
	}
}
