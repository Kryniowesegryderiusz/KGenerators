package me.kryniowesegryderiusz.kgenerators.handlers;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.PostBlockGenerationEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.PreBlockGenerationEvent;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.utils.RandomSelector;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class GenerateBlock {
	
	/*
	 * @param location Generator Location
	 */
	
	static ItemStack pistonHead = XMaterial.PISTON_HEAD.parseItem();
	
	public static void generate(GeneratorLocation gLocation) {
			
		Generator generator = gLocation.getGenerator();
		Location location = gLocation.getLocation();
		
		PreBlockGenerationEvent event = new PreBlockGenerationEvent(gLocation);
		Main.getInstance().getServer().getPluginManager().callEvent(event);
		
		if (event.isCancelled()) return;
	
		Location diggableBlockLocation = location.clone();
		if (generator.getType() == GeneratorType.DOUBLE) diggableBlockLocation.add(0,1,0);
		
		Block diggableBlock = diggableBlockLocation.getBlock();
		ItemStack diggableBlockItemStack = Main.getBlocksUtils().getItemStackByBlock(diggableBlock);
		
		if (!Locations.exists(location)
				&& !diggableBlockItemStack.equals(generator.getGeneratorBlock()) 
				&& !Main.getBlocksUtils().isAir(diggableBlock) 
				&& !Main.getBlocksUtils().isOnWhitelist(diggableBlock) 
				&& !diggableBlockItemStack.equals(generator.getPlaceholder()) 
				&& !generator.getChances().containsKey(diggableBlockItemStack) 
				&& !diggableBlock.getType().equals(pistonHead.getType())) {
			Remove.removeGenerator(location, true);
			return;
		}
		  
		  ItemStack drawedBlock = drawBlock(generator.getChances());
		  
		  if (!generator.getChances().containsKey(diggableBlockItemStack)) {
			  if (diggableBlock.getType().equals(pistonHead.getType())) {
				  Schedules.schedule(gLocation);
			  }
			  else
			  {
				  Main.getBlocksUtils().setBlock(diggableBlockLocation, drawedBlock);
			  }
		  }
		  
		  if (Main.dependencies.contains(Dependency.SuperiorSkyblock2)) {
			  Island island = SuperiorSkyblockAPI.getGrid().getIslandAt(diggableBlockLocation);
			  if (island != null) {
				  island.handleBlockPlace(diggableBlock);
			  }
		  }
		  
		Main.getInstance().getServer().getPluginManager().callEvent(new PostBlockGenerationEvent(gLocation));
	}
	
	public static void generatePlaceholder (GeneratorLocation gLocation) {
		Generator generator = gLocation.getGenerator();
		Location location = gLocation.getLocation().clone();
		if (generator.getType() == GeneratorType.DOUBLE) location.add(0,1,0);
		if (generator.getPlaceholder() != null) {
			Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
				  ItemStack m = Main.getBlocksUtils().getItemStackByBlock(location.getBlock());
				  
				  if (Main.getBlocksUtils().isAir(location.getBlock()) || generator.getChances().containsKey(m) || generator.getGeneratorBlock().equals(m)) {
					  Main.getBlocksUtils().setBlock(location, generator.getPlaceholder());
				  }
			});
		}
	}
	
	static ItemStack drawBlock (HashMap<ItemStack, Double> blocks ) {
		
		Random random = new Random();
		
		RandomSelector<ItemStack> selector = RandomSelector.weighted(blocks.keySet(), s -> blocks.get(s));
		return selector.next(random);
	}
}
