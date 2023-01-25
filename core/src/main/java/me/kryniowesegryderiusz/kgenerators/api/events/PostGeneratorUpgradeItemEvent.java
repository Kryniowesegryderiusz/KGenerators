package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.Upgrade;

/**
 * Event is fired, when generator is being removed from world.
 * @author user
 */
public class PostGeneratorUpgradeItemEvent extends Event {
	
    private static final HandlerList HANDLERS = new HandlerList();
    
    @Getter private Upgrade upgrade;
    @Getter private Player player;
    @Getter private ItemStack item;

	public PostGeneratorUpgradeItemEvent(Upgrade upgrade, Player p, ItemStack item) {
		this.upgrade = upgrade;
		this.player = p;
		this.item = item;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
