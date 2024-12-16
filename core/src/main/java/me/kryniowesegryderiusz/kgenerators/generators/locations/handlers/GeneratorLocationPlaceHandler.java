package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.PostGeneratorPlaceEvent;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.FactionsUUIDHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.LandsHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.PlotSquaredHook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratorLocationPlaceHandler {
	
	/**
	 * Handle place of generator and saves it if successful
	 * @param GeneratorLocation not being in Manager nor Database yet
	 * @param sender - player (CommandSender) placing generator
	 * @param generateGeneratorBlock - 
	 * @return whether placing was successful
	 */
	public boolean handle(GeneratorLocation gLocation, CommandSender sender, boolean generateGeneratorBlock) {    	
		Generator generator = gLocation.getGenerator();
		
		/*
		 * Pre checks
		 */
		
    	if (gLocation.getGenerator().isWorldDisabled(gLocation.getGeneratedBlockLocation().getWorld())) {
    		Lang.getMessageStorage().send(sender, Message.GENERATORS_ANY_DISABLED_WORLD_SPECIFIC, "<generator>", generator.getGeneratorItemName());
    		return false;
    	}
    	
    	if (!sender.hasPermission("kgenerators.place."+generator.getId())) {
    		Lang.getMessageStorage().send(sender, Message.GENERATORS_PLACE_NO_PERMISSION,
    				"<permission>", "kgenerators.place."+generator.getId());
    		return false;
    	}
    	
    	if (!gLocation.getOwner().canPlace(gLocation)) {
	    	return false;
	    }
    	
    	if (generator.getType() == GeneratorType.DOUBLE 
    			&& !Main.getMultiVersion().getBlocksUtils().isAir(gLocation.getGeneratedBlockLocation().getBlock())
    			&& !Main.getMultiVersion().getBlocksUtils().isOnWhitelist(gLocation.getGeneratedBlockLocation().getBlock())) {
    		Lang.getMessageStorage().send(sender, Message.GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_BLOCK);
    		return false;
    	}
    	
    	if (generator.getType() == GeneratorType.DOUBLE && Main.getPlacedGenerators().getLoaded(gLocation.getGeneratedBlockLocation()) != null) {
    		Lang.getMessageStorage().send(sender, Message.GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_GENERATOR);
    		return false;
    	}
    	
    	if (sender instanceof Player)
    		if (!PlotSquaredHook.isPlayerAllowedToPlace((Player) sender, gLocation.getLocation())
    			|| !LandsHook.isPlayerAllowedToPlace((Player) sender, gLocation.getLocation())
    			|| !FactionsUUIDHook.isPlayerAllowedToPlace((Player) sender, gLocation.getLocation())) {
    			Lang.getMessageStorage().send(sender, Message.GENERATORS_PLACE_CANT_HERE);
    			return false;
    		}
    	
    	/*
    	 * Placing
    	 */

    	Main.getDatabases().getDb().saveGenerator(gLocation);
    	Main.getPlacedGenerators().addLoaded(gLocation);
    	
    	if (sender instanceof Player) gLocation.getOwner().addGeneratorToPlayer(gLocation.getGenerator());
    	
    	if (sender instanceof Player) Main.getSettings().getPlaceSound().play((Player) sender);
    	
    	if (sender instanceof Player)
    		Logger.debugPlayer(((Player) sender).getName() + " placed " + generator.getId() + " in " + gLocation.toStringLocation());
    	else
    		Logger.debugPlayer("Something placed " + generator.getId() + " in " + gLocation.toStringLocation());
    	
    	if (generateGeneratorBlock && gLocation.getGenerator().getType() == GeneratorType.DOUBLE)
    		Main.getMultiVersion().getBlocksUtils().setBlock(gLocation.getLocation(), gLocation.getGenerator().getGeneratorItem());
    	
    	Main.getMultiVersion().getBlocksUtils().setBlock(gLocation.getGeneratedBlockLocation(), Material.AIR);
    	
    	/*
    	if (gLocation.getGenerator().isGenerateImmediatelyAfterPlace())
    		gLocation.regenerateGenerator();
    	else
    	*/
    	
    	Main.getSchedules().schedule(gLocation, true);
    	
    	Main.getInstance().getServer().getPluginManager().callEvent(new PostGeneratorPlaceEvent(gLocation, sender));
    	
		return true;
	}
}
