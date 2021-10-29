package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.classes.Upgrade;
import me.kryniowesegryderiusz.kgenerators.handlers.PickUp;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;

public class GeneratorMenu {
	
	public static Inventory get(Player player, GeneratorLocation gLocation)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.GENERATOR_MENU_RESET);
		exludedEnumMenuItems.add(MenuItemType.GENERATOR_MENU_UPGRADE);
		exludedEnumMenuItems.add(MenuItemType.GENERATOR_MENU_UPGRADE_MAXED);
				
		String time = Schedules.timeLeftFormatted(gLocation);
		if (time.equals("")) time = Lang.getMessageStorage().get(Message.COMMANDS_TIME_LEFT_FORMAT_NONE, false);
			
		Inventory menu = Lang.getMenuInventoryStorage().get(MenuInventoryType.GENERATOR)
				.getInv(MenuInventoryType.GENERATOR, player, exludedEnumMenuItems, 
						"<owner>", gLocation.getOwner().getName(), 
						"<time>", time, 
						"<generator_name>", gLocation.getGenerator().getGeneratorItemName());
		
		if (gLocation.isBroken())
		{
			for (int i : Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_RESET).getSlots())
			{
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_RESET).build());
			}
		}
		
		Upgrade upgrade = gLocation.getGenerator().getUpgrade();
		if (upgrade != null)
		{
			MenuItem mi = Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE);
			if (mi.isEnabled())
			{
				mi.replace("<next_generator>", upgrade.getNextGenerator().getGeneratorItemName());
				mi.replaceLore("<costs>", upgrade.getCostsFormattedGUI());
				ItemStack is = mi.build();
				
				for (int i : Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE).getSlots())
				{
					menu.setItem(i, is);
				}
			}
		}
		else if (!Upgrades.getPreviousGeneratorId(gLocation.getGeneratorId()).isEmpty())
		{
			if (Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE_MAXED).isEnabled())
			{
				for (int i : Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE_MAXED).getSlots())
				{
					menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE_MAXED).build());
				}
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
		if (Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_PICK_UP).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_PICK_UP).isEnabled())
		{
			PickUp.pickup(p, Menus.getMenuPlayer(p).getGLocation());
			Menus.closeInv(p);
		}
		else if (Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_QUIT).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_QUIT).isEnabled())
		{
			Menus.closeInv(p);
		}
		else if (Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_RESET).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_RESET).isEnabled())
		{
			GeneratorLocation gl = Menus.getMenuPlayer(p).getGLocation();
			if (gl.isBroken())
			{
				if (gl.getGenerator().getType() == GeneratorType.DOUBLE)
					Main.getBlocksUtils().setBlock(gl.getLocation(), gl.getGenerator().getGeneratorBlock());
				Schedules.schedule(gl);
				Lang.getMessageStorage().send(p, Message.GENERATORS_ANY_REPAIRED);
				Menus.closeInv(p);
			}
		}
		else if (Menus.getMenuPlayer(p).getGLocation().getGenerator().getUpgrade() != null && Main.dependencies.contains(Dependency.VAULT_ECONOMY)
				&& Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE).isEnabled())
		{
			Menus.getMenuPlayer(p).getGLocation().getGenerator().getUpgrade().blockUpgrade(Menus.getMenuPlayer(p).getGLocation(), p);
			Menus.closeInv(p);
		}
	}
}
