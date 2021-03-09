package me.kryniowesegryderiusz.kgenerators.api.events;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;

public class PreBlockGenerationEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter
    Location location;
    
    @Getter
    Generator generator;
    
    @Getter
    @Nullable GeneratorPlayer owner;

    public PreBlockGenerationEvent(Location location, Generator generator, GeneratorPlayer owner) {
		this.location = location;
		this.generator = generator;
		this.owner = owner;
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