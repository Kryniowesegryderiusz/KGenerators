package me.kryniowesegryderiusz.kgenerators.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.xseries.XEnchantment;

public class FilesUtils {
    
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
				Logger.error("FilesUtils: Directory for " + dir + "wasnt created!");
			}
		} catch (Exception e) {
			Logger.error("FilesUtils: Can not create directory for "+dir);
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
	     Logger.error("FilesUtils: Cannot replace text in " + file.getPath());
	     Logger.error(e);
	  }
	}
	
	public static void addToFile(File file, String string)
	{
		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.APPEND)) {
		    writer.write(string);
		    writer.newLine();
		} catch (IOException e) {
		    Logger.error("FilesUtils: Cannot add text to " + file.getPath());
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
		if (config.contains(path+".material") || config.contains(path+".item") || config.contains(path+".type")) {
			return loadItemStack(config.getMapList(path).get(0), config.getName()+"#"+path, isBlockCheck);
		} else
			return ItemUtils.parseItemStack(config.getString(path), config.getFile().getPath()+"#"+path, isBlockCheck);
	}
	
	public static ItemStack loadItemStack(Map<?,?> generalConfig, String objectName, String place, boolean isBlockCheck) {
		if (generalConfig.get("item") != null)
			if (generalConfig.get("item") instanceof String)
				return ItemUtils.parseItemStack((String) generalConfig.get("item"), place, false);
			else
				return FilesUtils.loadItemStack((Map<?, ?>) generalConfig.get("item"), place, false);
		else if (generalConfig.get("material") != null)
			if (generalConfig.get("material") instanceof String)
				return ItemUtils.parseItemStack((String) generalConfig.get("material"), place, false);
			else
				return FilesUtils.loadItemStack((Map<?, ?>) generalConfig.get("material"), place, false);
		else return null;
	}

	@SuppressWarnings("unchecked")
	private static ItemStack loadItemStack(Map<?, ?> config, String place, boolean isBlockCheck)
	{
		ItemStack item = null;
		try {

				if (config.containsKey("item"))
					item = ItemUtils.parseItemStack((String) config.get("item"), place, isBlockCheck);
				else if (config.containsKey("type"))
					item = ItemUtils.parseItemStack((String) config.get("type"), place, isBlockCheck);
				else if (config.containsKey("material"))
					item = ItemUtils.parseItemStack((String) config.get("material"), place, isBlockCheck);
				
			    ItemMeta meta = null;
			    if (item.getItemMeta() != null) {
			      meta = item.getItemMeta();
			    } else {
			      meta = Main.getInstance().getServer().getItemFactory().getItemMeta(item.getType());
			    } 
			    
			    if (config.containsKey("name"))
			    	meta.setDisplayName((String) config.get("name"));
			    
			    if (config.containsKey("lore")) {
			        ArrayList<String> lore = new ArrayList<>();
			        for (String s : (ArrayList<String>)config.get("lore")) {
			          if (s != null)
			            lore.add(Main.getMultiVersion().getChatUtils().colorize(s)); 
			        } 
			        meta.setLore(lore);
			    }
			    
			    item.setItemMeta(meta);
			    
			    if (config.containsKey("enchants")) {
			    	for (String s : (ArrayList<String>)config.get("enchants")) {
			            if (s != null)
			              {
			            	String[] splitted = s.split(":");
			            	Optional<XEnchantment> xeo = XEnchantment.matchXEnchantment(splitted[0]);
			            	if (xeo.isPresent())
			            	{
			            		item.addUnsafeEnchantment(xeo.get().parseEnchantment(), Integer.valueOf(splitted[1]));
			            	}
			            	else
			            		Logger.error(place+": Cannot load enchantment! " + splitted[0] + " doesnt exist!");
			              }
			          }
			    }
			    
			    if (config.containsKey("amount"))
			    	item.setAmount(Integer.valueOf((String) config.get("amount")));
			    
			    /*
			    if (XMaterial.matchXMaterial(item) == XMaterial.PLAYER_HEAD || XMaterial.matchXMaterial(item) == XMaterial.PLAYER_WALL_HEAD)
			    {
			    	if (config.contains(".custom-head-value"))
			    	{
			    		String value = config.getString(".custom-head-value");
			    		
			    	}
			    }
			    */
		} catch (Exception e) {
			Logger.error("FilesUtils: Cannot parse ItemStack for: " + place);
			Logger.error(e);
		}
		return item;
	}
}
