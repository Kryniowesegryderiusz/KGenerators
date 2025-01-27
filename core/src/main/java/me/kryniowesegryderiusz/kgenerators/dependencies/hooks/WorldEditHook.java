package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;

public class WorldEditHook {

	public static ArrayList<GeneratorLocation> getGeneratorsInRange(Player p) {

		if (!Main.getDependencies().isEnabled(Dependency.WORLD_EDIT)) {
			Lang.getMessageStorage().send(p, Message.COMMANDS_REMOVE_WORLDEDIT_NOT_FOUND);
			return null;
		}

		WorldEditPlugin we = ((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"));
		try {
			World w = we.getSession(p).getSelectionWorld();
			BlockVector3 min = we.getSession(p).getSelection(w).getMinimumPoint();
			BlockVector3 max = we.getSession(p).getSelection(w).getMaximumPoint();

			return Main.getDatabases().getDb().getGenerators(Bukkit.getWorld(w.getName()), min.getBlockX(),
					min.getBlockY(), min.getBlockZ(), max.getBlockX(), max.getBlockY(), max.getBlockZ());

		} catch (IncompleteRegionException e) {
			Lang.getMessageStorage().send(p, Message.COMMANDS_REMOVE_WORLDEDIT_WRONG_SELECTION);
			return null;
		}

	}

	public static ArrayList<Location> getPlayerSelection(Player p) {

		if (!Main.getDependencies().isEnabled(Dependency.WORLD_EDIT)) {
			Lang.getMessageStorage().send(p, Message.COMMANDS_REMOVE_WORLDEDIT_NOT_FOUND);
			return null;
		}

		ArrayList<Location> locations = new ArrayList<Location>();

		WorldEditPlugin we = ((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"));
		try {
			World w = we.getSession(p).getSelectionWorld();
			Iterator<BlockVector3> iterator = we.getSession(p).getSelection(w).iterator();
			while (iterator.hasNext()) {
				BlockVector3 vec = iterator.next();
				Location loc = new Location(BukkitAdapter.adapt(w), vec.getX(), vec.getY(), vec.getZ());
				locations.add(loc);
			}

		} catch (IncompleteRegionException e) {
			Lang.getMessageStorage().send(p, Message.COMMANDS_REMOVE_WORLDEDIT_WRONG_SELECTION);
			return null;
		}

		return locations;
	}

}
