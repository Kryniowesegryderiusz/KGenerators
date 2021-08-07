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

import me.kryniowesegryderiusz.kgenerators.enums.Action;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.Interaction;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.classes.PlayerLimits;
import me.kryniowesegryderiusz.kgenerators.classes.Upgrade;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.files.GeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.LangFiles;
import me.kryniowesegryderiusz.kgenerators.files.LimitsFile;
import me.kryniowesegryderiusz.kgenerators.files.PlacedGeneratorsFile;
import me.kryniowesegryderiusz.kgenerators.files.UpgradesFile;
import me.kryniowesegryderiusz.kgenerators.gui.Menus;
import me.kryniowesegryderiusz.kgenerators.files.ConfigFile;
import me.kryniowesegryderiusz.kgenerators.handlers.Vault;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Players;
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
				    	GeneratorsFile.load();
				    	Players.reload();
				    	UpgradesFile.load();
				    	LimitsFile.load();
				    	LangFiles.loadLang();
						Lang.sendMessage(sender, Message.COMMANDS_RELOAD_DONE);
						Main.dependenciesCheck();
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.reload");
						Lang.sendMessage(sender, Message.COMMANDS_RELOAD_NO_PERMISSION);
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
					        Lang.sendMessage(sender, Message.COMMANDS_GET_ALL_RECIEVED);
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.getall");
							Lang.sendMessage(sender, Message.COMMANDS_GET_ALL_NO_PERMISSION);
						}
					}
					else
					{
						System.out.println("[KGenerators] Use that command as player!");
					}
					break;
				case "list":
						if (sender.hasPermission("kgenerators.list") || sender instanceof ConsoleCommandSender){
							Lang.sendMessage(sender, Message.COMMANDS_LIST_HEADER);
					        for (Entry<String, Generator> e : Generators.getEntrySet()) {
					        	Lang.addReplecable("<generator>", e.getValue().getGeneratorItem().getItemMeta().getDisplayName());
					        	Lang.addReplecable("<generatorID>", e.getKey());
					        	Lang.sendMessage(sender, Message.COMMANDS_LIST_LIST, false, false);
					        }
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.list");
							Lang.sendMessage(sender, Message.COMMANDS_LIST_NO_PERMISSION);
						}
					break;
				case "check":
				case "limits":
					if (sender.hasPermission("kgenerators.limits") || sender instanceof ConsoleCommandSender){
						if (sender instanceof Player){
							Player player = (Player) sender;
							Menus.openLimitsMenu(player);
						}
						else
						{
							System.out.println("[KGenerators] Use that command as player!");
						}
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.limits");
						Lang.sendMessage(sender, Message.COMMANDS_LIMITS_NO_PERMISSION);
					}
					break;
				case "actions":
					if (sender.hasPermission("kgenerators.actions") || sender instanceof ConsoleCommandSender){
						Lang.sendMessage(sender, Message.COMMANDS_ACTIONS_HEADER);
						for (Entry<Action, GeneratorAction> e : Main.getSettings().getActions().entrySet())
						{
							if (e.getValue().getInteraction() != Interaction.NONE)
							{
								if (e.getKey() == Action.PICKUP) Lang.addReplecable("<action>", Lang.getMessage(Message.COMMANDS_ACTIONS_PICK_UP, false, false));
								else if (e.getKey() == Action.OPENGUI) Lang.addReplecable("<action>", Lang.getMessage(Message.COMMANDS_ACTIONS_OPEN_GUI, false, false));
								else if (e.getKey() == Action.TIMELEFT) Lang.addReplecable("<action>", Lang.getMessage(Message.COMMANDS_ACTIONS_TIME_LEFT, false, false));
							
								if (e.getValue().isSneak())
								{
									Lang.addReplecable("<sneak>", Lang.getMessage(Message.COMMANDS_ACTIONS_SNEAK, false, false));
								}
								else
								{
									Lang.addReplecable("<sneak>", "");
								}
								
								if (e.getValue().getInteraction() == Interaction.BREAK) Lang.addReplecable("<mode>", Lang.getMessage(Message.COMMANDS_ACTIONS_BREAK, false, false));
								else if (e.getValue().getInteraction() == Interaction.LEFT_CLICK) Lang.addReplecable("<mode>", Lang.getMessage(Message.COMMANDS_ACTIONS_LEFT_CLICK, false, false));
								else if (e.getValue().getInteraction() == Interaction.RIGHT_CLICK) Lang.addReplecable("<mode>", Lang.getMessage(Message.COMMANDS_ACTIONS_RIGHT_CLICK, false, false));
								
								if (e.getValue().getItem() != null)
								{
									Lang.addReplecable("<item>", Lang.getMessage(Message.COMMANDS_ACTIONS_ITEM, false, false));
									Lang.addReplecable("<itemname>", e.getValue().getItem().getType().toString());
								}
								else
								{
									Lang.addReplecable("<item>", "");
								}
								
								Lang.sendMessage(sender, Message.COMMANDS_ACTIONS_LIST);
							}
						}
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.actions");
						Lang.sendMessage(sender, Message.COMMANDS_ACTIONS_NO_PERMISSION);
					}
					break;
				case "give":
					if (sender.hasPermission("kgenerators.give") || sender instanceof ConsoleCommandSender){
						if (args.length >= 3){
							Player player = Bukkit.getPlayer(args[1]);
							if (player == null){
								Lang.sendMessage(sender, Message.COMMANDS_ANY_PLAYER_NOT_ONLINE);
							}
							else
							{
								String generatorID = args[2];
								if (!Generators.exists(generatorID)){
									Lang.sendMessage(sender, Message.COMMANDS_ANY_GENERATOR_DOESNT_EXIST);
									break;
								}
								
								ItemStack item = Generators.get(generatorID).getGeneratorItem();
								
								player.getInventory().addItem(item);
								
								Lang.addReplecable("<generator>", item.getItemMeta().getDisplayName());
								Lang.addReplecable("<player>", player.getDisplayName());
								Lang.sendMessage(sender, Message.COMMANDS_GIVE_GENERATOR_GIVEN);
								
								Lang.addReplecable("<generator>", item.getItemMeta().getDisplayName());
								Lang.sendMessage(player, Message.COMMANDS_GIVE_GENERATOR_RECIEVED);
							}
						}
						else
						{
							Lang.sendMessage(sender, Message.COMMANDS_GIVE_USAGE);
						}
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.give");
						Lang.sendMessage(sender, Message.COMMANDS_GIVE_NO_PERMISSION);
					}
					break;
				case "debug":
					if (sender.hasPermission("kgenerators.debug") || sender instanceof ConsoleCommandSender){
						Logger.debugPaste(sender);
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.debug");
						Lang.sendMessage(sender, Message.COMMANDS_DEBUG_NO_PERMISSION);
					}
					break;
				case "timeleft":
					if (sender instanceof Player){
						Player p = (Player) sender;
						if (sender.hasPermission("kgenerators.timeleft")){
							Location l = null;
							if (p.getTargetBlockExact(5) != null) l = p.getTargetBlockExact(5).getLocation();
							if (l != null && Locations.get(l) != null && Schedules.timeLeft(Locations.get(l)) >= 0)
							{
								Lang.addReplecable("<time>", Schedules.timeLeftFormatted(Locations.get(l)));
								Lang.sendMessage(sender, Message.GENERATORS_TIME_LEFT_OUTPUT);
							}
							else
								Lang.sendMessage(sender, Message.COMMANDS_TIME_LEFT_NO_GENERATOR);
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.timeLeft");
							Lang.sendMessage(sender, Message.COMMANDS_TIME_LEFT_NO_PERMISSION);
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
							if (Main.dependencies.contains(Dependency.VAULT_ECONOMY))
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
											Lang.sendMessage(p, Message.VAULT_ECONOMY_GENERATOR_UPGRADED);
										}
										else
										{
											Lang.addReplecable("<cost>", String.valueOf(cost));
											Lang.sendMessage(p, Message.VAULT_ECONOMY_NOT_ENOUGH_MONEY);
										}
									}
									else
									{
										Lang.sendMessage(p, Message.COMMANDS_UPGRADE_NO_NEXT_LEVEL);
									}
								}
								else
								{
									Lang.sendMessage(p, Message.COMMANDS_UPGRADE_NOT_A_GENERATOR);
								}
							}
							else
							{
								Lang.sendMessage(p, Message.VAULT_ECONOMY_NOT_AVAILABLE);
							}
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.upgrade");
							Lang.sendMessage(sender, Message.COMMANDS_TIME_LEFT_NO_PERMISSION);
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
									Lang.sendMessage(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
								}
								else if (args.length < 4 || Generators.get(args[3]) == null)
								{
									Lang.sendMessage(sender, Message.COMMANDS_ANY_GENERATOR_DOESNT_EXIST);
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
											Lang.sendMessage(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
									} 
									else if (args[2].toLowerCase().contains("upgrade"))
									{
										if (Upgrades.couldBeObtained(args[2]))
											Menus.openUpgradeMenu(player, generator);
										else
											Lang.sendMessage(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
									}
								}
									
							}
							else
							{
								Lang.sendMessage(sender, Message.COMMANDS_ANY_PLAYER_NOT_ONLINE);
							}
						}
						else
						{
							Lang.addReplecable("<permission>", "kgenerators.menu.others");
							Lang.sendMessage(sender, Message.COMMANDS_MENU_NO_PERMISSION_OTHERS);
						}
					}
					else
					{
						Lang.addReplecable("<permission>", "kgenerators.menu");
						Lang.sendMessage(sender, Message.COMMANDS_MENU_NO_PERMISSION);
					}
					break;
				default:
					Lang.sendMessage(sender, Message.COMMANDS_ANY_WRONG);
					break;
			}
		}
		else
		{
			Lang.addReplecable("<permission>", "kgenerators.commands");
			Lang.sendMessage(sender, Message.COMMANDS_ANY_NO_PERMISSION);
		}

		return false;
	}
}