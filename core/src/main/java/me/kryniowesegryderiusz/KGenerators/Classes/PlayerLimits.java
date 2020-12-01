package me.kryniowesegryderiusz.KGenerators.Classes;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.kryniowesegryderiusz.KGenerators.KGenerators;

public class PlayerLimits {
	
	private int globalLimit;
	private HashMap<String, Integer> generatorsLimits = new HashMap<String, Integer>();
	
	public PlayerLimits(Player player)
	{	
		
		/* Filling variables */
		this.globalLimit = KGenerators.overAllPerPlayerGeneratorsPlaceLimit;
		for (Entry<String, Generator> e : KGenerators.generators.entrySet())
		{
			this.generatorsLimits.put(e.getKey(), e.getValue().getPlaceLimit());
		}
		
		/* Adjusting limits basing on numeric permissions */
		for (PermissionAttachmentInfo pai : player.getEffectivePermissions())
		{
			String perm = pai.getPermission();
			
			if (perm.contains("kgenerators.overallplacelimit"))
			{
				String[] sperm = perm.split("\\.");
				int limit;
				try {
					limit = Integer.parseInt(sperm[2]);
					if (limit > this.globalLimit)
					{
						this.globalLimit = limit;
					}
				} catch (NumberFormatException e1) {}
			}
			
			for (Entry<String, Generator> e : KGenerators.generators.entrySet())
			{
				String generatorId = e.getKey();
				
				if (perm.contains("kgenerators.placelimit."+generatorId))
				{
					String[] sperm = perm.split("\\.");
					int limit;
					try {
						limit = Integer.parseInt(sperm[3]);
						if (limit > this.generatorsLimits.get(generatorId))
						{
							this.generatorsLimits.put(generatorId, limit);
						}
					} catch (NumberFormatException e1) {}
				}
			}
		}
		
		/* Adjusting limits basing on bypass permissions */
		if (player.hasPermission("kgenerators.overallplacelimit.bypass"))
		{
			this.globalLimit = -1;
		}
		
		for (Entry<String, Generator> e : KGenerators.generators.entrySet())
		{
			String generatorId = e.getKey();
			if (player.hasPermission("kgenerators.placelimit."+generatorId+".bypass"))
			{
				this.generatorsLimits.put(generatorId, -1);
			}
		}
	}
	
	public int getGlobalLimit()
	{
		return this.globalLimit;
	}
	
	public int getLimit(String generatorId)
	{
		return this.generatorsLimits.get(generatorId);
	}

}
