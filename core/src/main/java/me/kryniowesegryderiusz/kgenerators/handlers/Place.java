package me.kryniowesegryderiusz.kgenerators.handlers;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.files.PlacedGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Players;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;

public class Place {
	
	public static boolean place (Location location, Generator generator, @Nullable Player player)
	{
    	Location aLocation = location.clone().add(0,1,0);
    	GeneratorLocation gLocation = new GeneratorLocation(generator.getId(), location, Players.getPlayer(player));
    	GeneratorPlayer pGenerator = Players.getPlayer(player);
    	
    	if (!pGenerator.canPlace(gLocation))
	    {
	    	return false;
	    }
    	
    	if (generator.getType() == GeneratorType.DOUBLE && !Main.getBlocksUtils().isAir(aLocation.getBlock()))
    	{
    		if (player != null) Lang.sendMessage(player, Message.GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_BLOCK);
    		return false;
    	}
    	
    	if (generator.getType() == GeneratorType.DOUBLE && Locations.exists(aLocation))
    	{
    		if (player != null) Lang.sendMessage(player, Message.GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_GENERATOR);
    		return false;
    	}
    	
    	Locations.add(gLocation);
    	PlacedGeneratorsFile.saveGeneratorToFile(location, player, generator.getId());
    	if (!pGenerator.isNone()) pGenerator.addGeneratorToPlayer(generator);
    	
    	
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
