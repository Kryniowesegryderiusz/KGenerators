package me.kryniowesegryderiusz.kgenerators.addons.events;

import java.util.HashMap;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IHologramText;

public class HologramReplaceLinesEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter private IHologramText hologram;
    @Getter private GeneratorLocation generatorLocation;
    @Getter private HashMap<String, String> replacablesMap = new HashMap<String, String>();

    public HologramReplaceLinesEvent(GeneratorLocation generatorLocation, IHologramText hologram) {
		this.generatorLocation = generatorLocation;
		this.hologram = hologram;
	}

	@Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}