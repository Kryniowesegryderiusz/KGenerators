package me.kryniowesegryderiusz.kgenerators.handlers;

import java.util.Map.Entry;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.gui.Menus;
import me.kryniowesegryderiusz.kgenerators.enums.Action;
import me.kryniowesegryderiusz.kgenerators.enums.Interaction;

public class ActionHandler {
	
	/**
	 * 
	 * @param usedActionType
	 * @param gLocation
	 * @param player
	 * @return true if event should be cancelled
	 */
	public static boolean handler(Interaction usedActionType, GeneratorLocation gLocation, Player player)
	{		
		for (Entry<Action, GeneratorAction> e : Main.getSettings().getActions().entrySet())
		{
			if (e.getValue().requirementsMet(usedActionType, player))
			{
				Action action = e.getKey();
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
