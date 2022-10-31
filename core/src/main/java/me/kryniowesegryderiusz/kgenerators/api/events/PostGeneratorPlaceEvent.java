package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

/**
 * Event is fired, when generator is being removed from world.
 * @author user
 */
public class PostGeneratorPlaceEvent extends Event {
	
    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter IGeneratorLocation generatorLocation;
    @Getter CommandSender sender;

	public PostGeneratorPlaceEvent(GeneratorLocation gLocation, CommandSender sender) {
		this.generatorLocation = gLocation;
		this.sender = sender;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
