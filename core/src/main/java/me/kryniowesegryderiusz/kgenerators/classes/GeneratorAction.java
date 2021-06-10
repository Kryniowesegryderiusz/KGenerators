package me.kryniowesegryderiusz.kgenerators.classes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;
import me.kryniowesegryderiusz.kgenerators.enums.Action;
import me.kryniowesegryderiusz.kgenerators.enums.Interaction;

public class GeneratorAction {
	
	@Getter
	Action action;
	@Getter
	Interaction interaction = Interaction.NONE;
	@Getter
	ItemStack item = null;
	@Getter
	boolean sneak = false;
	
	public GeneratorAction(Action action, Config config, String path)
	{
		this.action = action;
		
		path += ".";
		if (config.contains(path+"mode"))
		{
			this.interaction = Interaction.Functions.getModeByString(config.getString(path+"mode"));
			if (action != Action.PICKUP && this.interaction == Interaction.BREAK)
			{
				Logger.error("Settings: You cannot set BREAK action to " + action.toString() + "! Using NONE!");
				this.interaction = Interaction.NONE;
			}
		}
		else
		{
			Logger.warn("Settings: Action mode for " + action.toString() + " is not set! Using NONE!");
			this.interaction = Interaction.NONE;
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
	
	public GeneratorAction(Action action, Interaction interaction, ItemStack item, boolean sneak)
	{
		this.action = action;
		this.interaction = interaction;
		this.item = item;
		this.sneak = sneak;
	}

	public boolean requirementsMet(Interaction gotInteraction, Player p)
	{
		if (gotInteraction != this.interaction) return false;
		else if (item != null && p.getInventory().getItemInMainHand().getType() != item.getType()) return false;
		else if (this.sneak != p.isSneaking()) return false;
		else return true;
	}
	
}
