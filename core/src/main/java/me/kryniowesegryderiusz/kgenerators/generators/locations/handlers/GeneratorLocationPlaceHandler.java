package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class GeneratorLocationPlaceHandler {
	
	/**
	 * Handle place of generator and saves it if successful
	 * @param GeneratorLocation not being in Manager nor Database yet
	 * @param Player - player placing generator
	 * @return whether placing was successful
	 */
	public boolean handle(GeneratorLocation gLocation, Player player)
	{    	
		Generator generator = gLocation.getGenerator();
		
    	if (gLocation.getGenerator().isWorldDisabled(gLocation.getGeneratedBlockLocation().getWorld()))
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
    	
    	if (!gLocation.getOwner().canPlace(gLocation))
	    {
	    	return false;
	    }
    	
    	if (generator.getType() == GeneratorType.DOUBLE && !Main.getMultiVersion().getBlocksUtils().isAir(gLocation.getGeneratedBlockLocation().getBlock()))
    	{
    		if (player != null) Lang.getMessageStorage().send(player, Message.GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_BLOCK);
    		return false;
    	}
    	
    	if (generator.getType() == GeneratorType.DOUBLE && Main.getLocations().get(gLocation.getGeneratedBlockLocation()) != null)
    	{
    		if (player != null) Lang.getMessageStorage().send(player, Message.GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_GENERATOR);
    		return false;
    	}

    	gLocation.save(true);
    	
    	if (player != null) Main.getSettings().getPlaceSound().play(player);
    	
    	if (player != null)
    		Logger.info(player.getName() + " placed " + generator.getId() + " in " + gLocation.toStringLocation());
    	else
    		Logger.info("Something placed " + generator.getId() + " in " + gLocation.toStringLocation());
    	
    	Main.getMultiVersion().getBlocksUtils().setBlock(gLocation.getGeneratedBlockLocation(), XMaterial.AIR.parseItem());
    	
    	if (gLocation.getGenerator().isGenerateImmediatelyAfterPlace())
    		gLocation.regenerateGenerator();
    	else
    		Main.getSchedules().schedule(gLocation, true);
		return true;
	}	
}
