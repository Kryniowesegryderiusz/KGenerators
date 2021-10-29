package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMenuItemType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
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
	public <T extends Enum<T> & IMenuInventoryType, T2 extends Enum<T2> & IMenuItemType> Inventory getInv(T menuInventory, Player player, ArrayList<T2> exludedEnumMenuItems, String... replecables)
	{
		ArrayList<String> rep = new ArrayList<>(Arrays.asList(replecables));
		
		
		String newName = this.name;
		for(int i = 0; i < rep.size(); i=i+2)
		{
			newName = newName.replace(rep.get(i), rep.get(i+1));
		}
		
		Inventory menu = Bukkit.createInventory(player, this.slots, newName);
		
		for(Enum<?> en : Lang.getMenuItemStorage().getAllEnums())
		{
			T2 enumMenuItem = (T2) en;
			if (enumMenuItem.getMenuInventory() == menuInventory &&  !exludedEnumMenuItems.contains(enumMenuItem))
			{
				MenuItem menuItem = Lang.getMenuItemStorage().get(enumMenuItem);
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
	
	public <T extends Enum<T> & IMenuInventoryType> Inventory getInv(T menuInventory, Player player, String... replecables)
	{
		return getInv(menuInventory, player, null, replecables);
	}
	
	public <T extends Enum<T> & IMenuInventoryType> void load(T en, Config config) {
		String path = en.getKey();
		this.load(path, config);
	}
	
	public void load (String path, Config config)
	{
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
