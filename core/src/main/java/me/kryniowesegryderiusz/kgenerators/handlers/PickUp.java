package me.kryniowesegryderiusz.kgenerators.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumWGFlags;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;

public class PickUp {
	
	/*
	 * Always should cancel event!
	 */
	
	public static void pickup(Player p, GeneratorLocation gLocation) {
		
		Generator generator = gLocation.getGenerator();
		
		if (!PerPlayerGenerators.canPickUp(p, gLocation))
		{
			return;
		}
		
		if (Main.dependencies.contains("WorldGuard") && !p.hasPermission("kgenerators.bypass.worldguard") && !Main.getWorldGuardUtils().worldGuardFlagCheck(gLocation, p, EnumWGFlags.PICK_UP))
		{
			Lang.sendMessage(p, EnumMessage.GeneratorsPickUpCantHere);
			return;
		}
		
		Remove.removeGenerator(gLocation, true);
		Lang.addReplecable("<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
		Lang.sendMessage(p, EnumMessage.GeneratorsPickUpSuccesful);
		return;
	}
}
