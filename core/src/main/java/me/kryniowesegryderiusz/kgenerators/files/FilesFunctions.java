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
}
