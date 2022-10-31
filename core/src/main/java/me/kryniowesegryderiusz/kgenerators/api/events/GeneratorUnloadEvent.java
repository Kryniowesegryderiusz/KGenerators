package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;

public class GeneratorUnloadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter IGeneratorLocation generatorLocation;

    public GeneratorUnloadEvent(IGeneratorLocation generatorLocation) {
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