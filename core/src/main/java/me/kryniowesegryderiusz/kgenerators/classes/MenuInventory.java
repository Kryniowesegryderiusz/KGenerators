package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class MenuInventory {
	
	@Getter
	String name = "";
	@Getter
	short slots = 27;
	
	public MenuInventory(String name, int slots)
	{
		this.name = Main.getChatUtils().colorize(name);;
		this.slots = (short) slots;
	}
	
	/**
	 * Builds inventory and fills it with items
	 * @param menuInventory
	 * @param player
	 * @param exludedEnumMenuItem
	 * @param replecables String, by String "key", "value"
	 * @return
	 */
	public Inventory getInv(MenuInventoryType menuInventory, Player player, ArrayList<MenuItemType> exludedEnumMenuItems, String... replecables)
	{
		ArrayList<String> rep = new ArrayList<>(Arrays.asList(replecables));
		
		
		String newName = this.name;
		for(int i = 0; i < rep.size(); i=i+2)
		{
			newName = newName.replace(rep.get(i), rep.get(i+1));
		}
		
		Inventory menu = Bukkit.createInventory(player, this.slots, newName);
		
		for(MenuItemType enumMenuItem : MenuItemType.values())
		{
			if (enumMenuItem.getMenuInventory() == menuInventory &&  !exludedEnumMenuItems.contains(enumMenuItem))
			{
				MenuItem menuItem = Lang.getMenuItem(enumMenuItem);
				if (menuItem.isEnabled())
				{
					for(int i = 0; i < rep.size(); i=i+2)
					{
						menuItem.replace(rep.get(i), rep.get(i+1));
					}
					
					ItemStack item = menuItem.build();
					
					for (int i : menuItem.getSlots())
					{
						menu.setItem(i, item);
					}
				}
			}
		}
		
		return menu;
	}
	
	public Inventory getInv(MenuInventoryType menuInventory, Player player, String... replecables)
	{
		return getInv(menuInventory, player, null, replecables);
	}
	
	public void load(MenuInventoryType menu, Config config) {
		String path = menu.getKey();
		if (config.contains(path))
		{
			if (config.contains(path+".name")) this.name = Main.getChatUtils().colorize(config.getString(path+".name"));
			if (config.contains(path+".slots")) this.slots = (short) config.getInt(path+".slots");
		}
		else
		{
			config.set(path+".name", this.name);
			config.set(path+".slots", this.slots);
		}
	}
}
