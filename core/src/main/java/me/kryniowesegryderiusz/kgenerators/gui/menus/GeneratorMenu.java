package me.kryniowesegryderiusz.kgenerators.gui.menus;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects.Upgrade;
import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;

public class GeneratorMenu {
	
	public static Inventory get(Player player, GeneratorLocation gLocation)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.GENERATOR_MENU_RESET);
		exludedEnumMenuItems.add(MenuItemType.GENERATOR_MENU_UPGRADE);
		exludedEnumMenuItems.add(MenuItemType.GENERATOR_MENU_UPGRADE_MAXED);
				
		String time = Main.getSchedules().timeLeftFormatted(gLocation);
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
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_RESET).build(player));
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
				ItemStack is = mi.build(player);
				
				for (int i : Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE).getSlots())
				{
					menu.setItem(i, is);
				}
			}
		}
		else if (!Main.getUpgrades().getPreviousGeneratorId(gLocation.getGenerator().getId()).isEmpty())
		{
			if (Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE_MAXED).isEnabled())
			{
				for (int i : Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE_MAXED).getSlots())
				{
					menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE_MAXED).build(player));
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
			Main.getMenus().getMenuPlayer(p).getGLocation().pickUpGenerator(p);
			Main.getMenus().closeInv(p);
		}
		else if (Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_QUIT).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_QUIT).isEnabled())
		{
			Main.getMenus().closeInv(p);
		}
		else if (Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_RESET).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_RESET).isEnabled())
		{
			GeneratorLocation gl = Main.getMenus().getMenuPlayer(p).getGLocation();
			if (gl.isBroken())
			{
				if (gl.getGenerator().getType() == GeneratorType.DOUBLE)
					Main.getMultiVersion().getBlocksUtils().setBlock(gl.getLocation(), gl.getGenerator().getGeneratorItem());
				gl.scheduleGeneratorRegeneration();
				Lang.getMessageStorage().send(p, Message.GENERATORS_ANY_REPAIRED);
				Main.getMenus().closeInv(p);
			}
		}
		else if (Main.getMenus().getMenuPlayer(p).getGLocation().getGenerator().getUpgrade() != null
				&& Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.GENERATOR_MENU_UPGRADE).isEnabled())
		{
			Main.getMenus().getMenuPlayer(p).getGLocation().getGenerator().getUpgrade().blockUpgrade(Main.getMenus().getMenuPlayer(p).getGLocation(), p);
			Main.getMenus().closeInv(p);
		}
	}
}
