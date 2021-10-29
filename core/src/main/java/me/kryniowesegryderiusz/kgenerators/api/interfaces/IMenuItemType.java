package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;

public interface IMenuItemType {
	
	public <T extends Enum<T> & IMenuInventoryType> T getMenuInventory();
	public String getKey();
	public MenuItem getMenuItem();

}
