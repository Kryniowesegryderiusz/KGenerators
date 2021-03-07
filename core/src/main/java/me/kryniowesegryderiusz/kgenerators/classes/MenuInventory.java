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
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuItem;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class MenuInventory {
	
	@Getter
	String name = "";
	@Getter
	short slots = 27;
	
	public MenuInventory(String name, int slots)
	{
		this.name = ChatColor.translateAlternateColorCodes('&', name);
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
	public Inventory getInv(EnumMenuInventory menuInventory, Player player, ArrayList<EnumMenuItem> exludedEnumMenuItems, String... replecables)
	{
		ArrayList<String> rep = new ArrayList<>(Arrays.asList(replecables));
		
		Inventory menu = Bukkit.createInventory(player, this.slots, this.name);
		
		for(EnumMenuItem enumMenuItem : EnumMenuItem.values())
		{
			if (enumMenuItem.getMenuInventory() == menuInventory &&  !exludedEnumMenuItems.contains(enumMenuItem))
			{
				MenuItem menuItem = Lang.getMenuItem(enumMenuItem);

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
		
		return menu;
	}
	
	public Inventory getInv(EnumMenuInventory menuInventory, Player player, String... replecables)
	{
		return getInv(menuInventory, player, null, replecables);
	}
	
	public void load(EnumMenuInventory menu, Config config) {
		String path = menu.getKey();
		if (config.contains(path))
		{
			if (config.contains(path+".name")) this.name = ChatColor.translateAlternateColorCodes('&', config.getString(path+".name"));
			if (config.contains(path+".slots")) this.slots = (short) config.getInt(path+".slots");
		}
		else
		{
			config.set(path+".name", this.name);
			config.set(path+".slots", this.slots);
		}
	}
}
