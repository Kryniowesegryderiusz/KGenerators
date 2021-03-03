package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
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
	 * @param replecables String, by String "key", "value"
	 * @return
	 */
	public Inventory getInv(EnumMenuInventory menuInventory, Player player, Object object, String... replecables)
	{
		ArrayList<String> rep = new ArrayList<>(Arrays.asList(replecables));
		
		Inventory menu = Bukkit.createInventory(player, this.slots, this.name);
		
		for(EnumMenuItem enumMenuItem : EnumMenuItem.values())
		{
			if (enumMenuItem.getMenuInventory() == menuInventory)
			{
				MenuItem menuItem = Lang.getMenuItem(enumMenuItem);
				if (enumMenuItem != EnumMenuItem.ChancesListMenuGenerator && enumMenuItem != EnumMenuItem.ChancesSpecificMenuChance)
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
				else
				{
					if (enumMenuItem == EnumMenuItem.ChancesListMenuGenerator)
					{
						ArrayList<Integer> slotList = menuItem.getSlots();
						int lastId = -1;
						for (Entry<String, Generator> e : Generators.getEntrySet())
						{
							MenuItem generatorMenuItem = menuItem.clone();
							Generator generator = e.getValue();
							
							if (generatorMenuItem.getItemType().contains("<generator>"))
								generatorMenuItem.setItemStack(generator.getGeneratorItem());
							
							generatorMenuItem.replace("<generator_name>", generator.getGeneratorItem().getItemMeta().getDisplayName());
							
							lastId++;
							ItemStack readyItem = generatorMenuItem.build();
							try {
								menu.setItem(slotList.get(lastId), readyItem);
							} catch (Exception e1) {
								Logger.error("Lang: There is probably more generators than slots set in /lang/gui/chances.list.Generator");
								Logger.error(1);
							}
						}
					}
					else if (enumMenuItem == EnumMenuItem.ChancesSpecificMenuChance)
					{
						Generator generator = (Generator) object;
						
						ArrayList<Integer> slotList = menuItem.getSlots();
						int lastId = -1;
						for (Entry<ItemStack, Double> e : generator.getChances().entrySet())
						{
							ItemStack item = e.getKey().clone();
							double chance = e.getValue();
							MenuItem chanceMenuItem = menuItem.clone();
							if (chanceMenuItem.getItemType().contains("<block>"))
								chanceMenuItem.setItemStack(item);
							String type = item.getType().toString();
							type = type.toLowerCase().replaceAll("_", " ");
							type = type.substring(0, 1).toUpperCase() + type.substring(1);
							chanceMenuItem.replace("<block_name>", type);
							chanceMenuItem.replace("<chance>", String.valueOf(generator.getChancePercent(item)));
							
							lastId++;
							
							ItemStack readyItem = chanceMenuItem.build();
							try {
								System.out.println();
								menu.setItem(slotList.get(lastId), readyItem);
							} catch (Exception e1) {
								Logger.error("Lang: There is probably more generators than slots set in /lang/gui/chances.specific.block");
								Logger.error(e1);
							}
						}
					}
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
