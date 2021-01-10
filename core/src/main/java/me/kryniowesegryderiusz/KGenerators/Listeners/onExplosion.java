package me.kryniowesegryderiusz.KGenerators.Listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.Remove;

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
			short handler = Main.explosionHandler;
			Location location = block.getLocation();
			Location bLocation = location.clone().add(0,-1,0);
			boolean containsLocation = Main.generatorsLocations.containsKey(location);
			boolean containsBLocation =  Main.generatorsLocations.containsKey(bLocation);
			
			if(handler == 1 || handler == 2)
			{
				Location dropLocation = null;
				if (containsLocation) dropLocation = location;
				else if (containsBLocation) dropLocation = bLocation;
				
				if (dropLocation != null)
				{
					boolean drop = false;
					if (handler==1) drop = true;
					
					GeneratorLocation gLocation = Main.generatorsLocations.get(dropLocation);
					
					Remove.removeGenerator(gLocation, dropLocation, drop);				
				}
			}
			else if (containsLocation || containsBLocation) return true;
		}
		return false;
	}

}
