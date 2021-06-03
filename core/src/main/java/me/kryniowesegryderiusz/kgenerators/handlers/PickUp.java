package me.kryniowesegryderiusz.kgenerators.handlers;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.enums.WGFlag;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.managers.Players;

public class PickUp {
	
	/*
	 * Always should cancel event!
	 */
	
	public static void pickup(Player p, GeneratorLocation gLocation) {
		
		Generator generator = gLocation.getGenerator();
		
		if (!Players.getPlayer(p).canPickUp(gLocation))
		{
			return;
		}
		
		if ((Main.dependencies.contains(Dependency.WorldGuard) && !p.hasPermission("kgenerators.bypass.worldguard") && !Main.getWorldGuardUtils().worldGuardFlagCheck(gLocation.getLocation(), p, WGFlag.PICK_UP))
				|| (Main.dependencies.contains(Dependency.BentoBox) && !p.hasPermission("kgenerators.bypass.bentobox") && !BentoBoxHook.isAllowed(p, BentoBoxHook.Type.PICKUP_FLAG)))
		{
			Lang.sendMessage(p, Message.GENERATORS_PICK_UP_CANT_HERE);
			return;
		}
		
		Remove.removeGenerator(gLocation, true);
		Lang.addReplecable("<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
		Lang.sendMessage(p, Message.GENERATORS_PICK_UP_SUCCESFULL);
		return;
	}
}
