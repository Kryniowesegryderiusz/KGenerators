package me.kryniowesegryderiusz.KGenerators.Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.jet315.minions.events.MinerBlockBreakEvent;
import me.kryniowesegryderiusz.KGenerators.GenerateBlockFunction;
import me.kryniowesegryderiusz.KGenerators.KGenerators;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;

public class onJetsMinions implements Listener {
	
	@EventHandler
	public void MinionBreakEvent(final MinerBlockBreakEvent e){
		Location location = e.getBlock().getLocation();
		ItemStack block = KGenerators.getBlocksUtils().getItemStackByBlock(e.getBlock());
		Location bLocation = location.clone().add(0,-1,0);
		
		if (KGenerators.generatorsLocations.containsKey(location)){
			Generator generator = KGenerators.generators.get(KGenerators.generatorsLocations.get(location));
			if (generator.getType().equals("single")) {
				if (generator.getChances().containsKey(block))
				{
					GenerateBlockFunction.generateBlock(location, generator, 1);
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
		
		if (KGenerators.generatorsLocations.containsKey(bLocation)){
			Generator generator = KGenerators.generators.get(KGenerators.generatorsLocations.get(bLocation));
			if (generator.getType().equals("double")) {
				if (generator.getChances().containsKey(block))
				{
					GenerateBlockFunction.generateBlock(location, generator, 1);
				}
				else
				{
					e.setCancelled(true);
				}
			}
		}
	}
}
