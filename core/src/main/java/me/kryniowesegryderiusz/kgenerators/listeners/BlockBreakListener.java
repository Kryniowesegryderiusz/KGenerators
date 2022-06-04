package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.WorldGuardHook;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.InteractionType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.utils.PlayerUtils;

public class BlockBreakListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(final BlockBreakEvent e)
	{
		if (e.isCancelled()) return;
		
		Player p = e.getPlayer();
		
		GeneratorLocation gLoc = Main.getPlacedGenerators().getLoaded(e.getBlock().getLocation());
		
		if (gLoc != null) {
			
			if (gLoc.handleAction(InteractionType.BREAK, p)) {
				e.setCancelled(true);
				return;
			}
			
			if (gLoc.getGeneratedBlockLocation().equals(e.getBlock().getLocation())
					&& !gLoc.getGenerator().isPlaceholder(Main.getMultiVersion().getBlocksUtils().getItemStackByBlock(e.getBlock())) //Cant use !isScheduled, because ItemsAdder fires BlockBreakEvent twice
					&& gLoc.isPermittedToMine(p)
					&& Main.getPlayers().getPlayer(p).canUse(gLoc)) {
				
				gLoc.scheduleGeneratorRegeneration();
				
				if (Main.getSettings().isBlockDropUpToEq()) {
					
		    		if (!p.hasPermission("kgenerators.droptoinventory"))
		    			return;

		    		if (Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) {
		    			return;
		    		}
		    		
		    		e.setDropItems(false);
		    		
		    		int exp = e.getExpToDrop();
		    		e.setExpToDrop(0);
		    		p.giveExp(exp);
		    		
		    		for (ItemStack item : e.getBlock().getDrops(p.getItemInHand(), p)){
		    			PlayerUtils.dropToInventory(p, gLoc.getGeneratedBlockLocation(), item);
		    		}
				}
				
			} else
				e.setCancelled(true);
		} else if (!WorldGuardHook.isPlayerAllowedToMine(p, e.getBlock().getLocation())) e.setCancelled(true);
	}
}
