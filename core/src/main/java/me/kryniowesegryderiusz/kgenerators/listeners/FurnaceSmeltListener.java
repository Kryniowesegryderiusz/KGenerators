package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;

import me.kryniowesegryderiusz.kgenerators.managers.Generators;

public class FurnaceSmeltListener implements Listener {
	
	@EventHandler
	public void furnaceSmeltEvent(final FurnaceSmeltEvent e)
	{
		if (Generators.get(e.getSource()) != null)
		{
			e.setCancelled(true);
		}
	}

}
