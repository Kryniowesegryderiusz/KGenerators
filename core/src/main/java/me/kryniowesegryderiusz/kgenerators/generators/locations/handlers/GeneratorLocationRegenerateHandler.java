package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.PostGeneratorRegenerationEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.PreGeneratorRegenerationEvent;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import com.cryptomorin.xseries.XMaterial;

public class GeneratorLocationRegenerateHandler {
	
	static Material pistonHead = XMaterial.PISTON_HEAD.get();
	
	public void handle(GeneratorLocation gLocation) {
		
		if (!gLocation.isReadyForRegeneration()) {
			if (gLocation.getGenerator().getDelay() == 0) {
				gLocation.getGenerator().setDelay(10);
				Logger.warn("GeneratorLocationRegenerateHandler: Generator " + gLocation.getGenerator().getId() + " has delay set to 0 and is not ready for regeneration. Changing generator delay to 10 to prevent infinite loop crashes.");
			}
			gLocation.scheduleGeneratorRegeneration();
			return;
		}
	
		Location generatingLocation = gLocation.getGeneratedBlockLocation();
		Block generatingLocationBlock = generatingLocation.getBlock();
		
		if (generatingLocationBlock.getType() == pistonHead) {
			gLocation.scheduleGeneratorRegeneration();
			return;
		}
		
		boolean isAir = Main.getMultiVersion().getBlocksUtils().isAir(generatingLocationBlock);
		boolean isOnWhitelist = Main.getMultiVersion().getBlocksUtils().isOnWhitelist(generatingLocationBlock);
		boolean isPlaceholder = gLocation.getGenerator().getPlaceholder() == null ? isAir : gLocation.getGenerator().getPlaceholder().getItem().equals(Main.getMultiVersion().getBlocksUtils().getItemStackByBlock(generatingLocationBlock));
		
		if (!isAir && !isOnWhitelist && !isPlaceholder) {
			Logger.debug("GeneratorLocationRegenerateHandler: Dropping generator " + gLocation.toString() + " | isAir: " + isAir + " | isOnWhitelist: " + isOnWhitelist + " | isPlaceholder: " + isPlaceholder + " | block: " + generatingLocationBlock.getType().toString());
			gLocation.removeGenerator(true, null);
			return;
		}
		
		PreGeneratorRegenerationEvent event = new PreGeneratorRegenerationEvent(gLocation);
		Main.getInstance().getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		AbstractGeneratedObject ago = gLocation.getGenerator().drawGeneratedObject();
		gLocation.setLastGeneratedObject(ago);
		ago.regenerate(gLocation);
		  
		Main.getInstance().getServer().getPluginManager().callEvent(new PostGeneratorRegenerationEvent(gLocation));
	}
}
