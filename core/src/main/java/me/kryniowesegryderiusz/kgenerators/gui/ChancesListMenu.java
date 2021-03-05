package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;

public class ChancesListMenu implements Listener {
	
	public static Inventory get(Player player)
	{
		Inventory menu = Lang.getMenuInventory(EnumMenuInventory.ChancesList).getInv(EnumMenuInventory.ChancesList, player);
		
		return menu;
	}
	
	@EventHandler
	public void onClick(final InventoryClickEvent e)
	{
		if(e.isCancelled()) return;
		if (!Menus.isVieving((Player) e.getWhoClicked(), EnumMenuInventory.ChancesList)) return;
		
		int slot = e.getSlot();
		
		ArrayList<Integer> slotList = EnumMenuItem.ChancesListMenuGenerator.getMenuItem().getSlots();
		if (slotList.contains(slot))
		{
			int lastId = -1;
			for (Entry<String, Generator> entry : Generators.getEntrySet())
			{
				lastId++;
				if(slotList.get(lastId) == slot)
				{
					Menus.openChancesSpecificMenu((Player) e.getWhoClicked(), entry.getValue());
				}
			}
		}
		e.setCancelled(true);
	}
}
