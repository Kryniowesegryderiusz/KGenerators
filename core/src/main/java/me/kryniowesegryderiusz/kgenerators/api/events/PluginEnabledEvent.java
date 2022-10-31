package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event is fired after plugin is fully loaded.
 * @author user
 */
public class PluginEnabledEvent extends Event {
	
    private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
