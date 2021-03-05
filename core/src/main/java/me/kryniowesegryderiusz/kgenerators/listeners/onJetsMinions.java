package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.jet315.minions.events.MinerBlockBreakEvent;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.GenerateBlock;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;

public class onJetsMinions implements Listener {
	
	@EventHandler
	public void MinionBreakEvent(final MinerBlockBreakEvent e){
		Location location = e.getBlock().getLocation();
		ItemStack block = Main.getBlocksUtils().getItemStackByBlock(e.getBlock());
		Location bLocation = location.clone().add(0,-1,0);
		
		if (Locations.exists(location)){
			GeneratorLocation gLocation = Locations.get(location);
			Generator generator = gLocation.getGenerator();
			if (generator.getType() == GeneratorType.SINGLE) {
				if (generator.getChances().containsKey(block))
				{
					Schedules.schedule(gLocation);
				}
				else
				{
					e.setCancelled(true);
				}
			}
			else
			{
				e.setCancelled(true);
			}
		}
		
		if (Locations.exists(bLocation)){
			GeneratorLocation bgLocation = Locations.get(bLocation);
			Generator generator = bgLocation.getGenerator();
			if (generator.getType() == GeneratorType.DOUBLE) {
				if (generator.getChances().containsKey(block))
				{
					Schedules.schedule(bgLocation);
				}
				else
				{
					e.setCancelled(true);
				}
			}
		}
	}
}
