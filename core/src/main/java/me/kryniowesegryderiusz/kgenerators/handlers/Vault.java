package me.kryniowesegryderiusz.kgenerators.handlers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.kryniowesegryderiusz.kgenerators.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

public class Vault {
	
	private static Economy econ = null;
	
	private static Permission perms = null;

	
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
    
    public static boolean setupPermissions() {
    	if (Main.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
    	
        RegisteredServiceProvider<Permission> rsp = Main.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public static boolean hasPermission(OfflinePlayer offPlayer, String perm)
    {
    	return perms.playerHas(Bukkit.getWorlds().get(0).getName(), offPlayer, perm);
    }
    
    public static String formatMoney(double money)
    {
    	return econ.format(money);
    }

}
