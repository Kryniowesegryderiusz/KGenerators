package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class BlockPistonListener implements Listener {

	@EventHandler
	public void onBlockPistonExtend(final BlockPistonExtendEvent e) {
		try {
			if (pistonEvent(e.getBlocks()))
				e.setCancelled(true);
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}

	@EventHandler
	public void onBlockPistonRetractEvent(final BlockPistonRetractEvent e) {
		try {
			if (pistonEvent(e.getBlocks()))
				e.setCancelled(true);
		} catch (Exception exception) {
			Logger.error(exception);
		}
	}

	/* Returns true if cancelled */
	Boolean pistonEvent(List<Block> blocks) {
		for (Block block : blocks) {
			GeneratorLocation gLoc = Main.getPlacedGenerators().getLoaded(block.getLocation());
			if (gLoc != null) {
				if (!gLoc.getGenerator().isAllowPistonPush()
						|| !gLoc.getGeneratedBlockLocation().equals(block.getLocation())
						|| Main.getSchedules().isScheduled(gLoc)
						|| Main.getLimits().isOnlyOwnerUse(gLoc.getGenerator())) {
					return true;
				} else {
					gLoc.scheduleGeneratorRegeneration();
				}
			}
		}
		return false;
	}
}
