package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers;

import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.mechanics.Mechanic;
import me.kryniowesegryderiusz.kgenerators.dependencies.objects.GeneratedOraxenBlock;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.GeneratedBlock;
import me.kryniowesegryderiusz.kgenerators.utils.objects.CustomBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.PostGeneratorRegenerationEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.PreGeneratorRegenerationEvent;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class GeneratorLocationRegenerateHandler {

    static Material pistonHead = XMaterial.PISTON_HEAD.parseMaterial();

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
            Logger.debugPlacedGeneratorsManager("GeneratorLocationRegenerateHandler: Dropping generator " + gLocation.toString() + " | isAir: " + isAir + " | isOnWhitelist: " + isOnWhitelist + " | isPlaceholder: " + isPlaceholder);
            gLocation.removeGenerator(true, null);
            return;
        }

        PreGeneratorRegenerationEvent event = new PreGeneratorRegenerationEvent(gLocation);
        Main.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        AbstractGeneratedObject blockToGenerate = gLocation.getBlockToGenerate();
        if(blockToGenerate != null){
              blockToGenerate.regenerate(gLocation);
        }

        Main.getInstance().getServer().getPluginManager().callEvent(new PostGeneratorRegenerationEvent(gLocation));
    }
}
