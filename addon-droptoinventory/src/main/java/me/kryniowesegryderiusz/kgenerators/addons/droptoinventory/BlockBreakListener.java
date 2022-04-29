package me.kryniowesegryderiusz.kgenerators.addons.droptoinventory;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.KGeneratorsAPI;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;

public class BlockBreakListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e)
    {
    	if (e.isCancelled() || !e.isDropItems())
    		return;
    	
    	if (e.getPlayer().getGameMode() == GameMode.CREATIVE)
    		return;
    	
    	Player p = e.getPlayer();
    	
    	IGeneratorLocation gLocation = KGeneratorsAPI.getGeneratorLocation(e.getBlock().getLocation());
    	
    	if (gLocation != null 
    			&& gLocation.getGeneratedBlockLocation().equals(e.getBlock().getLocation())) {
    		
    		if (!p.hasPermission("kgenerators.addons.droptoinventory.drop"))
    			return;

    		if (Main.getDependencies().isEnabled(Dependency.ITEMS_ADDER)) {
    			return;
    		}
    		
    		e.setDropItems(false);
    		
    		int exp = e.getExpToDrop();
    		e.setExpToDrop(0);
    		p.giveExp(exp);
    		
    		for (ItemStack item : e.getBlock().getDrops(p.getItemInHand(), p)){
    			KGeneratorsAddonDropToInventory.dropToEq(p, item);
    		}
    	}
    }
}
