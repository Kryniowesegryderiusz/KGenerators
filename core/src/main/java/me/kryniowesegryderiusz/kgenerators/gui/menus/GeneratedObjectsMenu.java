package me.kryniowesegryderiusz.kgenerators.gui.menus;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.objects.StringContent;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratedObjectsMenu {
	
	public static Inventory get(Player player, Generator generator)
	{
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.GENERATED_OBJECTS_MENU_GENERATED_OBJECT);
		
		Inventory menu = Lang.getMenuInventoryStorage().get(MenuInventoryType.GENERATED_OBJECTS).getInv(MenuInventoryType.GENERATED_OBJECTS, player, exludedEnumMenuItems);

		MenuItem generatorMenuItem = Lang.getMenuItemStorage().get(MenuItemType.GENERATED_OBJECTS_MENU_GENERATED_OBJECT);
		
		ArrayList<Integer> slotList = generatorMenuItem.getSlots();
		int lastId = -1;
		for (Entry<AbstractGeneratedObject, Double> e : generator.getChancesEntryset())
		{
			AbstractGeneratedObject ago = e.getKey();
			MenuItem chanceMenuItem = generatorMenuItem.clone();
			
			if (chanceMenuItem.getItemType().contains("<generated_object>"))
				chanceMenuItem.setItemStack(ago.getGuiItem());

			ItemStack agoGuiItem = ago.getGuiItem();
			
			if (agoGuiItem.hasItemMeta() && agoGuiItem.getItemMeta().hasDisplayName())
				chanceMenuItem.replace("<generated_object_name>", agoGuiItem.getItemMeta().getDisplayName());
			else
				chanceMenuItem.replace("<generated_object_name>", Lang.getCustomNamesStorage().getItemTypeName(agoGuiItem));
			
			if(agoGuiItem.hasItemMeta() && agoGuiItem.getItemMeta().hasLore())
				chanceMenuItem.replaceLore("<generated_object_lore>", new StringContent().addLines((ArrayList<String>) agoGuiItem.getItemMeta().getLore()));
			else
				chanceMenuItem.replaceLore("<generated_object_lore>", new StringContent());
			
			if(ago.getType().contains("item"))
				chanceMenuItem.replaceLore("<generated_object_type>", Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.GENERATED_OBJECT_TYPE_ITEM));
			else if(ago.getType().contains("block"))
				chanceMenuItem.replaceLore("<generated_object_type>", Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.GENERATED_OBJECT_TYPE_BLOCK));
			else if(ago.getType().contains("entity"))
				chanceMenuItem.replaceLore("<generated_object_type>", Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.GENERATED_OBJECT_TYPE_ENTITY));
			else chanceMenuItem.replaceLore("<generated_object_type>", new StringContent());
			
			chanceMenuItem.replace("<chance>", generator.getChancePercentFormatted(ago));
			
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
	
	public static void onClick(Player p, int slot)
	{		
		if (Lang.getMenuItemStorage().get(MenuItemType.GENERATED_OBJECTS_MENU_BACK).getSlots().contains(slot) && Lang.getMenuItemStorage().get(MenuItemType.GENERATED_OBJECTS_MENU_BACK).isEnabled())
		{
			Main.getMenus().openMainMenu(p, Main.getMenus().getMenuPlayer(p).getGenerator());
		}
	}
}
