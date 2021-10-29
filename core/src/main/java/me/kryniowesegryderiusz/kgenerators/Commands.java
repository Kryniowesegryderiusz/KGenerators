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
import me.kryniowesegryderiusz.kgenerators.enums.DatabaseType;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.Interaction;
import me.kryniowesegryderiusz.kgenerators.api.events.ReloadEvent;
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
import me.kryniowesegryderiusz.kgenerators.handlers.DatabasesHandler;
import me.kryniowesegryderiusz.kgenerators.handlers.Vault;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
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
				Lang.getMessageStorage().sendHelp(sender);
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
						Lang.getMessageStorage().send(sender, Message.COMMANDS_RELOAD_DONE);
						Main.getInstance().getServer().getPluginManager().callEvent(new ReloadEvent());
					}
					else
					{
						Lang.getMessageStorage().send(sender, Message.COMMANDS_RELOAD_NO_PERMISSION, "<permission>", "kgenerators.reload");
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
					        Lang.getMessageStorage().send(sender, Message.COMMANDS_GET_ALL_RECIEVED);
						}
						else
						{
							Lang.getMessageStorage().send(sender, Message.COMMANDS_GET_ALL_NO_PERMISSION, "<permission>", "kgenerators.getall");
						}
					}
					else
					{
						System.out.println("[KGenerators] Use that command as player!");
					}
					break;
				case "list":
						if (sender.hasPermission("kgenerators.list") || sender instanceof ConsoleCommandSender){
							Lang.getMessageStorage().send(sender, Message.COMMANDS_LIST_HEADER);
					        for (Entry<String, Generator> e : Generators.getEntrySet()) {
					        	Lang.getMessageStorage().send(sender, Message.COMMANDS_LIST_LIST, false, false, 
					        			"<generator>", e.getValue().getGeneratorItem().getItemMeta().getDisplayName(),
					        			"<generatorID>", e.getKey());
					        }
						}
						else
						{
							Lang.getMessageStorage().send(sender, Message.COMMANDS_LIST_NO_PERMISSION, "<permission>", "kgenerators.list");
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
						Lang.getMessageStorage().send(sender, Message.COMMANDS_LIMITS_NO_PERMISSION, "<permission>", "kgenerators.limits");
					}
					break;
				case "actions":
					if (sender.hasPermission("kgenerators.actions") || sender instanceof ConsoleCommandSender){
						Lang.getMessageStorage().send(sender, Message.COMMANDS_ACTIONS_HEADER);
						for (Entry<Action, GeneratorAction> e : Main.getSettings().getActions().entrySet())
						{
							if (e.getValue().getInteraction() != Interaction.NONE)
							{
								String action = "";
								String mode = "";
								String sneak = "";
								String item = "";
								
								if (e.getKey() == Action.PICKUP) action = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_PICK_UP, false);
								else if (e.getKey() == Action.OPENGUI) action = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_OPEN_GUI, false);
								else if (e.getKey() == Action.TIMELEFT) action = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_TIME_LEFT, false);
							
								if (e.getValue().isSneak())
									sneak = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_SNEAK, false);
								
								if (e.getValue().getInteraction() == Interaction.BREAK) mode = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_BREAK, false);
								else if (e.getValue().getInteraction() == Interaction.LEFT_CLICK) mode = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_LEFT_CLICK, false);
								else if (e.getValue().getInteraction() == Interaction.RIGHT_CLICK) mode = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_RIGHT_CLICK, false);
								
								if (e.getValue().getItem() != null)
								{
									item = Lang.getMessageStorage().get(Message.COMMANDS_ACTIONS_ITEM, false, 
											"<itemname>", Lang.getCustomNamesStorage().getItemTypeName(e.getValue().getItem()));
								}
								
								Lang.getMessageStorage().send(sender, Message.COMMANDS_ACTIONS_LIST, "<action>", action, "<sneak>", sneak, "<mode>", mode, "<item>", item);
							}
						}
					}
					else
					{
						Lang.getMessageStorage().send(sender, Message.COMMANDS_ACTIONS_NO_PERMISSION, "<permission>", "kgenerators.actions");
					}
					break;
				case "give":
					if (sender.hasPermission("kgenerators.give") || sender instanceof ConsoleCommandSender){
						if (args.length >= 3){
							Player player = Bukkit.getPlayer(args[1]);
							if (player == null){
								Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_PLAYER_NOT_ONLINE);
							}
							else
							{
								String generatorID = args[2];
								if (!Generators.exists(generatorID)){
									Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_GENERATOR_DOESNT_EXIST);
									break;
								}
								
								ItemStack item = Generators.get(generatorID).getGeneratorItem();
								
								int amount = 1;
								
								if (args.length >= 4)
								{
									try {
										amount = Integer.valueOf(args[3]);
										item.setAmount(amount);
									} catch (NumberFormatException e) {
										Lang.getMessageStorage().send(sender, Message.COMMANDS_GIVE_USAGE);
										return false;
									}
								}
								
								player.getInventory().addItem(item);
								
								Lang.getMessageStorage().send(sender, Message.COMMANDS_GIVE_GENERATOR_GIVEN,
										"<generator>", item.getItemMeta().getDisplayName(),
										"<player>", player.getDisplayName(),
										"<amount>", String.valueOf(amount),
										label, generatorID);

								Lang.getMessageStorage().send(player, Message.COMMANDS_GIVE_GENERATOR_RECIEVED,
										"<generator>", item.getItemMeta().getDisplayName(),
										"<amount>", String.valueOf(amount));
							}
						}
						else
						{
							Lang.getMessageStorage().send(sender, Message.COMMANDS_GIVE_USAGE);
						}
					}
					else
					{
						Lang.getMessageStorage().send(sender, Message.COMMANDS_GIVE_NO_PERMISSION,
								"<permission>", "kgenerators.give");
					}
					break;
				case "debug":
					if (sender.hasPermission("kgenerators.debug") || sender instanceof ConsoleCommandSender){
						Logger.debugPaste(sender);
					}
					else
					{
						Lang.getMessageStorage().send(sender, Message.COMMANDS_DEBUG_NO_PERMISSION,
								"<permission>", "kgenerators.debug");
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
								Lang.getMessageStorage().send(sender, Message.GENERATORS_TIME_LEFT_OUTPUT,
										"<time>", Schedules.timeLeftFormatted(Locations.get(l)));
							}
							else
								Lang.getMessageStorage().send(sender, Message.COMMANDS_TIME_LEFT_NO_GENERATOR);
						}
						else
						{
							Lang.getMessageStorage().send(sender, Message.COMMANDS_TIME_LEFT_NO_PERMISSION,
									"<permission>", "kgenerators.timeLeft");
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

						if (Upgrades.getUpgrade(p.getItemInHand()) != null)
						{
							Upgrades.getUpgrade(p.getItemInHand()).handUpgrade(p);
						}
						else
						{
							Lang.getMessageStorage().send(p, Message.UPGRADES__NO_NEXT_LEVEL);
						}
					}
					else
					{
						Logger.textToConsole("Use that command as player!");
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
									Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
								}
								else if (args.length < 4 || Generators.get(args[3]) == null)
								{
									Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_GENERATOR_DOESNT_EXIST);
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
											Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
									} 
									else if (args[2].toLowerCase().contains("upgrade"))
									{
										if (Upgrades.couldBeObtained(args[2]))
											Menus.openUpgradeMenu(player, generator);
										else
											Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_MENU_DOESNT_EXIST);
									}
								}
									
							}
							else
							{
								Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_PLAYER_NOT_ONLINE);
							}
						}
						else
						{
							Lang.getMessageStorage().send(sender, Message.COMMANDS_MENU_NO_PERMISSION_OTHERS,
									"<permission>", "kgenerators.menu.others");
						}
					}
					else
					{
						Lang.getMessageStorage().send(sender, Message.COMMANDS_MENU_NO_PERMISSION,
								"<permission>", "kgenerators.menu");
					}
					break;
				case "convertdbto":
					if (sender instanceof ConsoleCommandSender)
					{
						if (args.length >= 2)
						{
							DatabaseType newDbType = DatabaseType.Functions.getTypeByString(args[1]);
							
							if (newDbType != Main.getSettings().getDbType())
							{
								DatabasesHandler.changeTo(newDbType);
							}
							else
								Logger.textToConsole("You already have this database type!");
						}
						else
							Logger.textToConsole("You have to choose database you want to convert to! YAML/SQLITE/MYSQL");
					}
					else
						Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_ONLY_CONSOLE);
					break;
				default:
					Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_WRONG);
					break;
			}
		}
		else
		{
			Lang.getMessageStorage().send(sender, Message.COMMANDS_ANY_NO_PERMISSION,
					"<permission>", "kgenerators.commands");
		}

		return false;
	}
}