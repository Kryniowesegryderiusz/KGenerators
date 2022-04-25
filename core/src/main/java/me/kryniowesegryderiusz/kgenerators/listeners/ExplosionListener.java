package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public class ExplosionListener implements Listener {
	
	@EventHandler
	public void onBlockExplode(final BlockExplodeEvent e) {
		if(e.isCancelled()){return;}
		e.setCancelled(this.isExplosionCancelled(e.blockList()));
	}
	
	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent e) {
		if(e.isCancelled()){return;}
		e.setCancelled(this.isExplosionCancelled(e.blockList()));
	}
	
	/**
	 * #0 - cancel explosion
	 * #1 - drop generator
	 * #2 - remove generator
	 * @param blocks
	 * @return isExplosionCancelled
	 */
	private Boolean isExplosionCancelled (List<Block> blocks) {
		for (Block block : blocks) {	
			short handler = Main.getSettings().getExplosionHandler();
			GeneratorLocation gLoc = Main.getLocations().get(block.getLocation());
			if (gLoc != null) {
				if(handler == 1 || handler == 2) {
					boolean drop = false;
					if (handler==1) drop = true;
					gLoc.removeGenerator(drop, null);
				} else return true;
			}
		}
		return false;
	}

}
