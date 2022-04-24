package me.kryniowesegryderiusz.kgenerators.lang.interfaces;

import me.kryniowesegryderiusz.kgenerators.gui.objects.MenuItem;

public interface IMenuItemType {
	
	public <T extends Enum<T> & IMenuInventoryType> T getMenuInventory();
	public String getKey();
	public MenuItem getMenuItem();

}
