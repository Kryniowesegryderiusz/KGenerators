package me.kryniowesegryderiusz.KGenerators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.Classes.Generator;
import me.kryniowesegryderiusz.KGenerators.Classes.PlayerLimits;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumLog;
import me.kryniowesegryderiusz.KGenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.PerPlayerGenerators;
import me.kryniowesegryderiusz.KGenerators.GeneratorsManagement.PickUp;
import me.kryniowesegryderiusz.KGenerators.Utils.ConfigManager;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("kgenerators.commands") || sender instanceof ConsoleCommandSender){

			if (args.length == 0){
				LangUtils.sendHelpMessage(sender);
				return false;
			}
		
			switch(args[0]){
				case "reload":
					if (sender.hasPermission("kgenerators.reload") || sender instanceof ConsoleCommandSender){
						
						try {
							Main.setMessagesFile(ConfigManager.getConfig("lang/"+Main.lang+".yml", null, false));
						} catch (FileNotFoundException e1) {
							Logger.error("[KGenerators] !!! ERROR !!! Cant find lang/" + Main.lang + ".yml file");
						}
				    	try {
							Main.getConfigFile().loadConfig();
							Main.getMessagesFile().loadConfig();
						} catch (IOException
								| InvalidConfigurationException e) {
							Logger.error(e);
						}
						Files.loadConfig();
						try {
							LangUtils.loadMessages();
						} catch (IOException e) {
							Logger.error(e);
						}
						LangUtils.sendMessage(sender, EnumMessage.CommandsReloadDone);
					}
					else
					{
						LangUtils.addReplecable("<permission>", "kgenerators.reload");
						LangUtils.sendMessage(sender, EnumMessage.CommandsReloadNoPermission);
					}
					break;
				case "getall":
					if (sender instanceof Player){
						if (sender.hasPermission("kgenerators.getall")){
							Player player = (Player) sender;
					        for (HashMap.Entry<String, Generator> generatorhmap : Main.generators.entrySet()) {
					        	Generator generator = generatorhmap.getValue();
					        	player.getInventory().addItem(generator.getGeneratorItem());
					        }
					        LangUtils.sendMessage(sender, EnumMessage.CommandsGetallRecieved);
						}
						else
						{
							LangUtils.addReplecable("<permission>", "kgenerators.getall");
							LangUtils.sendMessage(sender, EnumMessage.CommandsGetallNoPermission);
						}
					}
					else
					{
						System.out.println("[KGenerators] Use that command as player!");
					}
					break;
				case "list":
						if (sender.hasPermission("kgenerators.list") || sender instanceof ConsoleCommandSender){
							LangUtils.sendMessage(sender, EnumMessage.CommandsListHeader);
					        for (Entry<String, Generator> e : Main.generators.entrySet()) {
					        	LangUtils.addReplecable("<generator>", e.getValue().getGeneratorItem().getItemMeta().getDisplayName());
					        	LangUtils.addReplecable("<generatorID>", e.getKey());
					        	LangUtils.sendMessage(sender, EnumMessage.CommandsListList);
					        }
						}
						else
						{
							LangUtils.addReplecable("<permission>", "kgenerators.list");
							LangUtils.sendMessage(sender, EnumMessage.CommandsListNoPermission);
						}
					break;
				case "check":
					if (sender.hasPermission("kgenerators.check") || sender instanceof ConsoleCommandSender){
						if (args.length == 1)
						{
							if (sender instanceof Player){
								Player player = (Player) sender;
								check(sender, player.getName());
							}
							else
							{
								System.out.println("[KGenerators] Use that command as player!");
							}
						}
						else
						{
							if (sender.hasPermission("kgenerators.check.others"))
							{
								check(sender, args[1]);
							}
							else
							{
								LangUtils.addReplecable("<permission>", "kgenerators.check.others");
								LangUtils.sendMessage(sender, EnumMessage.CommandsCheckNoPermissionOthers);
							}
						}
					}
					else
					{
						LangUtils.addReplecable("<permission>", "kgenerators.check");
						LangUtils.sendMessage(sender, EnumMessage.CommandsCheckNoPermission);
					}
					break;
				case "howtopickup":
					if (sender.hasPermission("kgenerators.howtopickup") || sender instanceof ConsoleCommandSender){
						PickUp.errorMessage(sender, true);
					}
					else
					{
						LangUtils.addReplecable("<permission>", "kgenerators.howtopickup");
						LangUtils.sendMessage(sender, EnumMessage.CommandsHowtopickupNoPermission);
					}
				break;
				case "give":
					if (sender.hasPermission("kgenerators.give") || sender instanceof ConsoleCommandSender){
						if (args.length >= 3){
							Player player = Bukkit.getPlayer(args[1]);
							if (player == null){
								LangUtils.sendMessage(sender, EnumMessage.CommandsAnyPlayerNotOnline);
							}
							else
							{
								String generatorID = args[2];
								if (!Main.generators.containsKey(generatorID)){
									LangUtils.sendMessage(sender, EnumMessage.CommandsGiveGeneratorDoesntExist);
									break;
								}
								
								ItemStack item = Main.generators.get(generatorID).getGeneratorItem();
								
								player.getInventory().addItem(item);
								
								LangUtils.addReplecable("<generator>", item.getItemMeta().getDisplayName());
								LangUtils.addReplecable("<player>", player.getDisplayName());
								LangUtils.sendMessage(sender, EnumMessage.CommandsGiveGeneratorGiven);
								
								LangUtils.addReplecable("<generator>", item.getItemMeta().getDisplayName());
								LangUtils.sendMessage(sender, EnumMessage.CommandsGiveGeneratorRecieved);
							}
						}
						else
						{
							LangUtils.sendMessage(sender, EnumMessage.CommandsGiveUsage);
						}
					}
					else
					{
						LangUtils.addReplecable("<permission>", "kgenerators.give");
						LangUtils.sendMessage(sender, EnumMessage.CommandsGiveNoPermission);
					}
					break;
				case "debug":
					if (sender.hasPermission("kgenerators.debug") || sender instanceof ConsoleCommandSender){
						Logger.debugPasteToHaste(sender);
					}
					else
					{
						LangUtils.addReplecable("<permission>", "kgenerators.debug");
						LangUtils.sendMessage(sender, EnumMessage.CommandsDebugNoPermission);
					}
					break;
				default:
					LangUtils.sendMessage(sender, EnumMessage.CommandsAnyWrong);
					break;
			}
		}
		else
		{
			LangUtils.addReplecable("<permission>", "kgenerators.commands");
			LangUtils.sendMessage(sender, EnumMessage.CommandsAnyNoPermission);
		}

		return false;
	}
	
	void check(CommandSender sender, String nick)
	{
		Player player = Bukkit.getPlayer(nick);
		if (player != null)
		{
			LangUtils.addReplecable("<player>", player.getDisplayName());
			LangUtils.sendMessage(sender, EnumMessage.CommandsCheckHeader);
			
			for (Entry<String, Generator> e : Main.generators.entrySet())
			{
				String nr = String.valueOf(PerPlayerGenerators.getPlayerGeneratorsCount(player, e.getKey()));
				
				LangUtils.addReplecable("<number>", nr);
				LangUtils.addReplecable("<generator>", e.getValue().getGeneratorItem().getItemMeta().getDisplayName());
				LangUtils.sendMessage(sender, EnumMessage.CommandsCheckList);
			}
			
			if (Main.overAllPerPlayerGeneratorsEnabled)
			{
				LangUtils.addReplecable("<player>", player.getDisplayName());
				LangUtils.sendMessage(sender, EnumMessage.CommandsLimitsHeader);
				PlayerLimits pLimits = new PlayerLimits(player);
				for (Entry<String, Generator> e : Main.generators.entrySet())
				{
					int limit = pLimits.getLimit(e.getKey());
					String limitS;
					if (limit == -1){limitS = LangUtils.getMessage(EnumMessage.CommandsLimitsNone, false, true);}	else {limitS = String.valueOf(limit);}
					
					LangUtils.addReplecable("<generator>", e.getValue().getGeneratorItem().getItemMeta().getDisplayName());
					LangUtils.addReplecable("<limit>", limitS);
					LangUtils.sendMessage(sender, EnumMessage.CommandsLimitsList);
				}
				int limit = pLimits.getGlobalLimit();	String limitS;
				if (limit == -1){limitS = LangUtils.getMessage(EnumMessage.CommandsLimitsNone, false, true);}	else {limitS = String.valueOf(limit);}
				LangUtils.addReplecable("<limit>", limitS);
				LangUtils.sendMessage(sender, EnumMessage.CommandsLimitsOverall);
				
			}
		}
		else
		{
			LangUtils.sendMessage(sender, EnumMessage.CommandsAnyPlayerDoesntExist);
		}
	}
}