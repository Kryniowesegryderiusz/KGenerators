package me.kryniowesegryderiusz.kgenerators.api.events;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;

public class PostBlockGenerationEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter
    Location location;
    
    @Getter
    Generator generator;
    
    @Getter
    @Nullable GeneratorPlayer owner;

    public PostBlockGenerationEvent(Location location, Generator generator, GeneratorPlayer owner) {
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
}