package me.kryniowesegryderiusz.kgenerators.files;

import java.io.File;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;

public class FilesFunctions {
    
	public static void mkdir(String dir){
		
		File mainFolder = new File(Main.getInstance().getDataFolder()+"");
		if (!mainFolder.exists())
			mainFolder.mkdir();
		
		File file = new File(Main.getInstance().getDataFolder()+"/"+dir);
		
		if (file.exists())
			return;
		
		try {
			if(!file.mkdir()){
				Logger.error("FilesFunctions: Directory for " + dir + "wasnt created!");
			}
		} catch (Exception e) {
			Logger.error("FilesFunctions: Can not create directory for "+dir);
			Logger.error(e);
		}
	}
}
