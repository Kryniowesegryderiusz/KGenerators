package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.classes.Limit;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.classes.PlayerLimits;
import me.kryniowesegryderiusz.kgenerators.classes.StringContent;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.managers.Limits;
import me.kryniowesegryderiusz.kgenerators.managers.Players;

public class LimitsMenu implements Listener {
	
	public static Inventory get(Player player)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.LIMITS_MENU_LIMIT);
		
		Inventory menu = Lang.getMenuInventory(MenuInventoryType.LIMITS).getInv(MenuInventoryType.LIMITS, player, exludedEnumMenuItems);
		
		ArrayList<Integer> slotList = Lang.getMenuItem(MenuItemType.LIMITS_MENU_LIMIT).getSlots();
		int lastId = -1;
		
		GeneratorPlayer gp = Players.getPlayer(player);
		PlayerLimits pl = new PlayerLimits(player);
		
		for (Limit limit : Limits.getValues())
		{
			ItemStack item = limit.getItem().clone();
			MenuItem chanceMenuItem = Lang.getMenuItem(MenuItemType.LIMITS_MENU_LIMIT);
			if (chanceMenuItem.getItemType().contains("<limit_display_item>"))
				chanceMenuItem.setItemStack(item);

			chanceMenuItem.replace("<limit_name>", limit.getName());
			chanceMenuItem.replace("<amount>", String.valueOf(limit.getPlacedGenerators(gp)));
			chanceMenuItem.replace("<limit>", String.valueOf(pl.getLimit(limit)));
			
			ArrayList<String> generators = new ArrayList<String>();
			for (Generator g : limit.getGenerators())
			{
				StringContent s = new StringContent(Lang.getMenuItemAdditionalLines(MenuItemAdditionalLines.LIMITS_GENERATOR_LIST));
				s.replace("<generator_name>", g.getGeneratorItemName());
				generators.addAll(s.getLines());
			}
			chanceMenuItem.replaceLore("<generators_list>", generators);
			
			if (limit.isOnlyOwnerUse())
				chanceMenuItem.replaceLore("<is_only_owner_use>", Lang.getMenuItemAdditionalLines(MenuItemAdditionalLines.LIMITS_GENERATOR_ONLY_OWNER_USE));
			else
				chanceMenuItem.replaceLore("<is_only_owner_use>", null);
			
			if (limit.isOnlyOwnerPickUp())
				chanceMenuItem.replaceLore("<is_only_owner_pick_up>", Lang.getMenuItemAdditionalLines(MenuItemAdditionalLines.LIMITS_GENERATOR_ONLY_OWNER_PICK_UP));
			else
				chanceMenuItem.replaceLore("<is_only_owner_pick_up>", null);
			
			lastId++;
			
			ItemStack readyItem = chanceMenuItem.build();
			try {
				menu.setItem(slotList.get(lastId), readyItem);
			} catch (Exception e1) {
				Logger.error("Lang: There is probably more limits than slots set in /lang/gui/limits.limit");
				Logger.error(e1);
			}
		}
		
		return menu;
	}

	@EventHandler
	public void onClick(final InventoryClickEvent e)
	{
		if(e.isCancelled()) return;
		Player p = (Player) e.getWhoClicked();
		if (!Menus.isVieving(p, MenuInventoryType.LIMITS)) return;
		
		int slot = e.getSlot();
		
		if (Lang.getMenuItem(MenuItemType.LIMITS_MENU_BACK).getSlots().contains(slot) && Lang.getMenuItem(MenuItemType.LIMITS_MENU_BACK).isEnabled())
		{
			Menus.openMainMenu(p, Menus.getMenuPlayer(p).getGenerator());
		}
		e.setCancelled(true);
	}
}
