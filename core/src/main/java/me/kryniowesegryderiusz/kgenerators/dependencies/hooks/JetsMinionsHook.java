package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.jet315.minions.events.MinerBlockBreakEvent;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class JetsMinionsHook implements Listener {
	
	@EventHandler
	public void MinionBreakEvent(final MinerBlockBreakEvent e) {
		Location location = e.getBlock().getLocation();
		
		if (Main.getLocations().exists(location)) {
			GeneratorLocation gLocation = Main.getLocations().get(location);
			
			if (!gLocation.isBlockPossibleToMine(location))
				e.setCancelled(true);
			else
				gLocation.scheduleGeneratorRegeneration();
		}
	}
}
