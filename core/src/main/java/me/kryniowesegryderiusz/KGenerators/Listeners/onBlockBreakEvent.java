package me.kryniowesegryderiusz.KGenerators.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.GenerateBlockFunction;
import me.kryniowesegryderiusz.KGenerators.Generator;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManager;
import me.kryniowesegryderiusz.KGenerators.KGenerators;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;

public class onBlockBreakEvent implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void BlockBreakEvent(final BlockBreakEvent e){
		
		if (e.isCancelled()) {
			return;
		}
		
		final ItemStack block = KGenerators.getBlocksUtils().getItemStackByBlock(e.getBlock());
		
		final Location location = e.getBlock().getLocation();
		final Location bLocation = location.clone().add(0, -1, 0);
		
		final Generator generator = KGenerators.generators.get(KGenerators.generatorsLocations.get(location));
		final Generator bGenerator = KGenerators.generators.get(KGenerators.generatorsLocations.get(bLocation));
		
		
		if (bGenerator != null && bGenerator.getType().equals("double"))
		{			
			//czy wykopano blok, ktory jest generowany lub placholder
			if (bGenerator.getChances().containsKey(block) || block.equals(bGenerator.getPlaceholder())) {
				
				//jesli zniszczony zostal placeholder to cancel
				if (block.equals(bGenerator.getPlaceholder())) {
					e.setCancelled(true);
					return;
				}
				else
				{
					GenerateBlockFunction.generateBlock(location, bGenerator, 1);
					return;
				}
			}
		}

		if (generator != null && generator.getType().equals("double"))
		{
			if (generator.getGeneratorBlock().equals(block)) {

				checkIfPickingUp(e.getPlayer(), generator, location);
				
				e.setCancelled(true);
				return;
			}
			
		}
		

		if (generator != null && generator.getType().equals("single"))
		{
			
					
				//czy wykopano blok, ktory jest generowany lub sam generator lub placeholder
				if (generator.getChances().containsKey(block) || generator.getGeneratorBlock().equals(block) || block.equals(generator.getPlaceholder())) {
					
					if (checkIfPickingUp(e.getPlayer(), generator, location)) {
						e.setCancelled(true);
						return;
					}
					
					
					//czy placeholder
					if (block.equals(generator.getPlaceholder())) {
						e.setCancelled(true);
						return;
					}
					
					//blok generatora nie bedacy jednoczesnie generowanym blokiem
					if (block.equals(generator.getGeneratorBlock()) && !generator.getChances().containsKey(generator.getGeneratorBlock())) {
						
						return;
					}
				
					GenerateBlockFunction.generateBlock(location, generator, 1);
					return;
				}
		}
	}
	//jesli true to ma cancelowac event i konczyc funkcje
	boolean checkIfPickingUp(Player p, Generator generator, Location location) {
		if (!p.isSneaking()){
			if (generator.getType().equals("double")) {
				LangUtils.sendMessage(p, "generators.cant-break");
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			if (!p.hasPermission("kgenerators.pickupbypass") && KGenerators.dependencies.contains("WorldGuard") && !KGenerators.getWorldGuardUtils().worldGuardCheck(location, p))
			{
				LangUtils.sendMessage(p, "generators.cant-pick-up");
				return true;
			}
			else
			{
				GeneratorsManager.removeGenerator(generator, location, true);
				LangUtils.addReplecable("<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
				LangUtils.sendMessage(p, "generators.picked-up");
				return false;
			}
		}
	}
}
