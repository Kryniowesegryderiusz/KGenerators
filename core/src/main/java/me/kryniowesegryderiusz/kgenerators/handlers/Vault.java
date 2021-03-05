package me.kryniowesegryderiusz.kgenerators.handlers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.kryniowesegryderiusz.kgenerators.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Vault {
	
	private static Economy econ = null;
	
    public static boolean setupEconomy() {
        if (Main.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Main.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    public static boolean takeMoney(Player player, Double cost)
    {
    	EconomyResponse r = econ.withdrawPlayer(player, cost);
    	
    	if (r.transactionSuccess()) return true;
    	else return false;
    }

}
