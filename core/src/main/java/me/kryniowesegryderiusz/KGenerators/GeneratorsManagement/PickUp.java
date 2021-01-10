package me.kryniowesegryderiusz.KGenerators.GeneratorsManagement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.Main;
import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.GeneratorLocation;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumPickUpMode;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumWGFlags;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;
import me.kryniowesegryderiusz.KGenerators.XSeries.XMaterial;

public class PickUp {
	
	/* 
	 * Checks if generator is beeing picked up
	 * Checks if event should be cancelled
	 */
	
	public static boolean isPickingUpCheck(EnumPickUpMode mode, Player p, Location location, GeneratorLocation gLocation) {
			
		EnumPickUpMode setMode = Main.pickUpMode;
		if (setMode != mode)
		{
			if (mode == EnumPickUpMode.BREAK && setMode != EnumPickUpMode.RIGHT_CLICK) return true;
			if (setMode == EnumPickUpMode.BREAK && mode == EnumPickUpMode.LEFT_CLICK) return true;
			errorMessage (p);
			return true;
		}
		
		Generator generator = gLocation.getGenerator();
		
		if (Main.pickUpSneak && !p.isSneaking())
		{
			if (generator.getType().equals("double")) {
				errorMessage(p);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		if (!PerPlayerGenerators.canPickUp(p, gLocation))
		{
			return true;
		}
		
		if (Main.pickUpItem != null)
		{
			if (p.getInventory().getItemInMainHand().getType() != Main.pickUpItem.getType())
			{
				errorMessage(p);
				return true;
			}
		}
		
		if (Main.enableWorldGuardChecks && Main.dependencies.contains("WorldGuard") && !p.hasPermission("kgenerators.bypass.worldguard") && !Main.getWorldGuardUtils().worldGuardFlagCheck(location, p, EnumWGFlags.PICK_UP))
		{
			LangUtils.sendMessage(p, EnumMessage.GeneratorsPickUpCantHere);
			return true;
		}
		
		Remove.removeGenerator(gLocation, location, true);
		LangUtils.addReplecable("<generator>", generator.getGeneratorItem().getItemMeta().getDisplayName());
		LangUtils.sendMessage(p, EnumMessage.GeneratorsPickUpSuccesful);
		return true;

	}
	
	public static void errorMessage(CommandSender p, boolean forceChat)
	{
		if (Main.pickUpItem != null)
		{
			LangUtils.addReplecable("<itemname>", XMaterial.matchXMaterial(Main.pickUpItem).toString());
			LangUtils.addReplecable("<item>", LangUtils.getMessage(EnumMessage.GeneratorsPickUpModeItem, false, true));
		}
		else
		{
			LangUtils.addReplecable("<item>", "");
		}
		
		if (Main.pickUpSneak)
		{
			LangUtils.addReplecable("<sneak>", LangUtils.getMessage(EnumMessage.GeneratorsPickUpModeSneak, false, false));
		}
		else
		{
			LangUtils.addReplecable("<sneak>", "");
		}
		
		String mode = "";
		if (Main.pickUpMode == EnumPickUpMode.BREAK) mode = LangUtils.getMessage(EnumMessage.GeneratorsPickUpModeBreak, false, false);
		else if (Main.pickUpMode == EnumPickUpMode.ANY_CLICK) mode = LangUtils.getMessage(EnumMessage.GeneratorsPickUpModeAnyClick, false, false);
		else if (Main.pickUpMode == EnumPickUpMode.LEFT_CLICK) mode = LangUtils.getMessage(EnumMessage.GeneratorsPickUpModeLeftClick, false, false);
		else if (Main.pickUpMode == EnumPickUpMode.RIGHT_CLICK) mode = LangUtils.getMessage(EnumMessage.GeneratorsPickUpModeRightClick, false, false);
		LangUtils.addReplecable("<mode>", mode);
		
		LangUtils.sendMessage(p, EnumMessage.GeneratorsPickUpUnsuccessful, forceChat);
		
	}
	
	public static void errorMessage(CommandSender p)
	{
		errorMessage(p, false);
	}

}
