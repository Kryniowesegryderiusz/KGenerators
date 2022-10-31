package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;

public class PreGeneratorRegenerationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    @Setter @Getter private boolean cancelled = false;
    
    @Getter IGeneratorLocation generatorLocation;

    public PreGeneratorRegenerationEvent(IGeneratorLocation generatorLocation) {
		this.generatorLocation = generatorLocation;
	}

	@Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}