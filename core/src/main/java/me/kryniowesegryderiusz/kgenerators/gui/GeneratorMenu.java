package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.PickUp;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;

public class GeneratorMenu implements Listener {
	
	public static Inventory get(Player player, GeneratorLocation gLocation)
	{
		ArrayList<EnumMenuItem> exludedEnumMenuItems = new ArrayList<EnumMenuItem>();
				
		String time = Schedules.timeLeftFormatted(gLocation);
		if (time.equals("")) time = Lang.getMessage(EnumMessage.CommandsTimeLeftFormatNone, false, false);

		Inventory menu = Lang.getMenuInventory(EnumMenuInventory.Generator).getInv(EnumMenuInventory.Generator, player, exludedEnumMenuItems, "<owner>", gLocation.getOwner().getName(), "<time>", time);
		
		return menu;
	}
	
	public static void update(Inventory inventory, Player player, GeneratorLocation gLocation)
	{
		inventory.setContents(get(player, gLocation).getContents());
	}
	
	@EventHandler
	public void onClick(final InventoryClickEvent e)
	{
		if(e.isCancelled()) return;
		if (!Menus.isVieving((Player) e.getWhoClicked(), EnumMenuInventory.Generator)) return;
		
		int slot = e.getSlot();
		if (EnumMenuItem.GeneratorMenuPickUp.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(EnumMenuItem.GeneratorMenuPickUp).isEnabled())
		{
			Player p = (Player) e.getWhoClicked();
			PickUp.pickup(p, Menus.getMenuPlayer(p).getGLocation());
			Menus.closeInv(p);
		}
		if (EnumMenuItem.GeneratorMenuQuit.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(EnumMenuItem.GeneratorMenuQuit).isEnabled())
		{
			Menus.closeInv((Player) e.getWhoClicked());
		}
		
		e.setCancelled(true);
	}
}
