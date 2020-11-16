package me.kryniowesegryderiusz.KGenerators;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;

import me.kryniowesegryderiusz.KGenerators.Utils.RandomSelector;
import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

public abstract class GenerateBlockFunction {
	
	//sprawdza tylko, czy moze zespawnic blok po czasie
	public static void generateBlock (Location location, Generator generator, int immediately) {
		KGenerators.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(KGenerators.getInstance(), new Runnable() {
			  public void run() {
				  
				  
				  ItemStack block = KGenerators.getBlocksUtils().getItemStackByBlock(location.getBlock());
				  ItemStack air = XMaterial.AIR.parseItem();
				  
				  switch (generator.getType()) {
				  case "single":
						if (!block.equals(generator.getGeneratorBlock()))
						{
							if (!block.equals(air) && !KGenerators.generatingWhitelist.contains(block) && !block.equals(generator.getPlaceholder())) {
								KGenerators.generatorsLocations.remove(location);
							  	GeneratorsFileManger.removeGeneratorFromFile(location);
							  
							  	location.getWorld().dropItem(location, generator.getGeneratorItem());
								return;
							}
						}
						if (!KGenerators.generatorsLocations.containsKey(location)) {
							return;
						}
						break;
					case "double":
						Location underLocation = new Location(location.getWorld(),location.getX(),location.getY()-1,location.getZ());
						if (!KGenerators.generatorsLocations.containsKey(underLocation)) {
							  return;
						  }
						
						if (!block.equals(air) && !KGenerators.generatingWhitelist.contains(block) && !block.equals(generator.getPlaceholder())) {
							KGenerators.generatorsLocations.remove(underLocation);
						  	GeneratorsFileManger.removeGeneratorFromFile(underLocation);
						  	location.getWorld().dropItem(location, generator.getGeneratorItem());
						  	KGenerators.getBlocksUtils().setBlock(underLocation, air);
							return;
						}
						break;
				  }
				  
				  ItemStack drawedBlock = drawBlock(generator.getChances());
				  
				  KGenerators.getBlocksUtils().setBlock(location, drawedBlock);
				  
				  if (KGenerators.dependencies.contains("SuperiorSkyblock2")) {
					  Island island = SuperiorSkyblockAPI.getGrid().getIslandAt(location);
					  if (island != null) {
						  island.handleBlockPlace(location.getBlock());
					  }
				  }
			}
		}, immediately * generator.getDelay() * 1L);
		
		if (immediately != 0 ) {
			generatePlaceholder(generator, location);
		}
	}
	
	public static void generatePlaceholder (Generator generator, Location location) {
		if (generator.getPlaceholder() != null && generator.getDelay() > 1) {
			KGenerators.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(KGenerators.getInstance(), new Runnable() {
				  public void run() {
					  ItemStack air = XMaterial.AIR.parseItem();
					  ItemStack m = KGenerators.getBlocksUtils().getItemStackByBlock(location.getBlock());
					  
					  if (m.equals(air) || generator.getChances().containsKey(m) || generator.getGeneratorBlock().equals(m)) {
						  KGenerators.getBlocksUtils().setBlock(location, generator.getPlaceholder());
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
