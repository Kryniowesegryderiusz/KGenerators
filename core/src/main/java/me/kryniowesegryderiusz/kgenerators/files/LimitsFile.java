package me.kryniowesegryderiusz.kgenerators.files;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.Limit;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Limits;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.utils.ConfigManager;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class LimitsFile {
	
	public static void load()
	{
		Config config;

    	try {
    		config = ConfigManager.getConfig("limits.yml", (String) null, true, false);
			config.loadConfig();
		} catch (IOException | InvalidConfigurationException e) {
			Logger.error("Limits file: Cant load limits. Disabling plugin.");
			Logger.error(e);
			Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
			return;
		}
		
		Limits.clear();
		
		int amount = 0;
		
		if (!config.contains("enabled") || config.getBoolean("enabled") != true)
		{
			Logger.info("Limits: Limits are disabled");
			return;
		}
		
		Main.getSettings().setLimits(true);
		
    	ConfigurationSection mainSection = config.getConfigurationSection("");
    	for(String id: mainSection.getKeys(false)){
    		
    		if (!id.equals("example_limit_group") && !id.equals("enabled"))
    		{
				boolean error = false;

				String name = "";
				if (config.contains(id+".name"))
					name = config.getString(id+".name");
				else
				{
					Logger.error("Limits file: " + id + " doesnt have name set!");
					error = true;
				}
				
				ItemStack item = null;
				if (config.contains(id+".item"))
					item = XUtils.parseItemStack(config.getString(id+".item"), "Limits file", false);
				else
				{
					Logger.error("Limits file: " + id + " doesnt have item set!");
					error = true;
				}
				
				ArrayList<Generator> gens = new ArrayList<Generator>();
				if (config.contains(id+".generators"))
				{
					ArrayList<String> gensIds = (ArrayList<String>) config.getList(id+".generators");
					for (String s : gensIds)
					{
						Generator g = Generators.get(s);
						if (g != null)
						{
							gens.add(g);
						}
						else
						{
							Logger.error("Limits file: Cannot load generator " + s + " in " + id + ", because it doesnt exist!");
							error = true;
						}
					}
				}
				else
				{
					if (id.equals("global_limits"))
					{
						gens.addAll(Generators.getAll());
					}
					else
					{
						Logger.error("Limits file: " + id + " doesnt have generators set!");
						error = true;
					}
				}
				
				Limit limit = new Limit(id, name, item, gens);
				
				if (config.contains(id+".place-limit"))
					limit.setPlaceLimit(config.getInt(id+".place-limit"));
				
				if (config.contains(id+".only-owner-pickup"))
					limit.setOnlyOwnerPickUp(config.getBoolean(id+".only-owner-pickup"));
				
				if (config.contains(id+".only-owner-use"))
					limit.setOnlyOwnerUse(config.getBoolean(id+".only-owner-use"));
				
				if (error)
				{
					Logger.error("Limits file: Couldnt load " + id + " limit!");
				}
				else
				{
					Limits.add(limit);
					amount++;
				}
    		}
    	}
    	
    	Logger.info("Limits file: Loaded " + String.valueOf(amount) + " limits");
	}

}
