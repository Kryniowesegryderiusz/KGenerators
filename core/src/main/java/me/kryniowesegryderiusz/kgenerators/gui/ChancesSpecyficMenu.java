package me.kryniowesegryderiusz.kgenerators.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.Lang;

public class ChancesSpecyficMenu implements Listener {
	
	public static Inventory get(Player player, Generator generator)
	{
		Inventory menu = Lang.getMenuInventory(EnumMenuInventory.ChancesSpecific).getInv(EnumMenuInventory.ChancesSpecific, player, generator);
		return menu;
	}
	
	@EventHandler
	public void onClick(final InventoryClickEvent e)
	{
		if(e.isCancelled()) return;
		if (!Menus.isVieving((Player) e.getWhoClicked(), EnumMenuInventory.ChancesSpecific)) return;
		
		int slot = e.getSlot();
		
		if (EnumMenuItem.ChancesSpecificMenuBack.getMenuItem().getSlots().contains(slot))
		{
			Menus.openChancesListMenu((Player) e.getWhoClicked());
		}
		e.setCancelled(true);
	}
}
