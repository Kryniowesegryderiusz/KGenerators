package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratorProtectionListeners implements Listener {
	
	/*
	 * Blocks physics
	 */

	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent e) {
		try {
			if (Main.getPlacedGenerators().isLoaded(e.getBlock().getLocation()))
				e.setCancelled(true);
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
	
	@EventHandler
	public void onBlockFade(BlockFadeEvent e) {
		try {
			if (Main.getPlacedGenerators().isLoaded(e.getBlock().getLocation()))
				e.setCancelled(true);
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
	
	@EventHandler
	public void onBlockSpread(BlockSpreadEvent e) {
		try {
			if (Main.getPlacedGenerators().isLoaded(e.getBlock().getLocation()))
				e.setCancelled(true);
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
	
	@EventHandler
	public void onBlockForm(BlockFormEvent e) {
		try {
			if (Main.getPlacedGenerators().isLoaded(e.getBlock().getLocation()))
				e.setCancelled(true);
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}
	
	/*
	 * Explosions
	 */
	
	@EventHandler
	public void onBlockExplode(final BlockExplodeEvent e) {

		try {
			if (e.isCancelled()) {
				return;
			}
			e.setCancelled(this.isExplosionCancelled(e.blockList()));
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}

	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent e) {
		try {
			if (e.isCancelled()) {
				return;
			}
			e.setCancelled(this.isExplosionCancelled(e.blockList()));
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}

	/**
	 * #0 - cancel explosion #1 - drop generator #2 - remove generator
	 * 
	 * @param blocks
	 * @return isExplosionCancelled
	 */
	private Boolean isExplosionCancelled(List<Block> blocks) {
		for (Block block : blocks) {
			short handler = Main.getSettings().getExplosionHandler();
			GeneratorLocation gLoc = Main.getPlacedGenerators().getLoaded(block.getLocation());
			if (gLoc != null) {
				if (handler == 1 || handler == 2) {
					boolean drop = false;
					if (handler == 1)
						drop = true;
					gLoc.removeGenerator(drop, null);
				} else
					return true;
			}
		}
		return false;
	}
}
