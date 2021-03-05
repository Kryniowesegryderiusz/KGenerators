package me.kryniowesegryderiusz.kgenerators.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;
import me.kryniowesegryderiusz.kgenerators.Enums;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumAction;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumInteraction;

public class GeneratorAction {
	
	@Getter
	EnumAction action;
	@Getter
	EnumInteraction interaction = EnumInteraction.NONE;
	@Getter
	ItemStack item = null;
	@Getter
	boolean sneak = false;
	
	public GeneratorAction(EnumAction action, Config config, String path)
	{
		this.action = action;
		
		path += ".";
		if (config.contains(path+"mode"))
		{
			this.interaction = Enums.getModeByString(config.getString(path+"mode"));
			if (action != EnumAction.PICKUP && this.interaction == EnumInteraction.BREAK)
			{
				Logger.error("Settings: You cannot set BREAK action to " + action.toString() + "! Using NONE!");
				this.interaction = EnumInteraction.NONE;
			}
		}
		else
		{
			Logger.warn("Settings: Action mode for " + action.toString() + " is not set! Using NONE!");
			this.interaction = EnumInteraction.NONE;
		}
		
		if (config.contains(path+"item"))
		{
			String item = (config.getString(path+"item"));
			if (!item.toLowerCase().equals("any"))

				this.item = (XUtils.parseItemStack(item, "Config file", false));
		}
		else
		{
			Logger.warn("Settings: Action item for " + action.toString() + " is not set! Using ANY!");
		}
		
		
		if (config.contains(path+"sneak"))
		{
			this.sneak = config.getBoolean(path+"sneak");
		}
		else
		{
			Logger.warn("Settings: Sneak requirement for " + action.toString() + " is not set! Using FALSE!");
		}
		
	}
	
	public GeneratorAction(EnumAction action, EnumInteraction interaction, ItemStack item, boolean sneak)
	{
		this.action = action;
		this.interaction = interaction;
		this.item = item;
		this.sneak = sneak;
	}

	public boolean requirementsMet(EnumInteraction gotInteraction, Player p)
	{
		if (gotInteraction != this.interaction) return false;
		else if (item != null && p.getInventory().getItemInMainHand().getType() != item.getType()) return false;
		else if (this.sneak != p.isSneaking()) return false;
		else return true;
	}
	
}
