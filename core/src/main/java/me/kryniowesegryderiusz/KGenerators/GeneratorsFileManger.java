package me.kryniowesegryderiusz.KGenerators;

import java.io.IOException;

import org.bukkit.Location;

import me.kryniowesegryderiusz.KGenerators.Utils.Config;

public abstract class GeneratorsFileManger {
	
	public static void saveGeneratorToFile(Location location, String generatorID){
		Config file = KGenerators.getPluginGeneratorsFile();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world  = location.getWorld().getName();
		file.set("placedGenerators."+world+","+x+","+y+","+z+".generatorID", generatorID);
		
		try {
			file.saveConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void removeGeneratorFromFile(Location location){
		Config file = KGenerators.getPluginGeneratorsFile();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world  = location.getWorld().getName();
		
		file.set("placedGenerators."+world+","+x+","+y+","+z+".generatorID", null);
		file.set("placedGenerators."+world+","+x+","+y+","+z, null);
		
		try {
			file.saveConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
