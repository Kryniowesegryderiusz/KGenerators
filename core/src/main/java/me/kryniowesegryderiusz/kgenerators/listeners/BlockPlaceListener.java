package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;

public class BlockPlaceListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(final BlockPlaceEvent e) {
		
		if (e.isCancelled()) return;
		
		Player player = e.getPlayer();
		
		if (Main.getPlacedGenerators().getLoaded(e.getBlock().getLocation()) != null)
		{
			Lang.getMessageStorage().send(player, Message.GENERATORS_PLACE_CANT_PLACE_BLOCK);
			e.setCancelled(true);
			return;
		}
		
		Generator generator = Main.getGenerators().get(e.getItemInHand());
		if (generator != null)
	    	e.setCancelled(!new GeneratorLocation(generator, e.getBlock().getLocation(), Main.getPlayers().getPlayer(player)).placeGenerator(player));
	}
}
