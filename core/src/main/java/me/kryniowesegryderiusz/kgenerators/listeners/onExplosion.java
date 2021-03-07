package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.handlers.Remove;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;

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
			short handler = Main.getSettings().getExplosionHandler();
			Location location = block.getLocation();
			Location bLocation = location.clone().add(0,-1,0);
			boolean containsLocation = Locations.exists(location);
			boolean containsBLocation =  Locations.exists(bLocation);
			
			if(handler == 1 || handler == 2)
			{
				Location dropLocation = null;
				if (containsLocation) dropLocation = location;
				else if (containsBLocation) dropLocation = bLocation;
				
				if (dropLocation != null)
				{
					boolean drop = false;
					if (handler==1) drop = true;
										
					Remove.removeGenerator(dropLocation, drop);				
				}
			}
			else if (containsLocation || containsBLocation) return true;
		}
		return false;
	}

}
