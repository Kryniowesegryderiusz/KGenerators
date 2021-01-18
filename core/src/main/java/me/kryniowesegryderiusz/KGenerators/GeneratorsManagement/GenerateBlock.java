package me.kryniowesegryderiusz.KGenerators.GeneratorsManagement;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Utils.RandomSelector;
import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

public abstract class GenerateBlock {
	
	public static void schedule(Location location, Generator generator)
	{
		if (!Main.scheduledLocations.contains(location)) Main.scheduledLocations.add(location);
		Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			  public void run() {
				  if (Main.scheduledLocations.contains(location)) Main.scheduledLocations.remove(location);
				  generateBlock(location, generator);
			  }
		}, generator.getDelay() * 1L);
		generatePlaceholder(generator, location);
	}
	
	public static void now(Location location, Generator generator)
	{
		generateBlock(location, generator);
	}

	private static void generateBlock (Location location, Generator generator) {
		  ItemStack block = Main.getBlocksUtils().getItemStackByBlock(location.getBlock());
		  ItemStack pistonHead = XMaterial.PISTON_HEAD.parseItem();
		  
		  switch (generator.getType()) {
		  case "single":
			  if (!Main.generatorsLocations.containsKey(location)) {
					return;
				}
				if (!block.equals(generator.getGeneratorBlock()) && !Main.getBlocksUtils().isAir(location.getBlock()) && !Main.getBlocksUtils().isOnWhitelist(location.getBlock()) && !block.equals(generator.getPlaceholder()) && !generator.getChances().containsKey(block) && !block.getType().equals(pistonHead.getType())) {
					Remove.removeGenerator(Main.generatorsLocations.get(location), location, true);
					return;
				}
				break;
			case "double":
				Location underLocation = location.clone().add(0,-1,0);
				if (!Main.generatorsLocations.containsKey(underLocation)) {
					  return;
				}
				if (!Main.getBlocksUtils().isAir(location.getBlock()) && !Main.getBlocksUtils().isOnWhitelist(location.getBlock()) && !block.equals(generator.getPlaceholder()) && !generator.getChances().containsKey(block) && !block.getType().equals(pistonHead.getType())) {
					Remove.removeGenerator(Main.generatorsLocations.get(underLocation), underLocation, true);
					return;
				}
				break;
		  }
		  
		  ItemStack drawedBlock = drawBlock(generator.getChances());
		  
		  if (!generator.getChances().containsKey(block)) {
			  if (block.getType().equals(pistonHead.getType())) {
				  schedule(location, generator);
			  }
			  else
			  {
				  Main.getBlocksUtils().setBlock(location, drawedBlock);
			  }
		  }
		  
		  if (Main.dependencies.contains("SuperiorSkyblock2")) {
			  Island island = SuperiorSkyblockAPI.getGrid().getIslandAt(location);
			  if (island != null) {
				  island.handleBlockPlace(location.getBlock());
			  }
		  }
	}
	
	public static void generatePlaceholder (Generator generator, Location location) {
		if (generator.getPlaceholder() != null && generator.getDelay() > 1) {
			Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
				  public void run() {
					  ItemStack m = Main.getBlocksUtils().getItemStackByBlock(location.getBlock());
					  
					  if (!Main.getBlocksUtils().isAir(location.getBlock()) || generator.getChances().containsKey(m) || generator.getGeneratorBlock().equals(m)) {
						  Main.getBlocksUtils().setBlock(location, generator.getPlaceholder());
					  }
				  }
			}, 1L);
		}
	}
	
	static ItemStack drawBlock (HashMap<ItemStack, Double> blocks ) {
		
		Random random = new Random();
		
		RandomSelector<ItemStack> selector = RandomSelector.weighted(blocks.keySet(), s -> blocks.get(s));
		return selector.next(random);
	}
}
