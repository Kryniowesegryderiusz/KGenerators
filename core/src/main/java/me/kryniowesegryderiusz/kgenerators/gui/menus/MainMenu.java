package me.kryniowesegryderiusz.kgenerators.gui.menus;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class MainMenu {

	public static Inventory get(Player player, int page) {
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_GENERATOR);
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_LIMITS);
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_PAGE_PREVIOUS);
		exludedEnumMenuItems.add(MenuItemType.MAIN_MENU_PAGE_NEXT);

		Inventory menu = Lang.getMenuInventoryStorage().get(MenuInventoryType.MAIN).getInv(MenuInventoryType.MAIN,
				player, exludedEnumMenuItems);

		if (Main.getLimits().hasLimits() && Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).isEnabled()) {
			for (int i : Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).getSlots()) {
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).build(player));
			}
		}

		if (page > 0) {
			for (int i : Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_PREVIOUS).getSlots()) {
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_PREVIOUS).build(player));
			}
		}

		int nrOfGenerators = Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size();

		if (Main.getGenerators().getEntrySet().size() > (page + 1) * nrOfGenerators) {
			for (int i : Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_NEXT).getSlots()) {
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_NEXT).build(player));
			}
		}

		MenuItem generatorItem = Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR);
		ArrayList<Integer> slotList = generatorItem.getSlots();
		int lastId = -1;
		for (Entry<String, Generator> e : Main.getGenerators().getSpecifiedEntrySet(page * nrOfGenerators,
				nrOfGenerators)) {
			MenuItem generatorMenuItem = generatorItem.clone();
			Generator generator = e.getValue();

			if (generatorMenuItem.getItemType().contains("<generator>"))
				generatorMenuItem.setItemStack(generator.getGeneratorItem());

			generatorMenuItem.replace("<generator_name>", generator.getGeneratorItem().getItemMeta().getDisplayName());

			if (Main.getRecipes().get(generator) != null) {
				generatorMenuItem.addLore(Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.RECIPE));
			} else if (Main.getUpgrades().couldBeObtained(generator.getId())) {
				generatorMenuItem
						.addLore(Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.UPGRADE));
			}

			lastId++;
			ItemStack readyItem = generatorMenuItem.build(player);
			try {
				menu.setItem(slotList.get(lastId), readyItem);
			} catch (Exception e1) {
				Logger.error(
						"MainMenu: Something went wrong, while creating MainMenu index " + lastId + " on page " + page);
				Logger.error(e1);
			}
		}

		return menu;
	}

	public static void onClick(Player p, int slot, ClickType clickType) {
		if (Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_QUIT).getSlots().contains(slot)
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_QUIT).isEnabled()) {
			Main.getMenus().closeInv(p);
		} else if (Main.getLimits().hasLimits()
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).getSlots().contains(slot)
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_LIMITS).isEnabled()) {
			Main.getMenus().openLimitsMenu(p);
		} else if (Main.getMenus().getMenuPlayer(p).getPage() > 0
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_PREVIOUS).getSlots().contains(slot)
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_PREVIOUS).isEnabled()) {
			Main.getMenus().openMainMenu(p, Main.getMenus().getMenuPlayer(p).getPage() - 1);
		} else if (Main.getGenerators().getEntrySet().size() > (Main.getMenus().getMenuPlayer(p).getPage() + 1)
				* Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size()
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_NEXT).getSlots().contains(slot)
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_PAGE_NEXT).isEnabled()) {
			Main.getMenus().openMainMenu(p, Main.getMenus().getMenuPlayer(p).getPage() + 1);
		} else if (Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().contains(slot)
				&& Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).isEnabled()) {
			int lastId = -1;
			for (Entry<String, Generator> entry : Main.getGenerators().getSpecifiedEntrySet(
					Main.getMenus().getMenuPlayer(p).getPage()
							* Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size(),
					Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size())) {
				lastId++;
				if (Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size() == lastId)
					break;

				if (Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().get(lastId) == slot) {
					if (clickType == ClickType.LEFT)
						Main.getMenus().openChancesMenu(p, entry.getValue());
					else if (clickType == ClickType.RIGHT) {
						Generator generator = entry.getValue();
						if (Main.getRecipes().get(generator) != null) {
							Main.getMenus().openRecipeMenu(p, generator);
						} else if (Main.getUpgrades().couldBeObtained(generator.getId())) {
							Main.getMenus().openUpgradeMenu(p, generator);
						}
					}
				}
			}
		}
	}
}
