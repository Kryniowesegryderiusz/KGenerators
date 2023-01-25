package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.Upgrade;

/**
 * Event is fired, when generator is being removed from world.
 * @author user
 */
public class PostGeneratorUpgradeGeneratorLocationEvent extends Event {
	
    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter private Upgrade upgrade;
    @Getter private Player player;
    @Getter private IGeneratorLocation generatorLocation;

	public PostGeneratorUpgradeGeneratorLocationEvent(Upgrade upgrade, Player player, IGeneratorLocation generatorLocation) {
		this.upgrade = upgrade;
		this.generatorLocation = generatorLocation;
		this.player = player;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
