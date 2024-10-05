package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.waterarchery.litminions.api.events.MinerMinionBreakEvent;

public class LitMinionsHook implements Listener {
	
	@EventHandler
	public void MinionBreakEvent(final MinerMinionBreakEvent e) {
		Location location = e.getBlock().getLocation();
		
		if (!Main.getPlacedGenerators().isChunkFullyLoaded(location)) {
			e.setCancelled(true);
			return;
		}
		
		GeneratorLocation gLocation = Main.getPlacedGenerators().getLoaded(location);
		if (gLocation != null) {
			if (!gLocation.isBlockPossibleToMine(location))
				e.setCancelled(true);
			else {
				if (gLocation.getLastGeneratedObject() != null && gLocation.getLastGeneratedObject().getCustomDrops() != null) {
					if (gLocation.getLastGeneratedObject().getCustomDrops().isRemoveDefaults())
						e.getDrops().clear();
					e.getDrops().add(gLocation.getLastGeneratedObject().getCustomDrops().getItem().clone());
					e.getMinion().setExp(e.getMinion().getExp() + gLocation.getLastGeneratedObject().getCustomDrops().getExp());
					gLocation.getLastGeneratedObject().getCustomDrops().doCommandDrops(e.getMinion().getOwnerName(), location);
				}
				gLocation.scheduleGeneratorRegeneration();
			}
		}
	}
}
