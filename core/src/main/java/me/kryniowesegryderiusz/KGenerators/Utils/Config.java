package me.kryniowesegryderiusz.kgenerators.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config extends YamlConfiguration{
    File file;
    File folder;
    Config(File file){
        super();
        this.file = file;
    }
    File getFile(){
        return this.file;
    }
    void setFile(File file){
        this.file = file;
    }
    public void loadConfig() throws FileNotFoundException, IOException, InvalidConfigurationException{
        load(file);
    }
    public void saveConfig() throws IOException{
        save(file);
    }
}
