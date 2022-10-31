package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event is fired after reload command.
 * All lang storage classes should be registered again after it
 * @author user
 *
 */
public class PluginReloadEvent extends Event {
	
    private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
