package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;

public class ChancesMenu implements Listener {
	
	public static Inventory get(Player player, Generator generator)
	{
		ArrayList<EnumMenuItem> exludedEnumMenuItems = new ArrayList<EnumMenuItem>();
		exludedEnumMenuItems.add(EnumMenuItem.ChancesMenuChance);
		
		Inventory menu = Lang.getMenuInventory(EnumMenuInventory.Chances).getInv(EnumMenuInventory.Chances, player, exludedEnumMenuItems);

		MenuItem generatorMenuItem = EnumMenuItem.ChancesMenuChance.getMenuItem();
		
		ArrayList<Integer> slotList = generatorMenuItem.getSlots();
		int lastId = -1;
		for (Entry<ItemStack, Double> e : generator.getChances().entrySet())
		{
			ItemStack item = e.getKey().clone();
			MenuItem chanceMenuItem = generatorMenuItem.clone();
			if (chanceMenuItem.getItemType().contains("<block>"))
				chanceMenuItem.setItemStack(item);

			chanceMenuItem.replace("<block_name>", Lang.getItemTypeName(item));
			chanceMenuItem.replace("<chance>", String.valueOf(generator.getChancePercent(item)));
			
			lastId++;
			
			ItemStack readyItem = chanceMenuItem.build();
			try {
				menu.setItem(slotList.get(lastId), readyItem);
			} catch (Exception e1) {
				Logger.error("Lang: There is probably more generators than slots set in /lang/gui/chances.block");
				Logger.error(e1);
			}
		}
		
		return menu;
	}
	
	@EventHandler
	public void onClick(final InventoryClickEvent e)
	{
		if(e.isCancelled()) return;
		if (!Menus.isVieving((Player) e.getWhoClicked(), EnumMenuInventory.Chances)) return;
		
		int slot = e.getSlot();
		
		if (EnumMenuItem.ChancesMenuBack.getMenuItem().getSlots().contains(slot))
		{
			Menus.openMainMenu((Player) e.getWhoClicked());
		}
		e.setCancelled(true);
	}
}
