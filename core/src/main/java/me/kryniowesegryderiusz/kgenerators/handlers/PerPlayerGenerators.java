package me.kryniowesegryderiusz.kgenerators.handlers;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.PlayerLimits;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;

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
	
	public static Integer getPlayerGeneratorsCount(Player player, String generatorId)
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
		if (Main.getSettings().isPerPlayerGenerators())
		{		
			Generator generator = Generators.get(generatorId);
			
			PlayerLimits pLimits = new PlayerLimits(player);
			int globalLimit = pLimits.getGlobalLimit();
			int generatorLimit = pLimits.getLimit(generatorId);
						
			/* Checking global limit */
			if (globalLimit >= 0)
			{
				int allPlayerGenerators = getPlayerOverallGeneratorsCount(player);
				if (allPlayerGenerators >= globalLimit)
				{
					Lang.addReplecable("<number>", String.valueOf(globalLimit));
					Lang.sendMessage(player, EnumMessage.GeneratorsPPGCantPlaceMore);
					return false;
				}
			}
			
			/* Checking per generator limit */
			if (generatorLimit >= 0)
			{
				int playerGenerators = getPlayerGeneratorsCount(player, generatorId);
				if (playerGenerators >= generatorLimit)
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
	
	public static Boolean canPickUp(Player player, GeneratorLocation gLocation){
		if (Main.getSettings().isPerPlayerGenerators())
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
			if (generator.getOnlyOwnerPickUp())
			{
				if (gLocation.getOwner() == null)
				{
					return true;
				}
				
				if (!player.equals(gLocation.getOwner()))
				{
					Lang.addReplecable("<owner>", gLocation.getOwner().getDisplayName());
					Lang.sendMessage(player, EnumMessage.GeneratorsPPGCantPickUp);
					return false;
				}
			}
		}
		return true;
	}

	public static Boolean canUse(Player player, GeneratorLocation gLocation){
		if (Main.getSettings().isPerPlayerGenerators())
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
			if (generator.getOnlyOwnerUse())
			{
				if (!player.equals(gLocation.getOwner()))
				{
					Lang.addReplecable("<owner>", gLocation.getOwner().getDisplayName());
					Lang.sendMessage(player, EnumMessage.GeneratorsPPGCantUse);
					return false;
				}
			}
		}
		return true;
	}
	
	public static Boolean canPush(Generator generator)
	{
		if (generator.getOnlyOwnerUse())
		{
			return false;
		}
		return true;
	}

	public static void clear() {
		playersGenerators.clear();
		
	}
}
