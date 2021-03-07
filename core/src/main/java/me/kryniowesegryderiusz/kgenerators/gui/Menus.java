package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.MenuPlayer;

public class Menus implements Listener {
	
	static HashMap<Player, MenuPlayer> guis = new HashMap<Player, MenuPlayer>();
	
	public static MenuPlayer getMenuPlayer(Player p)
	{
		return guis.get(p);
	}
	
	public static void openGeneratorMenu(Player p, GeneratorLocation gLocation)
	{
		Inventory menu = GeneratorMenu.get(p, gLocation);
		guis.put(p, new MenuPlayer(p, EnumMenuInventory.Generator, menu, gLocation));
		p.openInventory(menu);
	}
	
	static void everyFreq()
	{
		ArrayList<Player> toRemove = new ArrayList<Player>();
		for (Entry<Player, MenuPlayer> e : guis.entrySet())
		{
			if (!e.getValue().update())
				toRemove.add(e.getKey());
		}
		
		for (Player p : toRemove)
		{
			guis.remove(p);
		}
	}
	
	public static void setup()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
		    @Override
		    public void run() {
		    	everyFreq();
		    }
		}, 0L, Main.getSettings().getGuiUpdateFrequency()*1L);
	}
	
	static void closeInv(Player p)
	{
		Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			@Override
            public void run() {
				p.closeInventory();
            }
		});
	}

	public static void openMainMenu(Player p) {
		Inventory menu = MainMenu.get(p);
		guis.put(p, new MenuPlayer(p, EnumMenuInventory.Main, menu, null));
		p.openInventory(menu);
	}
	
	public static void openChancesMenu(Player p, Generator generator) {
		Inventory menu = ChancesMenu.get(p, generator);
		guis.put(p, new MenuPlayer(p, EnumMenuInventory.Chances, menu, null));
		p.openInventory(menu);
	}
	
	public static void openRecipeMenu(Player p, Generator generator) {
		Inventory menu = RecipeMenu.get(p, generator);
		guis.put(p, new MenuPlayer(p, EnumMenuInventory.Recipe, menu, null));
		p.openInventory(menu);
	}
	
	public static void openUpgradeMenu(Player p, Generator generator) {
		Inventory menu = UpgradeMenu.get(p, generator);
		guis.put(p, new MenuPlayer(p, EnumMenuInventory.Upgrade, menu, null));
		p.openInventory(menu);
	}
	
	public static boolean isVieving(Player p, EnumMenuInventory enumMenuInventory)
	{
		if (guis.containsKey(p) && guis.get(p).getMenuInventory() == enumMenuInventory)
			return true;
		return false;
	}
	
	public static void closeAll()
	{
		for (Entry<Player, MenuPlayer> e : guis.entrySet())
		{
			e.getKey().closeInventory();
		}
	}

}
