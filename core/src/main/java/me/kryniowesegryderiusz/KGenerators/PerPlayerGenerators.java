package me.kryniowesegryderiusz.KGenerators;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.Classes.PlayerLimits;
import me.kryniowesegryderiusz.KGenerators.EnumsManager.Message;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;

public abstract class PerPlayerGenerators {
	
	private static HashMap<Player, HashMap<String, Integer>> playersGenerators = new HashMap<Player, HashMap<String, Integer>>();
	
	public static void addGeneratorToPlayer(Player player, String generatorId)
	{
		HashMap<String, Integer> playerGenerators = new HashMap<String, Integer>();
		if (playersGenerators.containsKey(player))
		{
			playerGenerators = playersGenerators.get(player);
			if (playerGenerators.containsKey(generatorId)) {
				int nr = playerGenerators.get(generatorId);
				nr ++;
				playerGenerators.put(generatorId, nr);
			}
			else
			{
				playerGenerators.put(generatorId, 1);
			}
		}
		else
		{
			
			playerGenerators.put(generatorId, 1);
		}
		playersGenerators.put(player, playerGenerators);
	}
	
	public static void removeGeneratorFromPlayer(Player player, String generatorId)
	{
		if (playersGenerators.containsKey(player))
		{
			HashMap<String, Integer> playerGenerators = new HashMap<String, Integer>();
			playerGenerators = playersGenerators.get(player);
			if (playerGenerators.containsKey(generatorId))
			{
				int nr = playerGenerators.get(generatorId);
				nr--;
				playerGenerators.put(generatorId, nr);
				playersGenerators.put(player, playerGenerators);
			}
			else
			{
				//TODO log and message
			}
		}
		else
		{
			//TODO log and message
		}
	}
	
	static Integer getPlayerOverallGeneratorsCount(Player player)
	{
		if (playersGenerators.containsKey(player))
		{
			HashMap<String, Integer> playerGenerators = new HashMap<String, Integer>();
			playerGenerators = playersGenerators.get(player);
			
			if(playerGenerators == null)
			{
				return 0;
			}
			else
			{
				int nr = 0;
				for (Entry<String, Integer> e : playerGenerators.entrySet())
				{
					nr = nr + e.getValue();
				}
				return nr;
			}
		}
		else
		{
			return 0;
		}	
	}
	
	static Integer getPlayerGeneratorsCount(Player player, String generatorId)
	{
		if (playersGenerators.containsKey(player))
		{
			HashMap<String, Integer> playerGenerators = new HashMap<String, Integer>();
			playerGenerators = playersGenerators.get(player);
			
			if(playerGenerators == null)
			{
				return 0;
			}
			else
			{
				if (playerGenerators.containsKey(generatorId))
				{
					return playerGenerators.get(generatorId);
				}
				else
				{
					return 0;
				}
			}
		}
		else
		{
			return 0;
		}	
	}
	
	public static Boolean canPlace(Player player, String generatorId)
	
	{		
		if (KGenerators.overAllPerPlayerGeneratorsEnabled)
		{		
			Generator generator = KGenerators.generators.get(generatorId);
			
			PlayerLimits pLimits = new PlayerLimits(player);
			int globalLimit = pLimits.getGlobalLimit();
			int generatorLimit = pLimits.getLimit(generatorId);
						
			/* Checking global limit */
			if (globalLimit >= 0)
			{
				int allPlayerGenerators = getPlayerOverallGeneratorsCount(player);
				if (allPlayerGenerators >= globalLimit)
				{
					LangUtils.addReplecable("<number>", String.valueOf(globalLimit));
					LangUtils.sendMessage(player, Message.GeneratorsPPGCantPlaceMore);
					return false;
				}
			}
			
			/* Checking per generator limit */
			if (generatorLimit >= 0)
			{
				int playerGenerators = getPlayerGeneratorsCount(player, generatorId);
				if (playerGenerators >= generatorLimit)
				{
					LangUtils.addReplecable("<number>", String.valueOf(generatorLimit));
					LangUtils.addReplecable("<generator>", KGenerators.generators.get(generatorId).getGeneratorItem().getItemMeta().getDisplayName());
					LangUtils.sendMessage(player, Message.GeneratorsPPGCantPlaceMoreType);
					return false;
				}
			}
		}
		return true;
	}
	
	public static Boolean canPickUp(Player player, GeneratorLocation gLocation){
		if (KGenerators.overAllPerPlayerGeneratorsEnabled)
		{
			if (player.hasPermission("kgenerators.bypass.onlyownerchecks"))
			{
				return true;
			}
			
			/* To prevent errors if generator havent any owner */
			if (gLocation.getOwner() == null)
			{
				return true;
			}
			/* end */
			
			Generator generator = gLocation.getGenerator();
			if (generator.isOnlyOwnerPickUp())
			{
				if (gLocation.getOwner() == null)
				{
					return true;
				}
				
				if (!player.equals(gLocation.getOwner()))
				{
					LangUtils.addReplecable("<owner>", gLocation.getOwner().getDisplayName());
					LangUtils.sendMessage(player, Message.GeneratorsPPGCantPickUp);
					return false;
				}
			}
		}
		return true;
	}

	public static Boolean canUse(Player player, GeneratorLocation gLocation){
		if (KGenerators.overAllPerPlayerGeneratorsEnabled)
		{
			if (player.hasPermission("kgenerators.bypass.onlyownerchecks"))
			{
				return true;
			}
			
			/* To prevent errors if generator havent any owner */
			if (gLocation.getOwner() == null)
			{
				return true;
			}
			/* end */
			
			Generator generator = gLocation.getGenerator();
			if (generator.isOnlyOwnerUse())
			{
				if (!player.equals(gLocation.getOwner()))
				{
					LangUtils.addReplecable("<owner>", gLocation.getOwner().getDisplayName());
					LangUtils.sendMessage(player, Message.GeneratorsPPGCantUse);
					return false;
				}
			}
		}
		return true;
	}
	
	public static Boolean canPush(Generator generator)
	{
		if (generator.isOnlyOwnerUse())
		{
			return false;
		}
		return true;
	}
}
