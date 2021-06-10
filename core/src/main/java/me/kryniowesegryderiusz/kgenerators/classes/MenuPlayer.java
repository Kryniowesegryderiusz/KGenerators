package me.kryniowesegryderiusz.kgenerators.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.gui.GeneratorMenu;

public class MenuPlayer {
	
	@Getter
	Player player;
	@Getter
	Inventory inventory;
	@Getter
	MenuInventoryType menuInventory;
	
	/*
	 * Additional info
	 */
	@Getter
	GeneratorLocation gLocation;
	@Getter 
	int page;
	@Getter 
	Generator generator;
	
	/**
	 * Standard menu, without any additional info
	 * @param player
	 * @param menuInventory
	 * @param inventory
	 */
	public MenuPlayer(Player player, MenuInventoryType menuInventory, Inventory inventory)
	{
		this.player = player;
		this.menuInventory = menuInventory;
		this.inventory = inventory;
	}
	
	/**
	 * Used for specified menus, like Chances, Recipe, Upgrade
	 * @param player
	 * @param menuInventory
	 * @param generator
	 */
	public MenuPlayer(Player player, MenuInventoryType menuInventory, Inventory inventory, Generator generator)
	{
		this.player = player;
		this.menuInventory = menuInventory;
		this.inventory = inventory;
		this.generator = generator;
	}
	
	/**
	 * Used for paginated menus like Main
	 * @param player
	 * @param menuInventory
	 * @param inventory
	 * @param page
	 */
	public MenuPlayer(Player player, MenuInventoryType menuInventory, Inventory inventory, int page)
	{
		this.player = player;
		this.menuInventory = menuInventory;
		this.inventory = inventory;
		this.page = page;
	}
	
	/**
	 * Used for menus linked to specific location
	 * @param player
	 * @param menuInventory
	 * @param inventory
	 * @param gLocation
	 */
	public MenuPlayer(Player player, MenuInventoryType menuInventory, Inventory inventory, GeneratorLocation gLocation)
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
		
		if (menuInventory == MenuInventoryType.GENERATOR)
			GeneratorMenu.update(this.inventory, this.player, this.gLocation);
		
		return true;
	}

}
