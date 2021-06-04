package me.kryniowesegryderiusz.kgenerators.enums;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.classes.MenuInventory;

public enum MenuInventoryType implements IMenuInventoryType
{
	GENERATOR("generator", new MenuInventory("&9Generator info", 45)),
	
	MAIN("main", new MenuInventory("&9Generators", 45)),
	CHANCES("chances", new MenuInventory("&9Blocks chances", 45)),
	RECIPE("recipe", new MenuInventory("&9Recipe", 45)),
	UPGRADE("upgrade", new MenuInventory("&9Upgrade", 45)),
	LIMITS("limits", new MenuInventory("&9Limits", 45)),
	;

	@Getter
	String key;
	@Getter
	MenuInventory menuInventory;
	MenuInventoryType(String key, MenuInventory menuInventory) {
		this.key = key;
		this.menuInventory = menuInventory;
	}
}