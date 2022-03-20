package me.kryniowesegryderiusz.kgenerators.handlers;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.gui.Menus;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.enums.Action;
import me.kryniowesegryderiusz.kgenerators.enums.Interaction;
import me.kryniowesegryderiusz.kgenerators.enums.Message;

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
		Set<Entry<Action, GeneratorAction>> entryset = Main.getSettings().getActions().getEntrySet();
		
		if (gLocation.getGenerator().getActions() != null)
			entryset = gLocation.getGenerator().getActions().getEntrySet();
		
		for (Entry<Action, GeneratorAction> e : entryset)
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
						if (Schedules.timeLeft(gLocation) > 0)
						{
							Lang.getMessageStorage().send(player, Message.GENERATORS_TIME_LEFT_OUTPUT,
									"<time>", Schedules.timeLeftFormatted(gLocation));
						}
						break;
				}
			}
		}
		return false;
	}
}
