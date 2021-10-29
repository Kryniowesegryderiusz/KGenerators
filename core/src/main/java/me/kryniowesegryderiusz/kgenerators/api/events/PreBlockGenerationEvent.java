package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;

public class PreBlockGenerationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter
    GeneratorLocation generatorLocation;

    public PreBlockGenerationEvent(GeneratorLocation generatorLocation) {
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCancelled(boolean cancel) {
		// TODO Auto-generated method stub
		
	}
}