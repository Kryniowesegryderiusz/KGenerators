package me.kryniowesegryderiusz.kgenerators.addons.objects;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.addons.Addons;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

/**
 * Create new instance of this class to register new addon in KGenerators
 */
public class Addon {

	@Getter
	private String name;
	@Getter
	private String version;
	@Getter
	private ArrayList<String> configs = new ArrayList<String>();
	@Getter
	private HashMap<String, AddonCommand> commands = new HashMap<String, AddonCommand>();

	public Addon(Plugin plugin) {
		this.name = plugin.getDescription().getName();
		this.version = plugin.getDescription().getVersion();
		Addons.register(this);
		Logger.info("Addons manager: Registered " + this.toString() + " addon");
	}

	public String toString() {
		return this.name + "-" + this.version + "-" + configs + "-" + commands.keySet();
	}
	
	public interface AddonCommand {
		public void onCommand(CommandSender sender, String[] args);
	}
}
