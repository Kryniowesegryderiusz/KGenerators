package me.kryniowesegryderiusz.kgenerators.handlers;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.files.PlacedGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Players;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;

public class Place {
	
	/**
	 * Handle place of generator
	 * @param location
	 * @param generator
	 * @param player
	 * @return whether placing was successful
	 */
	public static boolean place (Location location, Generator generator, @Nullable Player player)
	{
    	Location aLocation = location.clone().add(0,1,0);
    	GeneratorLocation gLocation = new GeneratorLocation(generator.getId(), location, Players.getPlayer(player));
    	GeneratorPlayer pGenerator = Players.getPlayer(player);
    	
    	if (Main.getSettings().isWorldDisabled(location.getWorld()))
    	{
    		Lang.getMessageStorage().send(player, Message.GENERATORS_ANY_DISABLED_WORLD);
    		return false;
    	}
    	if (generator.isWorldDisabled(location.getWorld()))
    	{
    		Lang.getMessageStorage().send(player, Message.GENERATORS_ANY_DISABLED_WORLD_SPECIFIC, "<generator>", generator.getGeneratorItemName());
    		return false;
    	}
    	
    	if (!player.hasPermission("kgenerators.place."+generator.getId()))
    	{
    		Lang.getMessageStorage().send(player, Message.GENERATORS_PLACE_NO_PERMISSION,
    				"<permission>", "kgenerators.place."+generator.getId());
    		return false;
    	}
    	
    	if (!pGenerator.canPlace(gLocation))
	    {
	    	return false;
	    }
    	
    	if (generator.getType() == GeneratorType.DOUBLE && !Main.getBlocksUtils().isAir(aLocation.getBlock()))
    	{
    		if (player != null) Lang.getMessageStorage().send(player, Message.GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_BLOCK);
    		return false;
    	}
    	
    	if (generator.getType() == GeneratorType.DOUBLE && Locations.exists(aLocation))
    	{
    		if (player != null) Lang.getMessageStorage().send(player, Message.GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_GENERATOR);
    		return false;
    	}
    	
    	Locations.add(gLocation);
    	Main.getDb().savePlacedGenerator(gLocation);
    	if (!pGenerator.isNone()) pGenerator.addGeneratorToPlayer(generator);
    	if (player != null) Main.getSettings().getPlaceSound().play(player);
    	
    	if (player != null)
    		Logger.info(player.getName() + " placed " + generator.getId() + " in " + gLocation.toStringLocation());
    	else
    		Logger.info("Something placed " + generator.getId() + " in " + gLocation.toStringLocation());
    	
    	if (generator.getAfterPlaceWaitModifier() == 0)
    	{
    		Schedules.scheduleNow(gLocation);
    	}
    	else
    	{
    		Schedules.schedule(gLocation, true);
    	}
		return true;
	}

	/*public static boolean place(GeneratorLocation gLocation)
	{
		return true;
	}*/
	
}
