package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

/**
 * Event is fired, when generator is being removed from world.
 * @author user
 */
public class GeneratorRemoveEvent extends Event {
	
    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter IGeneratorLocation generatorLocation;
    @Getter boolean dropGeneratorItem;
    @Getter ItemStack generatorItem;
    @Getter Player player;
    
    public GeneratorRemoveEvent(GeneratorLocation gLocation, ItemStack generatorItem, boolean dropGeneratorItem, Player player) {
    	this.generatorItem = generatorItem;
    	this.player = player;
    	this.dropGeneratorItem = dropGeneratorItem;
    	this.generatorLocation = gLocation;
    }

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
