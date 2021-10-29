package me.kryniowesegryderiusz.kgenerators.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.MenuPlayer;
import me.kryniowesegryderiusz.kgenerators.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;

public class Menus implements Listener {
	
	public static HashMap<Player, MenuPlayer> guis = new HashMap<Player, MenuPlayer>();
	
	public static MenuPlayer getMenuPlayer(Player p)
	{
		return guis.get(p);
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
	
	public static void closeInv(Player p)
	{
		Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			@Override
            public void run() {
				p.closeInventory();
            }
		});
	}
	
	
	public static void openGeneratorMenu(Player p, GeneratorLocation gLocation)
	{
		if (!BentoBoxHook.isAllowed(p, BentoBoxHook.Type.OPEN_MENU_FLAG) || !SuperiorSkyblock2Hook.isAllowed(p, SuperiorSkyblock2Hook.Type.OPEN_MENU_FLAG))
		{
			Lang.getMessageStorage().send(p, Message.GENERATOR_MENU_CANT_OPEN_HERE);
			return;
		}
		
		Inventory menu = GeneratorMenu.get(p, gLocation);
		guis.put(p, new MenuPlayer(p, MenuInventoryType.GENERATOR, menu, gLocation));
		p.openInventory(menu);
	}
	
	/**
	 * Opens main menu on first page
	 * @param player
	 */
	public static void openMainMenu(Player p) {
		openMainMenu(p, 0);
	}
	
	/**
	 * Opens main menu page containing that generator
	 * @param player
	 */
	public static void openMainMenu(Player p, Generator g) {
		int nr = 0;
		for (Entry<String, Generator> e : Generators.getEntrySet())
		{
			if (e.getValue() == g)
			{
				openMainMenu(p, (int) Math.floor(nr/Lang.getMenuItemStorage().get(MenuItemType.MAIN_MENU_GENERATOR).getSlots().size()));
			}
			nr ++;
		}
	}
	
	/**
	 * Opens main menu on specified page
	 * @param player
	 * @param page (starts from 0)
	 */
	public static void openMainMenu(Player p, int page) {
		Inventory menu = MainMenu.get(p, page);
		guis.put(p, new MenuPlayer(p, MenuInventoryType.MAIN, menu, page));
		p.openInventory(menu);
	}
	
	public static void openChancesMenu(Player p, Generator generator) {
		Inventory menu = ChancesMenu.get(p, generator);
		guis.put(p, new MenuPlayer(p, MenuInventoryType.CHANCES, menu, generator));
		p.openInventory(menu);
	}
	
	public static void openRecipeMenu(Player p, Generator generator) {
		Inventory menu = RecipeMenu.get(p, generator);
		guis.put(p, new MenuPlayer(p, MenuInventoryType.RECIPE, menu, generator));
		p.openInventory(menu);
	}
	
	public static void openUpgradeMenu(Player p, Generator generator) {
		Inventory menu = UpgradeMenu.get(p, generator);
		guis.put(p, new MenuPlayer(p, MenuInventoryType.UPGRADE, menu, generator));
		p.openInventory(menu);
	}
	
	public static void openLimitsMenu(Player p) {
		Inventory menu = LimitsMenu.get(p);
		guis.put(p, new MenuPlayer(p, MenuInventoryType.LIMITS, menu));
		p.openInventory(menu);
	}
	
	public static <T extends Enum<T> & IMenuInventoryType> boolean isVieving(Player p, T enumMenuInventory)
	{
		if (guis.containsKey(p) && guis.get(p).getMenuInventory() == enumMenuInventory)
			return true;
		return false;
	}
	
	public static <T extends Enum<T> & IMenuInventoryType> T getVieving(Player p)
	{
		if (guis.containsKey(p))
			return (T) guis.get(p).getMenuInventory();
		return null;
	}
	
	public static <T extends Enum<T> & IMenuInventoryType> Player isSomeoneVieving(T enumMenuInventory, GeneratorLocation gLocation)
	{
		for (Entry<Player, MenuPlayer> e : guis.entrySet())
		{
			if (e.getValue().getMenuInventory() == enumMenuInventory && e.getValue().getGLocation() == gLocation)
				return e.getKey();
		}
		return null;
	}
	
	public static void closeAll()
	{
		for (Entry<Player, MenuPlayer> e : guis.entrySet())
		{
			e.getKey().closeInventory();
		}
	}

}
