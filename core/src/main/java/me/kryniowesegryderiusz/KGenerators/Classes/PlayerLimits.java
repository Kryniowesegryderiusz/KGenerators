package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;

public class PlayerLimits {
	
	private int globalLimit;
	private HashMap<String, Integer> generatorsLimits = new HashMap<String, Integer>();
	
	public PlayerLimits(Player player)
	{	
		
		/* Filling variables */
		this.globalLimit = Main.getSettings().getPerPlayerGeneratorsPlaceLimit();
		
		for (Entry<String, Generator> e : Generators.getEntrySet())
		{
			this.generatorsLimits.put(e.getKey(), e.getValue().getPlaceLimit());
		}
		
		ArrayList<String> bypassedGenerators = new ArrayList<String>();
		Boolean globalLimitBypass = false;
		
		/* Checking limits */
		player.recalculatePermissions();
		
		for (PermissionAttachmentInfo pai : player.getEffectivePermissions())
		{
			
			String perm = pai.getPermission();
			//Logger.log(player.getName() + " " + perm + " " + pai.getValue());
			
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
			
			for (Entry<String, Generator> e : Generators.getEntrySet())
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
				
				if (perm.contains("kgenerators.placelimit."+generatorId+".bypass"))
				{
					bypassedGenerators.add(generatorId);
				}
			}
			
			if (perm.contains("kgenerators.overallplacelimit.bypass"))
			{
				globalLimitBypass = true;
			}
		}
		
		/* Adjusting limits basing on bypass permissions */
		if (globalLimitBypass)
		{
			this.globalLimit = -1;
		}
		
		for (String generatorId : bypassedGenerators)
		{
			this.generatorsLimits.put(generatorId, -1);
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
