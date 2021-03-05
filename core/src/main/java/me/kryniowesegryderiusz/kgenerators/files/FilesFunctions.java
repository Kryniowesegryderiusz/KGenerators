package me.kryniowesegryderiusz.kgenerators.files;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;

public class FilesFunctions {
    
	static void mkdir(String dir){
		File file = new File(Main.getInstance().getDataFolder()+"/"+dir);
		
		  if (file.exists()) {
			  return;
		  }
		
	      boolean bool = file.mkdir();
	      if(!bool){
	         Logger.error("FilesFunctions: Can not create directory for "+dir);
	      }
	}
	
	static String locationToString(Location location)
	{
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		String world  = location.getWorld().getName();
		String string = world + "," + x + "," + y + "," + z;
		
		return string;
	}
	
	static Location stringToLocation (String string)
	{
		String[] parts = string.split(",");

        final World w = Bukkit.getServer().getWorld(parts[0]);
        final int x = Integer.parseInt(parts[1]);
        final int y = Integer.parseInt(parts[2]);
        final int z = Integer.parseInt(parts[3]);

        return new Location(w, x, y, z);
	}
}
