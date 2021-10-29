package me.kryniowesegryderiusz.kgenerators.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;

public class CustomMenuClickEvent extends Event {
	
	private static final HandlerList HANDLERS = new HandlerList();
	
    @Getter Player player;
    @Getter Enum<?> menuType;
    @Getter int slot;
    @Getter ClickType clickType;
    @Getter ItemStack currentItem;

    public CustomMenuClickEvent(Player player, Enum<?> menuType, int slot, ClickType clickType, ItemStack currentItem) {
		this.player = player;
		this.menuType = menuType;
		this.slot = slot;
		this.clickType = clickType;
		this.currentItem = currentItem;
	}

	@Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
	
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
