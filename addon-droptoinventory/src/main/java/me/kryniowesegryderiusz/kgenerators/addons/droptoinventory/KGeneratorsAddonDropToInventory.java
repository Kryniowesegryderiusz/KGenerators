package me.kryniowesegryderiusz.kgenerators.addons.droptoinventory;


import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.addons.objects.Addon;

public class KGeneratorsAddonDropToInventory extends JavaPlugin {
	
	@Getter private static KGeneratorsAddonDropToInventory instance;

    @Override
    public void onEnable() {

    	instance = this;

    	new Addon(this);
    	
    	this.getServer().getPluginManager().registerEvents(new BlockBreakListener(),  this);
    	
    }
    
    public static void dropToEq(Player p, ItemStack item)
    {
    	HashMap<Integer, ItemStack> left = p.getInventory().addItem(item);
		if (!left.isEmpty())
		{
			for (ItemStack i : left.values())
			{
				p.getLocation().getWorld().dropItem(p.getLocation(), i);
			}
		}
    }
    
}