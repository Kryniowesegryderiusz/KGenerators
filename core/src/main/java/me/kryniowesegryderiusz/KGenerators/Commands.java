package me.kryniowesegryderiusz.KGenerators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.KGenerators.Utils.ConfigManager;
import me.kryniowesegryderiusz.KGenerators.Utils.LangUtils;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("kgenerators.commands")){

			if (args.length == 0){
				LangUtils.sendHelpMessage(sender);
				return false;
			}
		
			switch(args[0]){
				case "reload":
					if (sender.hasPermission("kgenerators.reload")){
						
						try {
							KGenerators.messagesFile = ConfigManager.getConfig("lang/"+KGenerators.lang+".yml", null, false);
						} catch (FileNotFoundException e1) {
							System.out.println("[KGenerators] !!! ERROR !!! Cant find lang/" + KGenerators.lang + ".yml file");
						}
				    	try {
							KGenerators.config.loadConfig();
							KGenerators.messagesFile.loadConfig();
						} catch (IOException
								| InvalidConfigurationException e) {
							e.printStackTrace();
						}
						ConfigLoader.loadConfig();
						try {
							LangUtils.loadMessages();
						} catch (IOException e) {
							e.printStackTrace();
						}
						LangUtils.sendMessage(sender, "commands.reload.done");
					}
					else
					{
						LangUtils.addReplecable("<permission>", "kgenerators.reload");
						LangUtils.sendMessage(sender, "commands.reload.no-permission");
					}
					break;
				case "getall":
					if (sender instanceof Player){
						if (sender.hasPermission("kgenerators.getall")){
							Player player = (Player) sender;
					        for (HashMap.Entry<String, Generator> generatorhmap : KGenerators.generators.entrySet()) {
					        	Generator generator = generatorhmap.getValue();
					        	player.getInventory().addItem(generator.getGeneratorItem());
					        }
					        LangUtils.sendMessage(sender, "commands.getall.recieved");
						}
						else
						{
							LangUtils.addReplecable("<permission>", "kgenerators.getall");
							LangUtils.sendMessage(sender, "commands.getall.no-permission");
						}
					}
					else
					{
						System.out.println("[KGenerators] Use that command as player!");
					}
					break;
				case "list":
						if (sender.hasPermission("kgenerators.list")){
							String generatorrsPossible = LangUtils.getMessage("commands.list.listing", true);
							String separator = LangUtils.getMessage("commands.list.separator", false);
					        for (HashMap.Entry<String, Generator> generatorhmap : KGenerators.generators.entrySet()) {
					        	generatorrsPossible = generatorrsPossible + generatorhmap.getKey() + separator;
					        }
							sender.sendMessage(generatorrsPossible);
						}
						else
						{
							LangUtils.addReplecable("<permission>", "kgenerators.list");
							LangUtils.sendMessage(sender, "commands.list.no-permission");
						}
							
					break;
				case "give":
					if (sender.hasPermission("kgenerators.give")){
						if (args.length == 3){
							Player player = Bukkit.getPlayer(args[1]);
							if (player == null){
								LangUtils.sendMessage(sender, "commands.give.player-not-online");
							}
							else
							{
								String generatorID = args[2];
								if (!KGenerators.generators.containsKey(generatorID)){
									LangUtils.sendMessage(sender, "commands.give.generator-doesnt-exist");
									break;
								}
								
								ItemStack item = KGenerators.generators.get(generatorID).getGeneratorItem();
								
								player.getInventory().addItem(item);
								
								LangUtils.addReplecable("<generator>", item.getItemMeta().getDisplayName());
								LangUtils.addReplecable("<player>", args[1]);
								LangUtils.sendMessage(sender, "commands.give.generator-given");
								
								LangUtils.addReplecable("<generator>", item.getItemMeta().getDisplayName());
								LangUtils.sendMessage(sender, "commands.give.generator-recieved");
							}
						}
						else
						{
							LangUtils.sendMessage(sender, "commands.give.usage");
						}
					}
					else
					{
						LangUtils.addReplecable("<permission>", "kgenerators.give");
						LangUtils.sendMessage(sender, "commands.give.no-permission");
					}
					break;
				default:
					LangUtils.sendMessage(sender, "commands.any.wrong");
					break;
			}
		}
		else
		{
			LangUtils.addReplecable("<permission>", "kgenerators.commands");
			LangUtils.sendMessage(sender, "commands.any.no-permission");
		}

		return false;
	}
	
}