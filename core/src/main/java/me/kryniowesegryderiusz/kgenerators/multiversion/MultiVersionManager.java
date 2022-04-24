package me.kryniowesegryderiusz.kgenerators.multiversion;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.ActionBar;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.BlocksUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.ChatUtils;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.RecipesLoader;
import me.kryniowesegryderiusz.kgenerators.multiversion.interfaces.WorldGuardUtils;

public class MultiVersionManager {
	
	private short minor;
	
	@Getter private RecipesLoader recipesLoader;
	@Getter private BlocksUtils blocksUtils;
	@Getter private WorldGuardUtils worldGuardUtils;
	@Getter private ActionBar actionBar;
	@Getter private ChatUtils chatUtils;
	
	public MultiVersionManager() {
    	try {
    		String serverVersion = Main.getInstance().getServer().getVersion();
    		serverVersion = serverVersion.split("MC: ")[1];
    		serverVersion = serverVersion.split("\\)")[0];
    		String[] serverVersionSplitted = serverVersion.split("\\.");
    		minor = Short.valueOf(serverVersionSplitted[1]);
    		
        	String packageName = Main.class.getPackage().getName() + ".multiversion";
        	
        	String recipesPackage;
        	String blocksPackage;
        	String wgPackage;
        	String actionBarPackage;
        	String chatPackage;
        	
        	if (minor == 8) {
        		recipesPackage = packageName + ".RecipesLoader_1_8";
        		blocksPackage = packageName + ".BlocksUtils_1_8";
        		wgPackage = packageName + ".WorldGuardUtils_1_8";
        		actionBarPackage = packageName + ".ActionBar_1_8";
        		chatPackage = packageName + ".ChatUtils_1_8";
        	} 
        	else if (minor == 9 || minor == 10 || minor == 11)
        	{
        		recipesPackage = packageName + ".RecipesLoader_1_8";
        		blocksPackage = packageName + ".BlocksUtils_1_8";
        		wgPackage = packageName + ".WorldGuardUtils_1_8";
        		actionBarPackage = packageName + ".ActionBar_1_9";
        		chatPackage = packageName + ".ChatUtils_1_8";
        	}
        	else if (minor == 12)
        	{
        		recipesPackage = packageName + ".RecipesLoader_1_12";
        		blocksPackage = packageName + ".BlocksUtils_1_8";
        		wgPackage = packageName + ".WorldGuardUtils_1_8";
        		actionBarPackage = packageName + ".ActionBar_1_9";
        		chatPackage = packageName + ".ChatUtils_1_8";
        	}
        	else if (minor == 12 || minor == 13 || minor == 14 || minor == 15)
        	{
        		recipesPackage = packageName + ".RecipesLoader_1_13";
        		blocksPackage = packageName + ".BlocksUtils_1_13";
        		wgPackage = packageName + ".WorldGuardUtils_1_13";
        		actionBarPackage = packageName + ".ActionBar_1_9";
        		chatPackage = packageName + ".ChatUtils_1_8";
        	}
        	else
        	{
        		recipesPackage = packageName + ".RecipesLoader_1_13";
        		blocksPackage = packageName + ".BlocksUtils_1_13";
        		wgPackage = packageName + ".WorldGuardUtils_1_13";
        		actionBarPackage = packageName + ".ActionBar_1_9";
        		chatPackage = packageName + ".ChatUtils_1_16";
        	}
        	
        	try {
    			this.recipesLoader = (RecipesLoader) Class.forName(recipesPackage).newInstance();
    			this.blocksUtils = (BlocksUtils) Class.forName(blocksPackage).newInstance();
    			
    			if (Main.getInstance().getServer().getPluginManager().getPlugin("WorldGuard") != null) {
    				this.worldGuardUtils = (WorldGuardUtils) Class.forName(wgPackage).newInstance();
    				this.worldGuardUtils.worldGuardFlagsAdd();
    	    	}
    			
    			this.actionBar = (ActionBar) Class.forName(actionBarPackage).newInstance();
    			this.chatUtils = (ChatUtils) Class.forName(chatPackage).newInstance();
    			
    		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e3) {
    			Logger.error(e3);
    		}
		} catch (Exception e) {
			Logger.error(e);
		}   
	}
	
	public boolean isHigher(int minorVersion)
	{
		if (minor > minorVersion) return true;
		else return false;
	}
}
