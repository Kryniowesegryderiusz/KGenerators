package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;

public interface IMenuItemType {
	
	public MenuInventoryType getMenuInventory();
	public String getKey();
	public MenuItem getMenuItem();

}
