package me.kryniowesegryderiusz.kgenerators.addons.superiorskyblock2forcehook;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.bgsoftware.superiorskyblock.api.events.PluginInitializeEvent;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;

import me.kryniowesegryderiusz.kgenerators.addons.objects.Addon;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class KGeneratorsAddonSuperiorSkyblock2ForceHook extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
    	
    	new Addon(this);
    	
    	if (this.getServer().getPluginManager().getPlugin("KGenerators") == null) {
    		this.getServer().getLogger().warning("KGenerators is not installed! There is no point of having this plugin installed!");
    		return;
    	}
    	
    	if (this.getServer().getPluginManager().getPlugin("SuperiorSkyblock2") != null) {
    		this.getServer().getPluginManager().registerEvents(this, this);
    	} else Logger.error("KGeneratorsAddon: SuperiorSkyblock2ForceHook: You dont use SuperiorSkyblock2! This addon is not required!");
    }
    
    @EventHandler
    public void onPluginInit(PluginInitializeEvent e){
   	 try {
   		 IslandPrivilege.register("KGENERATORS_PICKUP_FLAG");
		 IslandPrivilege.register("KGENERATORS_USE_FLAG");
		 IslandPrivilege.register("KGENERATORS_OPEN_MENU_FLAG");
		 Logger.debugPluginLoad("KGeneratorsAddon: SuperiorSkyblock2ForceHook: Initialised SuperiorSkyblock2 permissions");
		 this.getServer().getScheduler().runTask(this, new Runnable() {
			public void run() {
				SuperiorSkyblock2Hook.forceHook();
			}
		 });
	} catch (IllegalArgumentException e1) {
		Logger.error("KGeneratorsAddon: SuperiorSkyblock2ForceHook: Cannot register SuperiorSkyblock2 permissions!");
		Logger.error(e);
	}
    }
}
