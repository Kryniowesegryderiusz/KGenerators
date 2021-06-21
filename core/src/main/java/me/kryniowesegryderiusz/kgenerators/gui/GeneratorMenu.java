package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.handlers.PickUp;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;

public class GeneratorMenu {
	
	public static Inventory get(Player player, GeneratorLocation gLocation)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.GENERATOR_MENU_RESET);
				
		String time = Schedules.timeLeftFormatted(gLocation);
		if (time.equals("")) time = Lang.getMessage(Message.COMMANDS_TIME_LEFT_FORMAT_NONE, false, false);
			
		Inventory menu = Lang.getMenuInventory(MenuInventoryType.GENERATOR).getInv(MenuInventoryType.GENERATOR, player, exludedEnumMenuItems, "<owner>", gLocation.getOwner().getName(), "<time>", time, "<generator_name>", gLocation.getGenerator().getGeneratorItemName());
		
		if (gLocation.isBroken())
		{
			for (int i : Lang.getMenuItem(MenuItemType.GENERATOR_MENU_RESET).getSlots())
			{
				menu.setItem(i, Lang.getMenuItem(MenuItemType.GENERATOR_MENU_RESET).build());
			}
		}
		
		return menu;
	}
	
	public static void update(Inventory inventory, Player player, GeneratorLocation gLocation)
	{
		inventory.setContents(get(player, gLocation).getContents());
	}
	
	public static void onClick(Player p, int slot)
	{	
		if (Lang.getMenuItem(MenuItemType.GENERATOR_MENU_PICK_UP).getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.GENERATOR_MENU_PICK_UP).isEnabled())
		{
			PickUp.pickup(p, Menus.getMenuPlayer(p).getGLocation());
			Menus.closeInv(p);
		}
		else if (Lang.getMenuItem(MenuItemType.GENERATOR_MENU_QUIT).getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.GENERATOR_MENU_QUIT).isEnabled())
		{
			Menus.closeInv(p);
		}
		else if (Lang.getMenuItem(MenuItemType.GENERATOR_MENU_RESET).getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.GENERATOR_MENU_RESET).isEnabled())
		{
			if (Menus.getMenuPlayer(p).getGLocation().isBroken())
			{
				Schedules.schedule(Menus.getMenuPlayer(p).getGLocation());
				Lang.sendMessage(p, Message.GENERATORS_ANY_REPAIRED);
				Menus.closeInv(p);
			}
		}
	}
}
