package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.WGFlag;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratorLocationPickUpHandler {
	
	/**
	 * Remember that this should always cancel event!
	 * @param player
	 * @param GeneratorLocation
	 */
	public void handle(GeneratorLocation gLocation, Player p) {
		
		Generator generator = gLocation.getGenerator();
		
		if (!Main.getPlacedGenerators().isLoaded(gLocation)) {
			Lang.getMessageStorage().send(p, Message.GENERATORS_ANY_NO_LONGER_THERE);
			return;
		}
		
    	if (!p.hasPermission("kgenerators.pickup."+generator.getId())) {
    		Lang.getMessageStorage().send(p, Message.GENERATORS_PICK_UP_NO_PERMISSION,
    				"<permission>", "kgenerators.pickup."+generator.getId());
    		return;
    	}
		
		if (!Main.getPlayers().getPlayer(p).canPickUp(gLocation)) {
			return;
		}
		
		if ((Main.getDependencies().isEnabled(Dependency.WORLD_GUARD) && !p.hasPermission("kgenerators.bypass.worldguard") && !Main.getMultiVersion().getWorldGuardUtils().worldGuardFlagCheck(gLocation.getLocation(), p, WGFlag.PICK_UP))
				|| !BentoBoxHook.isAllowed(p, BentoBoxHook.Type.PICKUP_FLAG, gLocation.getLocation()) 
				|| !SuperiorSkyblock2Hook.isAllowed(p, SuperiorSkyblock2Hook.Type.PICKUP_FLAG, gLocation.getLocation())) {
			Lang.getMessageStorage().send(p, Message.GENERATORS_PICK_UP_CANT_HERE);
			return;
		}
		
		gLocation.removeGenerator(true, p);
		Logger.info(p.getName() + " picked up " + gLocation.toString());
		Lang.getMessageStorage().send(p, Message.GENERATORS_PICK_UP_SUCCESFULL,
				"<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
		
		Main.getSettings().getPickupSound().play(p);
		return;
	}
}
