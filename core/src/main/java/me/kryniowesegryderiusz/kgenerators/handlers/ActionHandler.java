package me.kryniowesegryderiusz.kgenerators.handlers;

import java.util.Map.Entry;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.gui.GeneratorMenu;
import me.kryniowesegryderiusz.kgenerators.gui.Menus;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumAction;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumInteraction;

public class ActionHandler {
	
	public static boolean handler(EnumInteraction usedActionType, GeneratorLocation gLocation, Player player)
	{
		for (Entry<EnumAction, GeneratorAction> e : Main.getSettings().getActions().entrySet())
		{
			if (e.getValue().requirementsMet(usedActionType, player))
			{
				EnumAction action = e.getKey();
				switch (action)
				{
					case PICKUP:
						PickUp.pickup(player, gLocation);
						return true;
					case OPENGUI:
						Menus.openGeneratorMenu(player, gLocation);
						break;
					case TIMELEFT:
						break;
				}
			}
		}
		return false;
	}
}
