package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.jet315.minions.events.MinerBlockBreakEvent;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class JetsMinionsHook implements Listener {
	
	@EventHandler
	public void MinionBreakEvent(final MinerBlockBreakEvent e) {
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
					
					if (gLocation.getLastGeneratedObject().getCustomDrops().isRemoveDefaults()) {
						e.setCancelled(true);
						Main.getMultiVersion().getBlocksUtils().setBlock(location, Material.AIR);
						e.getMinion().setTotalActionsProcessed(e.getMinion().getTotalActionsProcessed()+1);
						e.getMinion().setActionsProcessedSinceHealthDrop(e.getMinion().getActionsProcessedSinceHealthDrop()+1);
					}
					
					if (!e.getMinion().addItemToChest(gLocation.getLastGeneratedObject().getCustomDrops().getItem().clone()).isEmpty())
						gLocation.getLastGeneratedObject().getCustomDrops().doItemDrops(null, location);

					e.getMinion().setExp(e.getMinion().getExp() + gLocation.getLastGeneratedObject().getCustomDrops().getExp());
					
					gLocation.getLastGeneratedObject().getCustomDrops().doCommandDrops(Main.getInstance().getServer().getOfflinePlayer(e.getMinion().getPlayerUUID()).getName(), location);
				}
				gLocation.scheduleGeneratorRegeneration();
			}
		}
	}
}
