package me.kryniowesegryderiusz.KGenerators.Listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.kryniowesegryderiusz.KGenerators.Main;

public class onExplosion implements Listener {
	
	@EventHandler
	public void onUnknown(final BlockExplodeEvent e)
	{
		if(e.isCancelled()){return;}
		e.setCancelled(explosion(e.blockList()));
	}
	
	@EventHandler
	public void onUnknown(final EntityExplodeEvent e)
	{
		if(e.isCancelled()){return;}
		e.setCancelled(explosion(e.blockList()));
	}
	
	private Boolean explosion (List<Block> blocks)
	{
		for (Block block : blocks)
		{
			if (Main.generatorsLocations.containsKey(block.getLocation()) || Main.generatorsLocations.containsKey(block.getLocation().clone().add(0,-1,0)))
			{
				return true;
			}
		}
		return false;
	}

}
