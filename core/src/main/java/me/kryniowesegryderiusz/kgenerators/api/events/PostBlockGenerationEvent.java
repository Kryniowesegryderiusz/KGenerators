package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;

public class PostBlockGenerationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter
    GeneratorLocation generatorLocation;

    public PostBlockGenerationEvent(GeneratorLocation generatorLocation) {
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