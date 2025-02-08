package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.FactionsUUIDHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.LandsHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.PlotSquaredHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.WorldGuardHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.ActionType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.InteractionType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;

public class GeneratorLocationActionHandler {

	/**
	 * @return true if event should be cancelled
	 */
	public boolean handle(GeneratorLocation gLocation, InteractionType usedActionType, Player player) {
		
		Set<Entry<ActionType, GeneratorAction>> entryset = Main.getSettings().getActions().getEntrySet();

		if (gLocation.getGenerator().getActions() != null)
			entryset = gLocation.getGenerator().getActions().getEntrySet();

		for (Entry<ActionType, GeneratorAction> e : entryset) {
			
			if (e.getValue().requirementsMet(usedActionType, player)) {
				
				if (!WorldGuardHook.isPlayerAllowedToInteract(player, gLocation.getLocation())
						|| !PlotSquaredHook.isPlayerAllowedToInteract(player, gLocation.getLocation())
						|| !LandsHook.isPlayerAllowedToInteract(player, gLocation.getLocation())
						|| !FactionsUUIDHook.isPlayerAllowedToInteract(player, gLocation.getLocation())) {
						
					Lang.getMessageStorage().send(player, Message.GENERATORS_ACTION_CANT_HERE);
					
					return true;
				}
				
				ActionType action = e.getKey();
				switch (action) {
				case PICKUP:
					gLocation.pickUpGenerator(player);
					return true;
				case OPENGUI:
					Main.getMenus().openGeneratorMenu(player, gLocation);
					break;
				case TIMELEFT:
					if (Main.getSchedules().timeLeft(gLocation) > 0) {
						Lang.getMessageStorage().send(player, Message.GENERATORS_TIME_LEFT_OUTPUT, "<time>",
								Main.getSchedules().timeLeftFormatted(gLocation));
					}
					break;
				case UPGRADE:
					if (gLocation.getGenerator().getUpgrade() != null)
						gLocation.getGenerator().getUpgrade().blockUpgrade(gLocation, player);
					else
						Lang.getMessageStorage().send(player, Message.UPGRADES_NO_NEXT_LEVEL);
					break;
				}
			}
		}
		return false;
	}
}
