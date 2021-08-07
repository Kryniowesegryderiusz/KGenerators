package me.kryniowesegryderiusz.kgenerators.handlers;

import org.bukkit.entity.Player;

import com.iridium.iridiumcore.dependencies.xseries.XSound;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.enums.WGFlag;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Players;

public class PickUp {
	
	/*
	 * Always should cancel event!
	 */
	
	public static void pickup(Player p, GeneratorLocation gLocation) {
		
		Generator generator = gLocation.getGenerator();
		
		if (!Locations.exists(gLocation.getLocation()))
		{
			Lang.sendMessage(p, Message.GENERATORS_ANY_NO_LONGER_THERE);
			return;
		}
		
    	if (!p.hasPermission("kgenerators.pickup."+generator.getId()))
    	{
    		Lang.addReplecable("<permission>", "kgenerators.pickup."+generator.getId());
    		Lang.sendMessage(p, Message.GENERATORS_PICK_UP_NO_PERMISSION);
    		return;
    	}
		
		if (!Players.getPlayer(p).canPickUp(gLocation))
		{
			return;
		}
		
		if ((Main.dependencies.contains(Dependency.WORLD_GUARD) && !p.hasPermission("kgenerators.bypass.worldguard") && !Main.getWorldGuardUtils().worldGuardFlagCheck(gLocation.getLocation(), p, WGFlag.PICK_UP))
				|| !BentoBoxHook.isAllowed(p, BentoBoxHook.Type.PICKUP_FLAG) 
				|| !SuperiorSkyblock2Hook.isAllowed(p, SuperiorSkyblock2Hook.Type.PICKUP_FLAG))
		{
			Lang.sendMessage(p, Message.GENERATORS_PICK_UP_CANT_HERE);
			return;
		}
		
		Remove.removeGenerator(gLocation, p);
		Logger.info(p.getName() + " picked up " + gLocation.toString());
		Lang.addReplecable("<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
		Lang.sendMessage(p, Message.GENERATORS_PICK_UP_SUCCESFULL);
		
		Main.getSettings().getPickupSound().play(p);
		return;
	}
}
