package me.kryniowesegryderiusz.kgenerators.utils.immutable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import me.kryniowesegryderiusz.kgenerators.Main;

public class ConfigManager {
	   
    private static Main PLUGIN = Main.getInstance();
   
    private static File mainFolder;
   
    static {
		mainFolder = PLUGIN.getDataFolder();
		if(!mainFolder.exists()){
			mainFolder.mkdir();
			mainFolder = PLUGIN.getDataFolder();
		}
	}
    
    public static Config getConfig(String fileName, String directory, boolean copyFromResourceIfDidNotCreated, boolean createEmptyConfigurationIfDidNotCreated) throws IOException{
		return getConfig(fileName, getFolder(directory), copyFromResourceIfDidNotCreated, createEmptyConfigurationIfDidNotCreated);
	}
	
	//Returns null if file is null
	public static Config getConfig(String fileName, File folder, boolean copyFromResourceIfDidNotCreated, boolean createEmptyConfigurationIfDidNotCreated) throws IOException {
		if(!fileName.endsWith(".yml")) {
			fileName = fileName + ".yml";
		}
		File file = getFile(fileName, folder, copyFromResourceIfDidNotCreated);
		if(file == null && createEmptyConfigurationIfDidNotCreated) {
			file = createNewFile(fileName, folder);
		}
		return file != null ? new Config(file) : null;
	}
	
	//Returns null if file does not exist
	public static File getFile(String fileName, File folder, boolean copyFromResourceIfDidNotCreated) throws IOException {
		File file = new File(folder, fileName);
		if(!file.exists()){
			if(copyFromResourceIfDidNotCreated){
				Files.copy(PLUGIN.getResource(fileName), file.toPath());
				file = new File(folder, fileName);
				if(!file.exists()){
					return null;
				}
			}
			else {
				return null;
			}
			
		}
		return file;
	}
	
	public static File getFile(String fileName, String directory, boolean copyFromResourceIfDidNotCreated) throws IOException {
		return getFile(fileName, getFolder(directory), copyFromResourceIfDidNotCreated);
	}
	
	public static File createNewFile(String fileName, File folder) throws IOException{
		File file = new File(folder, fileName);
		file.createNewFile();
		return file;
	}
	
	//If String directory is null or is empty returns plugin main folder
	public static File getFolder(String directory){
		File folder;
		if(directory != null && !directory.equals("")){
			folder = new File(mainFolder+directory);
			if(!folder.exists()){
				folder.mkdir();
			}
			return folder;
		}
		else{
			return mainFolder;
		}
	}
}
