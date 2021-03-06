package me.kryniowesegryderiusz.kgenerators.hooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.thebusybiscuit.slimefun4.api.events.AndroidMineEvent;
import io.github.thebusybiscuit.slimefun4.api.events.ExplosiveToolBreakBlocksEvent;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;

public class SlimefunHook implements Listener {
	
	/*
	 * Handles android mining
	 * @param e
	 */
	@EventHandler
	public void onAndroidMineEvent (final AndroidMineEvent e){
		
		if (e.isCancelled())
			return;
		e.setCancelled(handler(e.getBlock().getLocation()));
	}
	
	/*
	 * Handles all explosion regeneration on SINGLE GeneratorType. If DOUBLE event is cancelled.
	 * @param e
	 */
	@EventHandler
	public void onExplosiveToolBreakBlocksEvent (final ExplosiveToolBreakBlocksEvent e){
		
		if (e.isCancelled())
			return;
		
		ArrayList<Block> blocks = new ArrayList<Block>();
		blocks.addAll(e.getAdditionalBlocks());
		blocks.add(e.getPrimaryBlock());
		
		boolean cancel = false;
		for (Block b : blocks)
		{
			if (handler(b.getLocation()))
				cancel = true;
		}
		if (cancel)
			Lang.sendMessage(e.getPlayer(), Message.HOOKS_EXPLODE_PICKAXE_CANNOT_USE_ON_DOUBLE);
		e.setCancelled(cancel);
	}
	
	/**
	 * Schould event be cancelled?
	 * @param Location mined
	 */
	public static boolean handler(Location l)
	{
		GeneratorLocation gLocation = Locations.get(l);
		GeneratorLocation ugLocation = Locations.get(l.clone().add(0,-1,0));
		
		/*
		 * Double generator base protection
		 */
		if (gLocation != null && gLocation.getGenerator().getType() == GeneratorType.DOUBLE)
			return true;
		else if (ugLocation != null && ugLocation.getGenerator().getType() == GeneratorType.DOUBLE)
		{
			ugLocation.regenSchedule();
		}
		else if (gLocation != null && gLocation.getGenerator().getType() == GeneratorType.SINGLE)
		{
			gLocation.regenSchedule();
		}
		return false;
	}

}
