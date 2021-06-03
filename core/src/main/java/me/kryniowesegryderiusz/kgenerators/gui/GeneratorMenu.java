package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.PickUp;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;

public class GeneratorMenu implements Listener {
	
	public static Inventory get(Player player, GeneratorLocation gLocation)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
				
		String time = Schedules.timeLeftFormatted(gLocation);
		if (time.equals("")) time = Lang.getMessage(Message.COMMANDS_TIME_LEFT_FORMAT_NONE, false, false);

		Inventory menu = Lang.getMenuInventory(MenuInventoryType.GENERATOR).getInv(MenuInventoryType.GENERATOR, player, exludedEnumMenuItems, "<owner>", gLocation.getOwner().getName(), "<time>", time);
		
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
		if (!Menus.isVieving((Player) e.getWhoClicked(), MenuInventoryType.GENERATOR)) return;
		
		int slot = e.getSlot();
		if (MenuItemType.GENERATOR_MENU_PICK_UP.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.GENERATOR_MENU_PICK_UP).isEnabled())
		{
			Player p = (Player) e.getWhoClicked();
			PickUp.pickup(p, Menus.getMenuPlayer(p).getGLocation());
			Menus.closeInv(p);
		}
		if (MenuItemType.GENERATOR_MENU_QUIT.getMenuItem().getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.GENERATOR_MENU_QUIT).isEnabled())
		{
			Menus.closeInv((Player) e.getWhoClicked());
		}
		
		e.setCancelled(true);
	}
}
