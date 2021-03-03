package me.kryniowesegryderiusz.kgenerators.multiversion;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;

public class MultiVersion {
	
	private static short minor;
	
	public static void setup()
	{
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
    	
    	if (minor == 8) {
    		recipesPackage = packageName + ".RecipesLoader_1_8";
    		blocksPackage = packageName + ".BlocksUtils_1_8";
    		wgPackage = packageName + ".WorldGuardUtils_1_8";
    		actionBarPackage = packageName + ".ActionBar_1_8";
    	} 
    	else if (minor == 9 || minor == 10 || minor == 11)
    	{
    		recipesPackage = packageName + ".RecipesLoader_1_8";
    		blocksPackage = packageName + ".BlocksUtils_1_8";
    		wgPackage = packageName + ".WorldGuardUtils_1_8";
    		actionBarPackage = packageName + ".ActionBar_1_9";
    	}
    	else if (minor == 12)
    	{
    		recipesPackage = packageName + ".RecipesLoader_1_12";
    		blocksPackage = packageName + ".BlocksUtils_1_8";
    		wgPackage = packageName + ".WorldGuardUtils_1_8";
    		actionBarPackage = packageName + ".ActionBar_1_9";
    	}
    	else
    	{
    		recipesPackage = packageName + ".RecipesLoader_1_13";
    		blocksPackage = packageName + ".BlocksUtils_1_13";
    		wgPackage = packageName + ".WorldGuardUtils_1_13";
    		actionBarPackage = packageName + ".ActionBar_1_9";
    	}
    	
    	try {
			Main.setRecipesLoader((RecipesLoader) Class.forName(recipesPackage).newInstance());
			Main.setBlocksUtils((BlocksUtils) Class.forName(blocksPackage).newInstance());
			if (Main.getInstance().getServer().getPluginManager().getPlugin("WorldGuard") != null) {
				WorldGuardUtils worldGuardUtils = (WorldGuardUtils) Class.forName(wgPackage).newInstance();
	    		worldGuardUtils.worldGuardFlagsAdd();
	    		Main.setWorldGuardUtils(worldGuardUtils);
	    	}
			Main.setActionBar((ActionBar) Class.forName(actionBarPackage).newInstance());
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e3) {
			Logger.error(e3);
		}
	}
	
	public static boolean isHigher(int minorVersion)
	{
		if (minor > minorVersion) return true;
		else return false;
	}
}
