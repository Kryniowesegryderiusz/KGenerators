package me.kryniowesegryderiusz.KGenerators.Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.jet315.minions.events.MinerBlockBreakEvent;
import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.GenerateBlock;

public class onJetsMinions implements Listener {
	
	@EventHandler
	public void MinionBreakEvent(final MinerBlockBreakEvent e){
		Location location = e.getBlock().getLocation();
		ItemStack block = Main.getBlocksUtils().getItemStackByBlock(e.getBlock());
		Location bLocation = location.clone().add(0,-1,0);
		
		if (Main.generatorsLocations.containsKey(location)){
			Generator generator = Main.generators.get(Main.generatorsLocations.get(location));
			if (generator.getType().equals("single")) {
				if (generator.getChances().containsKey(block))
				{
					GenerateBlock.generateBlock(location, generator, 1);
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
		
		if (Main.generatorsLocations.containsKey(bLocation)){
			Generator generator = Main.generators.get(Main.generatorsLocations.get(bLocation));
			if (generator.getType().equals("double")) {
				if (generator.getChances().containsKey(block))
				{
					GenerateBlock.generateBlock(location, generator, 1);
				}
				else
				{
					e.setCancelled(true);
				}
			}
		}
	}
}
