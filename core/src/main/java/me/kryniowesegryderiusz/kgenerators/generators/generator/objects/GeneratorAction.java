package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.ActionType;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.InteractionType;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class GeneratorAction {
	
	@Getter ActionType action;
	@Getter InteractionType interaction = InteractionType.NONE;
	@Getter ItemStack item = null;
	@Getter boolean sneak = false;
	
	public GeneratorAction(ActionType action, Config config, String path)
	{
		this.action = action;
		
		path += ".";
		
		if (config.contains(path+"mode"))
		{
			this.interaction = InteractionType.Functions.getModeByString(config.getString(path+"mode"));
			if (action != ActionType.PICKUP && this.interaction == InteractionType.BREAK)
			{
				Logger.error(config.getFile().getName() + " " + path + ": You cannot set BREAK action to " + action.toString() + "! Using NONE!");
				this.interaction = InteractionType.NONE;
			}
		}
		else
		{
			Logger.warn(config.getFile().getName() + " " + path + ": Action mode for " + action.toString() + " is not set! Using NONE!");
			this.interaction = InteractionType.NONE;
		}
		
		if (config.contains(path+"item"))
		{
			String item = (config.getString(path+"item"));
			if (!item.toLowerCase().equals("any"))

				this.item = (ItemUtils.parseItemStack(item, "Config file", false));
		}
		else
		{
			Logger.warn(config.getFile().getName() + " " + path + ": Action item for " + action.toString() + " is not set! Using ANY!");
		}
		
		
		if (config.contains(path+"sneak"))
		{
			this.sneak = config.getBoolean(path+"sneak");
		}
		else
		{
			Logger.warn(config.getFile().getName() + " " + path + ": Sneak requirement for " + action.toString() + " is not set! Using FALSE!");
		}
		
	}
	
	public GeneratorAction(ActionType action, InteractionType interaction, ItemStack item, boolean sneak)
	{
		this.action = action;
		this.interaction = interaction;
		this.item = item;
		this.sneak = sneak;
	}

	public boolean requirementsMet(InteractionType gotInteraction, Player p)
	{
		if (gotInteraction != this.interaction) return false;
		else if (item != null && p.getItemInHand().getType() != item.getType()) return false;
		else if (this.sneak != p.isSneaking()) return false;
		else return true;
	}
	
}
