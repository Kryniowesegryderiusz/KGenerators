package me.kryniowesegryderiusz.kgenerators.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.xseries.XEnchantment;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class FilesFunctions {
    
	public static void mkdir(String dir)
	{
		
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
	
	public static void changeText(File file, String... keys)
	{
		
	  ArrayList<String> replacables = new ArrayList<>(Arrays.asList(keys));

	  try {
		  List<String> content = Files.readAllLines(file.toPath());
		  ArrayList<String> newContent = new ArrayList<String>();
		  for (String s : content)
		  {
		  	String line = s;
		  	
			for(int i = 0; i < replacables.size(); i=i+2)
			{
				line = line.replace(replacables.get(i), replacables.get(i+1));
			}
			
		  	newContent.add(line);
		  }
		  
		  Files.write(file.toPath(), newContent, StandardCharsets.UTF_8);
	  } catch (IOException e) {
	     Logger.error("FilesFunctions: Cannot replace text in " + file.getPath());
	     Logger.error(e);
	  }
	}
	
	public static void addToFile(File file, String string)
	{
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.APPEND)) {
		    writer.write(string);
		    writer.newLine();
		} catch (IOException e) {
		    Logger.error("FilesFunctions: Cannot add text to " + file.getPath());
		    Logger.error(e);
		}
	}
	
	/**
	 * Loads ItemStack from config
	 * @param config
	 * @param path
	 * @param isBlockCheck
	 * @return ItemStack
	 */
	public static ItemStack loadItemStack(Config config, String path, boolean isBlockCheck)
	{
		ItemStack item = null;
		
		if (config.contains(path+".item") || config.contains(path+".type"))
		{
			if (config.contains(path+".item"))
				item = XUtils.parseItemStack(config.getString(path+".item"), config.getName()+"#"+path, isBlockCheck);
			else
				item = XUtils.parseItemStack(config.getString(path+".type"), config.getName()+"#"+path, isBlockCheck);
			
            ItemMeta meta = null;
            if (item.getItemMeta() != null) {
              meta = item.getItemMeta();
            } else {
              meta = Main.getInstance().getServer().getItemFactory().getItemMeta(item.getType());
            } 
            
            if (config.contains(path+".name"))
            	meta.setDisplayName(config.getString(path+".name"));
            
            if (config.contains(path+".lore"))
            {
                ArrayList<String> lore = new ArrayList<>();
                for (String s : (ArrayList<String>)config.getList(path+".lore")) {
                  if (s != null)
                    lore.add(Main.getChatUtils().colorize(s)); 
                } 
                meta.setLore(lore);
            }
            
            item.setItemMeta(meta);
            
            if (config.contains(path+".enchants"))
            {
            	ConfigurationSection enchantsSection = config.getConfigurationSection(path+".enchants");
	            for (String enchantString : enchantsSection.getKeys(false)) {
	            	Optional<XEnchantment> xeo = XEnchantment.matchXEnchantment(enchantString);
	            	if (xeo.isPresent())
	            	{
	            		item.addUnsafeEnchantment(xeo.get().parseEnchantment(), config.getInt(path+".enchants."+enchantString));
	            	}
	            	else
	            		Logger.error(config.getName()+"#"+path+": Cannot load enchantment! " + enchantString + " doesnt exist!");
	            }
            }
            
            if (XMaterial.matchXMaterial(item) == XMaterial.PLAYER_HEAD || XMaterial.matchXMaterial(item) == XMaterial.PLAYER_WALL_HEAD)
            {
            	if(config.contains(path+".custom-head-value"))
            	{
            		String value = config.getString(path+".custom-head-value");
            		
            	}
            }
            
		}
		else
			item = XUtils.parseItemStack(config.getString(path), config.getName()+"#"+path, isBlockCheck);
		
		return item;
	}
}
