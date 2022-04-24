package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.InteractionType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class BlockBreakListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void BlockBreakEvent(final BlockBreakEvent e)
	{
		if (e.isCancelled()) return;
		
		Player p = e.getPlayer();
		
		GeneratorLocation gLoc = Main.getLocations().get(e.getBlock().getLocation());
		
		if (gLoc == null) return;

		e.setCancelled(true);
		
		if (gLoc.handleAction(InteractionType.BREAK, p)) return;
		
		if (gLoc.getGeneratedBlockLocation().equals(e.getBlock().getLocation())
				&& !Main.getSchedules().isScheduled(gLoc)
				&& gLoc.isPermittedToMine(p)
				&& Main.getPlayers().getPlayer(p).canUse(gLoc)) {
			e.setCancelled(false);
			gLoc.scheduleGeneratorRegeneration();
		}
	}
}
