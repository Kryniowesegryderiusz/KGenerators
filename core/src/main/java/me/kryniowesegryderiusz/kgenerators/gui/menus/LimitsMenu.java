package me.kryniowesegryderiusz.kgenerators.gui.menus;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.Limit;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.PlayerLimits;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.objects.StringContent;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class LimitsMenu {

	public static Inventory get(Player player) {
		ArrayList<MenuItemType> exludedEnumMenuItems = new ArrayList<MenuItemType>();
		exludedEnumMenuItems.add(MenuItemType.LIMITS_MENU_LIMIT);
		exludedEnumMenuItems.add(MenuItemType.LIMITS_MENU_BACK);

		Inventory menu = Lang.getMenuInventoryStorage().get(MenuInventoryType.LIMITS).getInv(MenuInventoryType.LIMITS,
				player, exludedEnumMenuItems);

		if (Main.getMenus().getMenuPlayer(player) != null) {
			for (int i : Lang.getMenuItemStorage().get(MenuItemType.LIMITS_MENU_BACK).getSlots()) {
				menu.setItem(i, Lang.getMenuItemStorage().get(MenuItemType.LIMITS_MENU_BACK).build(player));
			}
		}

		ArrayList<Integer> slotList = Lang.getMenuItemStorage().get(MenuItemType.LIMITS_MENU_LIMIT).getSlots();
		int lastId = -1;

		GeneratorPlayer gp = Main.getPlayers().getPlayer(player);
		PlayerLimits pl = new PlayerLimits(player);

		for (Limit limit : Main.getLimits().getValues()) {
			ItemStack item = limit.getItem().clone();
			
			if (item.getType() == Material.PLAYER_HEAD)
				item = Main.getMultiVersion().getSkullUtils().itemFromName(player.getName());
			
			MenuItem chanceMenuItem = Lang.getMenuItemStorage().get(MenuItemType.LIMITS_MENU_LIMIT);
			if (chanceMenuItem.getItemType().contains("<limit_display_item>"))
				chanceMenuItem.setItemStack(item);

			chanceMenuItem.replace("<limit_name>", limit.getName());
			chanceMenuItem.replace("<amount>", String.valueOf(limit.getPlacedGenerators(gp)));
			
			if (pl.getLimit(limit) == -1)
				chanceMenuItem.replace("<limit>", "∞");
			else
				chanceMenuItem.replace("<limit>", String.valueOf(pl.getLimit(limit)));

			if (limit.getGenerators().size() != Main.getGenerators().getEntrySet().size()) {
				ArrayList<String> generators = new ArrayList<String>();
				for (Generator g : limit.getGenerators()) {
					StringContent s = Lang.getMenuItemAdditionalLinesStorage()
							.get(MenuItemAdditionalLines.LIMITS_GENERATOR_LIST);
					s.replace("<generator_name>", g.getGeneratorItemName());
					generators.addAll(s.getLines());
				}
				chanceMenuItem.replaceLore("<generators_list>", new StringContent(generators));
			} else {
				chanceMenuItem.replaceLore("<generators_list>",
						Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.LIMITS_ALL_GENERATORS));
			}

			if (limit.isOnlyOwnerUse())
				chanceMenuItem.replaceLore("<is_only_owner_use>",
						Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.LIMITS_ONLY_OWNER_USE));
			else
				chanceMenuItem.replaceLore("<is_only_owner_use>", null);

			if (limit.isOnlyOwnerPickUp())
				chanceMenuItem.replaceLore("<is_only_owner_pick_up>", Lang.getMenuItemAdditionalLinesStorage()
						.get(MenuItemAdditionalLines.LIMITS_ONLY_OWNER_PICK_UP));
			else
				chanceMenuItem.replaceLore("<is_only_owner_pick_up>", null);

			lastId++;

			ItemStack readyItem = chanceMenuItem.build(player);
			try {
				menu.setItem(slotList.get(lastId), readyItem);
			} catch (Exception e1) {
				Logger.error("Lang: There is probably more limits than slots set in /lang/gui/limits.limit");
				Logger.error(e1);
			}
		}

		return menu;
	}

	public static void onClick(Player p, int slot, ItemStack currentItem) {
		if (Lang.getMenuItemStorage().get(MenuItemType.LIMITS_MENU_BACK).getSlots().contains(slot)
				&& Lang.getMenuItemStorage().get(MenuItemType.LIMITS_MENU_BACK).isEnabled()
				&& currentItem.equals(Lang.getMenuItemStorage().get(MenuItemType.LIMITS_MENU_BACK).build(p))) {
			Main.getMenus().openMainMenu(p);
		}
	}
}
