package me.kryniowesegryderiusz.kgenerators.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMenuInventory;
import me.kryniowesegryderiusz.kgenerators.gui.GeneratorMenu;

public class MenuPlayer {
	
	@Getter
	Player player;
	@Getter
	Inventory inventory;
	@Getter
	EnumMenuInventory menuInventory;
	
	@Getter
	GeneratorLocation gLocation;
	
	public MenuPlayer(Player player, EnumMenuInventory menuInventory, Inventory inventory)
	{
		this.player = player;
		this.menuInventory = menuInventory;
		this.inventory = inventory;
	}
	
	public MenuPlayer(Player player, EnumMenuInventory menuInventory, Inventory inventory, GeneratorLocation gLocation)
	{
		this.player = player;
		this.menuInventory = menuInventory;
		this.inventory = inventory;
		this.gLocation = gLocation;
	}
	
	/**
	 * Updates and checks if player is no longer opening inventory
	 * @return false, if player is no longer viewing inventory
	 */
	public boolean update()
	{		
		if (this.inventory.getViewers().isEmpty()) return false;
		
		if (menuInventory == EnumMenuInventory.Generator)
			GeneratorMenu.update(this.inventory, this.player, this.gLocation);
		
		return true;
	}

}
