package me.kryniowesegryderiusz.kgenerators;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import me.kryniowesegryderiusz.kgenerators.Enums.EnumAction;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumDependency;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumInteraction;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.classes.PlayerLimits;
import me.kryniowesegryderiusz.kgenerators.classes.Upgrade;
import me.kryniowesegryderiusz.kgenerators.files.GeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.LangFiles;
import me.kryniowesegryderiusz.kgenerators.files.UpgradesFile;
import me.kryniowesegryderiusz.kgenerators.gui.Menus;
import me.kryniowesegryderiusz.kgenerators.files.ConfigFile;
import me.kryniowesegryderiusz.kgenerators.handlers.PerPlayerGenerators;
import me.kryniowesegryderiusz.kgenerators.handlers.Vault;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;

public class Commands implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("kgenerators.commands") || sender instanceof ConsoleCommandSender){

			if (args.length == 0){
				Lang.sendHelpMessage(sender);
				return false;
			}
		
			switch(args[0].toLowerCase()){
				case "reload":
					if (sender.hasPermission("kgenerators.reload") || sender instanceof ConsoleCommandSender){
						Logger.info("Reload: KGenerators reload started");
						ConfigFile.globalSettingsLoader();
				    	GeneratorsFile.loadGenerators();
				    	UpgradesFile.load();
				    	LangFiles.loadLang();
						Lang.sendMessage(sender, EnumMessage.CommandsReloadDone);
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.reload");
						Lang.sendMessage(sender, EnumMessage.CommandsReloadNoPermission);
					}
					break;
				case "getall":
					if (sender instanceof Player){
						if (sender.hasPermission("kgenerators.getall")){
							Player player = (Player) sender;
					        for (HashMap.Entry<String, Generator> generatorhmap : Generators.getEntrySet()) {
					        	Generator generator = generatorhmap.getValue();
					        	player.getInventory().addItem(generator.getGeneratorItem());
					        }
					        Lang.sendMessage(sender, EnumMessage.CommandsGetallRecieved);
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.getall");
							Lang.sendMessage(sender, EnumMessage.CommandsGetallNoPermission);
						}
					}
					else
					{
						System.out.println("[KGenerators] Use that command as player!");
					}
					break;
				case "list":
						if (sender.hasPermission("kgenerators.list") || sender instanceof ConsoleCommandSender){
							Lang.sendMessage(sender, EnumMessage.CommandsListHeader);
					        for (Entry<String, Generator> e : Generators.getEntrySet()) {
					        	Lang.addReplecable("<generator>", e.getValue().getGeneratorItem().getItemMeta().getDisplayName());
					        	Lang.addReplecable("<generatorID>", e.getKey());
					        	Lang.sendMessage(sender, EnumMessage.CommandsListList);
					        }
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.list");
							Lang.sendMessage(sender, EnumMessage.CommandsListNoPermission);
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
								Lang.addReplecable("<permission>", "kgenerators.check.others");
								Lang.sendMessage(sender, EnumMessage.CommandsCheckNoPermissionOthers);
							}
						}
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.check");
						Lang.sendMessage(sender, EnumMessage.CommandsCheckNoPermission);
					}
					break;
				case "actions":
					if (sender.hasPermission("kgenerators.actions") || sender instanceof ConsoleCommandSender){
						Lang.sendMessage(sender, EnumMessage.CommandsActionsHeader);
						for (Entry<EnumAction, GeneratorAction> e : Main.getSettings().getActions().entrySet())
						{
							if (e.getValue().getInteraction() != EnumInteraction.NONE)
							{
								if (e.getKey() == EnumAction.PICKUP) Lang.addReplecable("<action>", Lang.getMessage(EnumMessage.CommandsActionsPickUp, false, false));
								else if (e.getKey() == EnumAction.OPENGUI) Lang.addReplecable("<action>", Lang.getMessage(EnumMessage.CommandsActionsOpenGui, false, false));
								else if (e.getKey() == EnumAction.TIMELEFT) Lang.addReplecable("<action>", Lang.getMessage(EnumMessage.CommandsActionsTimeLeft, false, false));
							
								if (e.getValue().isSneak())
								{
									Lang.addReplecable("<sneak>", Lang.getMessage(EnumMessage.CommandsActionsSneak, false, false));
								}
								else
								{
									Lang.addReplecable("<sneak>", "");
								}
								
								if (e.getValue().getInteraction() == EnumInteraction.BREAK) Lang.addReplecable("<mode>", Lang.getMessage(EnumMessage.CommandsActionsBreak, false, false));
								else if (e.getValue().getInteraction() == EnumInteraction.LEFT_CLICK) Lang.addReplecable("<mode>", Lang.getMessage(EnumMessage.CommandsActionsLeftClick, false, false));
								else if (e.getValue().getInteraction() == EnumInteraction.RIGHT_CLICK) Lang.addReplecable("<mode>", Lang.getMessage(EnumMessage.CommandsActionsRightClick, false, false));
								
								if (e.getValue().getItem() != null)
								{
									Lang.addReplecable("<item>", Lang.getMessage(EnumMessage.CommandsActionsItem, false, false));
									Lang.addReplecable("<itemname>", e.getValue().getItem().getType().toString());
								}
								else
								{
									Lang.addReplecable("<item>", "");
								}
								
								Lang.sendMessage(sender, EnumMessage.CommandsActionsList);
							}
						}
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.actions");
						Lang.sendMessage(sender, EnumMessage.CommandsActionsNoPermission);
					}
					break;
				case "give":
					if (sender.hasPermission("kgenerators.give") || sender instanceof ConsoleCommandSender){
						if (args.length >= 3){
							Player player = Bukkit.getPlayer(args[1]);
							if (player == null){
								Lang.sendMessage(sender, EnumMessage.CommandsAnyPlayerNotOnline);
							}
							else
							{
								String generatorID = args[2];
								if (!Generators.exists(generatorID)){
									Lang.sendMessage(sender, EnumMessage.CommandsAnyGeneratorDoesntExist);
									break;
								}
								
								ItemStack item = Generators.get(generatorID).getGeneratorItem();
								
								player.getInventory().addItem(item);
								
								Lang.addReplecable("<generator>", item.getItemMeta().getDisplayName());
								Lang.addReplecable("<player>", player.getDisplayName());
								Lang.sendMessage(sender, EnumMessage.CommandsGiveGeneratorGiven);
								
								Lang.addReplecable("<generator>", item.getItemMeta().getDisplayName());
								Lang.sendMessage(player, EnumMessage.CommandsGiveGeneratorRecieved);
							}
						}
						else
						{
							Lang.sendMessage(sender, EnumMessage.CommandsGiveUsage);
						}
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.give");
						Lang.sendMessage(sender, EnumMessage.CommandsGiveNoPermission);
					}
					break;
				case "debug":
					if (sender.hasPermission("kgenerators.debug") || sender instanceof ConsoleCommandSender){
						Logger.debugPaste(sender);
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.debug");
						Lang.sendMessage(sender, EnumMessage.CommandsDebugNoPermission);
					}
					break;
				case "timeleft":
					if (sender instanceof Player){
						Player p = (Player) sender;
						if (sender.hasPermission("kgenerators.timeleft")){
							Location l = null;
							if (p.getTargetBlockExact(5) != null) l = p.getTargetBlockExact(5).getLocation();
							if (l != null && Schedules.timeLeft(Locations.get(l)) >= 0)
							{
								Lang.addReplecable("<time>", Schedules.timeLeftFormatted(Locations.get(l)));
								Lang.sendMessage(sender, EnumMessage.GeneratorsTimeLeftOutput);
							}
							else
								Lang.sendMessage(sender, EnumMessage.CommandsTimeLeftNoGenerator);
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.timeLeft");
							Lang.sendMessage(sender, EnumMessage.CommandsTimeLeftNoPermission);
						}
					}
					else
					{
						System.out.println("[KGenerators] Use that command as player!");
					}
					break;
				case "upgrade":
					if (sender instanceof Player){
						Player p = (Player) sender;
						if (sender.hasPermission("kgenerators.upgrade")){
							if (Main.dependencies.contains(EnumDependency.VaultEconomy))
							{
								ItemStack items = p.getItemInHand();
								Generator generator = Generators.get(items);
								if (generator != null)
								{
									Upgrade upgrade = Upgrades.getUpgrade(generator.getId());
									if (upgrade != null)
									{
										double cost = items.getAmount() * upgrade.getCost();
										
										if(Vault.takeMoney(p, cost))
										{
											ItemStack newItems = upgrade.getNextGenerator().getGeneratorItem();
											newItems.setAmount(items.getAmount());
											p.setItemInHand(newItems);
											
											Lang.addReplecable("<cost>", String.valueOf(cost));
											Lang.addReplecable("<number>", String.valueOf(items.getAmount()));
											Lang.sendMessage(p, EnumMessage.VaultEconomyGeneratorUpgraded);
										}
										else
										{
											Lang.addReplecable("<cost>", String.valueOf(cost));
											Lang.sendMessage(p, EnumMessage.VaultEconomyNotEnoughMoney);
										}
									}
									else
									{
										Lang.sendMessage(p, EnumMessage.CommandsUpgradeNoNextLevel);
									}
								}
								else
								{
									Lang.sendMessage(p, EnumMessage.CommandsUpgradeNotAGenerator);
								}
							}
							else
							{
								Lang.sendMessage(p, EnumMessage.VaultEconomyNoEconomyAvaible);
							}
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.upgrade");
							Lang.sendMessage(sender, EnumMessage.CommandsTimeLeftNoPermission);
						}
					}
					else
					{
						System.out.println("[KGenerators] Use that command as player!");
					}
					break;
				case "menu":
				case "chances":
					if (sender.hasPermission("kgenerators.menu") || sender instanceof ConsoleCommandSender){
						if (args.length == 1)
						{
							if (sender instanceof Player)
								Menus.openMainMenu((Player) sender);
							else
								System.out.println("[KGenerators] Use that command as player!");
						}
						else if (sender.hasPermission("kgenerators.menu.others"))
						{
							
							Player player = Bukkit.getPlayer(args[1]);
							
							if (player != null)
							{
								if (args.length == 2)
								{
									Menus.openMainMenu(player);
								}
								else if (!args[2].toLowerCase().equals("chances") && !args[2].toLowerCase().equals("recipe") && !args[2].toLowerCase().equals("upgrade"))
								{
									Lang.sendMessage(sender, EnumMessage.CommandsAnyMenuDoesntExist);
								}
								else if (args.length < 4 || Generators.get(args[3]) == null)
								{
									Lang.sendMessage(sender, EnumMessage.CommandsAnyGeneratorDoesntExist);
								}
								else
								{
									Generator generator = Generators.get(args[3]);
									
									if (args[2].toLowerCase().contains("chances"))
									{
										Menus.openChancesMenu(player, generator);
									}
									else if (args[2].toLowerCase().contains("recipe"))
									{
										List<Recipe> recipe = Main.getInstance().getServer().getRecipesFor(generator.getGeneratorItem());
										if (!recipe.isEmpty() && recipe.get(0).getResult().equals(generator.getGeneratorItem()))
											Menus.openRecipeMenu(player, generator);
										else
											Lang.sendMessage(sender, EnumMessage.CommandsAnyMenuDoesntExist);
									} 
									else if (args[2].toLowerCase().contains("upgrade"))
									{
										if (Upgrades.couldBeObtained(args[2]))
											Menus.openUpgradeMenu(player, generator);
										else
											Lang.sendMessage(sender, EnumMessage.CommandsAnyMenuDoesntExist);
									}
								}
									
							}
							else
							{
								Lang.sendMessage(sender, EnumMessage.CommandsAnyPlayerNotOnline);
							}
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.menu.others");
							Lang.sendMessage(sender, EnumMessage.CommandsMenuNoPermissionOthers);
						}
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.menu");
						Lang.sendMessage(sender, EnumMessage.CommandsMenuNoPermission);
					}
					break;
				default:
					Lang.sendMessage(sender, EnumMessage.CommandsAnyWrong);
					break;
			}
		}
		else
		{
			Lang.addReplecable("<permission>", "kgenerators.commands");
			Lang.sendMessage(sender, EnumMessage.CommandsAnyNoPermission);
		}

		return false;
	}
	
	void check(CommandSender sender, String nick)
	{
		Player player = Bukkit.getPlayer(nick);
		if (player != null)
		{
			Lang.addReplecable("<player>", player.getDisplayName());
			Lang.sendMessage(sender, EnumMessage.CommandsCheckHeader);
			
			for (Entry<String, Generator> e : Generators.getEntrySet())
			{
				String nr = String.valueOf(PerPlayerGenerators.getPlayerGeneratorsCount(player, e.getKey()));
				
				Lang.addReplecable("<number>", nr);
				Lang.addReplecable("<generator>", e.getValue().getGeneratorItem().getItemMeta().getDisplayName());
				Lang.sendMessage(sender, EnumMessage.CommandsCheckList);
			}
			
			if (Main.getSettings().isPerPlayerGenerators())
			{
				Lang.addReplecable("<player>", player.getDisplayName());
				Lang.sendMessage(sender, EnumMessage.CommandsLimitsHeader);
				PlayerLimits pLimits = new PlayerLimits(player);
				for (Entry<String, Generator> e : Generators.getEntrySet())
				{
					int limit = pLimits.getLimit(e.getKey());
					String limitS;
					if (limit == -1){limitS = Lang.getMessage(EnumMessage.CommandsLimitsNone, false, true);}	else {limitS = String.valueOf(limit);}
					
					Lang.addReplecable("<generator>", e.getValue().getGeneratorItem().getItemMeta().getDisplayName());
					Lang.addReplecable("<limit>", limitS);
					Lang.sendMessage(sender, EnumMessage.CommandsLimitsList);
				}
				int limit = pLimits.getGlobalLimit();	String limitS;
				if (limit == -1){limitS = Lang.getMessage(EnumMessage.CommandsLimitsNone, false, true);}	else {limitS = String.valueOf(limit);}
				Lang.addReplecable("<limit>", limitS);
				Lang.sendMessage(sender, EnumMessage.CommandsLimitsOverall);
				
			}
		}
		else
		{
			Lang.sendMessage(sender, EnumMessage.CommandsAnyPlayerDoesntExist);
		}
	}
}