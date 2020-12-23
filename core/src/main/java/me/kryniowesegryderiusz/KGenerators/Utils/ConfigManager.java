package me.kryniowesegryderiusz.KGenerators.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import me.kryniowesegryderiusz.KGenerators.Main;

public final class ConfigManager {
	   
    private static Main PLUGIN = Main.getInstance();
   
    private static File mainFolder;
   
    public static void setup(){
        mainFolder = PLUGIN.getDataFolder();
        if(!mainFolder.exists()){
            mainFolder.mkdir();
            mainFolder = PLUGIN.getDataFolder();
        }
    }
   
    public static Config getConfig(String fileName, String directory, boolean copyFromResourceIfNotCreated) throws FileNotFoundException{
        File folder = getFolder(directory);
        File file = new File(folder, fileName);
        if(!file.exists()){
            if(copyFromResourceIfNotCreated){
                try {
                    Files.copy(PLUGIN.getResource(fileName), file.toPath());
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
                file = new File(folder, fileName);
                if(!file.exists()){
                    throw new FileNotFoundException("There is no "+directory+fileName);
                }
            }
            else {
                throw new FileNotFoundException("There is no "+directory+fileName);
            }
        }
        Config cfg = new Config(file);
        return cfg;
    }
   
    public static File createNewFile(String fileName, String directory) throws IOException{
        File folder = getFolder(directory);
        File file = new File(folder, fileName);
        file.createNewFile();
        return file;
    }
   
    static File getFolder(String directory){
        File folder;
        if(directory != null){
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