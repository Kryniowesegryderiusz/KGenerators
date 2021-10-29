package me.kryniowesegryderiusz.kgenerators.enums;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMenuItemType;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;

public enum MenuItemType implements IMenuItemType
{
	GENERATOR_MENU_FILLER(MenuInventoryType.GENERATOR, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44")),
	GENERATOR_MENU_TIME_LEFT(MenuInventoryType.GENERATOR,"time-left", new MenuItem("CLOCK", "&aTime left for regeneration", false, "21" , "&a<time>")),
	GENERATOR_MENU_OWNER(MenuInventoryType.GENERATOR,"owner", new MenuItem("BOOK", "&aGenerator owner", false, "22", "&e<owner>")),
	GENERATOR_MENU_PICK_UP(MenuInventoryType.GENERATOR,"pick-up", new MenuItem("BARRIER", "&aPick up", false, "23", "&eClick here to pick up generator")),
	GENERATOR_MENU_QUIT(MenuInventoryType.GENERATOR,"quit", new  MenuItem("ARROW", "&cQuit", false, "40", "&6Quit menu")),
	GENERATOR_MENU_RESET(MenuInventoryType.GENERATOR,"reset", new  MenuItem("MUSIC_DISC_11", "&cReset generator", false, "13", "&6This generator seems to be currupted!", "&6Click here to repair him")),
	GENERATOR_MENU_UPGRADE(MenuInventoryType.GENERATOR,"upgrade", new  MenuItem("EXPERIENCE_BOTTLE", "&cUpgrade generator", false, "31", "&6Click here to upgrade this generator!", "", "&6Next stage: &e<next_generator>", "&6Costs:" ,"<costs>")),
	GENERATOR_MENU_UPGRADE_MAXED(MenuInventoryType.GENERATOR,"upgrade-maxed", new  MenuItem("EXPERIENCE_BOTTLE", "&cMaximum generator level", false, "31", "&6This generator doesnt have", "&6any further upgrades!","&aYou've maxed it!")),
	
	MAIN_MENU_FILLER(MenuInventoryType.MAIN, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44")),
	MAIN_MENU_GENERATOR(MenuInventoryType.MAIN,"generator", new MenuItem("<generator>", "&a<generator_name>", false, "9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35" , "&6Left click:", "&eCheck generator chances")),
	MAIN_MENU_LIMITS(MenuInventoryType.MAIN,"limits", new MenuItem("PLAYER_HEAD", "&aLimits", false, "8", "&eClick here to check limits")),
	MAIN_MENU_QUIT(MenuInventoryType.MAIN,"quit", new  MenuItem("BARRIER", "&cQuit", false, "40", "&6Quit menu")),
	MAIN_MENU_PAGE_PREVIOUS(MenuInventoryType.MAIN,"page.previous", new  MenuItem("ARROW", "&cPrevious page", false, "38", "&6Click to back", "&6to previous page")),
	MAIN_MENU_PAGE_NEXT(MenuInventoryType.MAIN,"page.next", new  MenuItem("ARROW", "&cNext page", false, "42", "&6Click to go", "&6to next page")),
	
	CHANCES_MENU_FILLER(MenuInventoryType.CHANCES, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44")),
	CHANCES_MENU_CHANCE(MenuInventoryType.CHANCES,"block", new MenuItem("<block>", "&a<block_name>", false, "9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26" , "&6Chance: &e<chance>%")),
	CHANCES_MENU_BACK(MenuInventoryType.CHANCES,"back", new  MenuItem("ARROW", "&cBack", false, "40", "&6Get back to", "&cprevious page")),
	
	RECIPE_MENU_FILLER(MenuInventoryType.RECIPE, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44")),
	RECIPE_MENU_INGREDIENS(MenuInventoryType.RECIPE,"ingredients", new MenuItem("<block>", "&a<block_name>", false, "10,11,12,19,20,21,28,29,30")),
	RECIPE_MENU_RESULT(MenuInventoryType.RECIPE,"result", new MenuItem("<generator>", "&a<generator_name>", false, "25")),
	RECIPE_MENU_MARKER(MenuInventoryType.RECIPE,"marker", new MenuItem("CRAFTING_TABLE", "&aCrafting", false, "14,23,32")),
	RECIPE_MENU_BACK(MenuInventoryType.RECIPE,"back", new  MenuItem("ARROW", "&cBack", false, "40", "&6Get back to", "&cprevious page")),
	
	UPGRADE_MENU_FILLER(MenuInventoryType.UPGRADE, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44")),
	UPGRADE_MENU_INGREDIENT(MenuInventoryType.UPGRADE,"ingredient", new MenuItem("<generator>", "&a<generator_name>", false, "20")),
	UPGRADE_MENU_RESULT(MenuInventoryType.UPGRADE,"result", new MenuItem("<generator>", "&a<generator_name>", false, "24")),
	UPGRADE_MENU_MARKER(MenuInventoryType.UPGRADE,"marker", new MenuItem("EXPERIENCE_BOTTLE", "&aUpgrade", false, "13,22,31", "&6To upgrade get", "&6generator and type", "&e/kgenerators upgrade", "&6or use generator menu", "", "&6Costs:", "<costs>")),
	UPGRADE_MENU_BACK(MenuInventoryType.UPGRADE,"back", new  MenuItem("ARROW", "&cBack", false, "40", "&6Get back to", "&cprevious page")),
	
	LIMITS_MENU_FILLER(MenuInventoryType.LIMITS, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44")),
	LIMITS_MENU_LIMIT(MenuInventoryType.LIMITS,"limit", new MenuItem("<limit_display_item>", "&a<limit_name>", false, "9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26" , "&6You've placed &e<amount>&6/&e<limit>&6 of", "<generators_list>", "<is_only_owner_use>", "<is_only_owner_pick_up>")),
	LIMITS_MENU_BACK(MenuInventoryType.LIMITS,"back", new  MenuItem("ARROW", "&cBack", false, "40", "&6Get back to", "&cprevious page")),

	;
	@Getter
	MenuInventoryType menuInventory;
	String subKey;
	@Getter
	MenuItem menuItem;
	
	MenuItemType(MenuInventoryType menuInventory, String subKey, MenuItem menuItem) {
		this.menuInventory = menuInventory;
		this.subKey = subKey;
		this.menuItem = menuItem;
	}
	
	public String getKey()
	{
		return this.menuInventory.getKey() + "." + this.subKey;
	}
}