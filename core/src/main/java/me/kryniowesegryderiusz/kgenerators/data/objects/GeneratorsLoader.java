package me.kryniowesegryderiusz.kgenerators.data.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.objects.AbstractGeneratedObject;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.PlacedGeneratorsManager.ChunkInfo;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratorsLoader {

	@Getter
	private int amount = 0;
	@Getter
	private int notLoaded = 0;
	private ArrayList<String> errNotSetId = new ArrayList<String>();
	private ArrayList<String> errNotExist = new ArrayList<String>();
	private ArrayList<String> errWorlds = new ArrayList<String>();

	private ArrayList<GeneratorLocation> loadedGenerators = new ArrayList<GeneratorLocation>();

	/**
	 * 
	 * @param generatorId
	 * @param location    ("world,x,y,z")
	 * @Param chunk ("world,x,z")
	 * @param owner of owner or null
	 * @throws SQLException
	 */
	public void loadNext(ResultSet res) throws SQLException {
		this.loadNext(res.getInt("id"), res.getString("generator_id"),
				res.getString("world") + "," + res.getString("x") + "," + res.getString("y") + "," + res.getString("z"),
				res.getString("world") + "," + res.getString("chunk_x") + "," + res.getString("chunk_z"),
				res.getString("owner"), res.getInt("last_generated_object"));
	}

	public void loadNext(int generatorLocationId, String generatorId, String location, String chunk, String owner, int lastGeneratedObjectId) {
		try {
			boolean err = false;

			String world = location.split(",")[0];
			if (Main.getInstance().getServer().getWorld(world) == null) {
				if (!errWorlds.contains(world))
					errWorlds.add(world);
				err = true;
			}
			Location generatorLocation = Main.getPlacedGenerators().stringToLocation(location);

			ChunkInfo generatorChunkInfo = null;
			if (!chunk.contains("null") && !chunk.equals(","))
				generatorChunkInfo = Main.getPlacedGenerators().stringToChunkInfo(chunk);

			if (generatorId != null) {
				if (!Main.getGenerators().exists(generatorId)) {
					if (!errNotExist.contains(generatorId))
						errNotExist.add(generatorId);
					err = true;
				}
			} else {
				if (!errNotSetId.contains(location))
					errNotSetId.add(location);
				err = true;
			}
			
			AbstractGeneratedObject ago = null;
			if (!err && lastGeneratedObjectId >= 0) {
				ago = Main.getGenerators().get(generatorId).getGeneratedObjectById(lastGeneratedObjectId);
			}

			if (!err) {
				this.loadedGenerators.add(new GeneratorLocation(generatorLocationId, Main.getGenerators().get(generatorId), generatorLocation,
						generatorChunkInfo, Main.getPlayers().createPlayer(owner), ago));
				amount++;
			} else {
				notLoaded++;
			}
		} catch (Exception e) {
			Logger.error("An error occured, while loading " + generatorId + " | " + location + " | " + chunk + " | " + owner + " | " + lastGeneratedObjectId);
			Logger.error(e);
		}
	}

	public ArrayList<GeneratorLocation> finish() {
		if (notLoaded != 0) {
			Logger.error(
					"Database: An error occured, while loading " + String.valueOf(notLoaded) + " placed generators!");
			if (!errWorlds.isEmpty()) {
				Logger.error("Database: Cant load worlds: " + errWorlds.toString());
				String possible = "";
				for (World world : Bukkit.getWorlds())
					possible += world.getName() + ", ";
				Logger.error("Database: Possible worlds: " + possible);
			}
			if (!errNotExist.isEmpty()) {
				Logger.error("Database: There are no generators with ids: " + errNotExist.toString());
				String possible = "";
				for (Entry<String, Generator> e : Main.getGenerators().getEntrySet())
					possible += e.getKey() + ", ";
				Logger.error("Database: Possible ids: " + possible);
			}
			if (!errNotSetId.isEmpty()) {
				Logger.error("Database: Generator id is not set for: " + errNotSetId.toString());
			}
		}

		return this.loadedGenerators;
	}
}
