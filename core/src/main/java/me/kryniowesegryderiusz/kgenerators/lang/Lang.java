package me.kryniowesegryderiusz.kgenerators.lang;

import java.io.IOException;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemType;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.enums.HologramText;
import me.kryniowesegryderiusz.kgenerators.enums.MenuInventoryType;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class Lang {

	@Getter private static MessageStorage messageStorage;
	
	@Getter private static MenuInventoryStorage menuInventoryStorage;
	@Getter private static MenuItemStorage menuItemStorage;
	@Getter private static MenuItemAdditionalLinesStorage menuItemAdditionalLinesStorage;
	
	@Getter private static HologramTextStorage hologramTextStorage;
	@Getter private static CustomNamesStorage customNamesStorage;
	
	public static void setup(Config langFile, Config guiLangFile, Config customNamesLangFile) throws IOException 
	{
		messageStorage = new MessageStorage(langFile);
		messageStorage.register(Message.class);
		
		menuInventoryStorage = new MenuInventoryStorage(guiLangFile);
		menuInventoryStorage.register(MenuInventoryType.class);
		menuItemStorage = new MenuItemStorage(guiLangFile);
		menuItemStorage.register(MenuItemType.class);
		menuItemAdditionalLinesStorage = new MenuItemAdditionalLinesStorage(guiLangFile);
		menuItemAdditionalLinesStorage.register(MenuItemAdditionalLines.class);
		
		hologramTextStorage = new HologramTextStorage(langFile);
		hologramTextStorage.register(HologramText.class);
		customNamesStorage = new CustomNamesStorage(customNamesLangFile);
	}
}
