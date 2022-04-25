package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;

public class PreBlockGenerationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter IGeneratorLocation generatorLocation;

    public PreBlockGenerationEvent(IGeneratorLocation generatorLocation) {
		this.generatorLocation = generatorLocation;
	}

	@Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public void setCancelled(boolean cancel) {

	}
}