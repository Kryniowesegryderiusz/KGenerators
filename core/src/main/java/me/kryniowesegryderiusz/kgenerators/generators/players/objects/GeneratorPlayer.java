package me.kryniowesegryderiusz.kgenerators.generators.players.objects;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.Limit;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects.PlayerLimits;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;

public class GeneratorPlayer {

	@Getter
	private boolean isNone = false;

	private String nick;

	@Getter
	private boolean loaded = false;

	public GeneratorPlayer(String nick) {
		this.nick = nick;
	}

	public GeneratorPlayer(String nick, boolean isNone) {
		this.nick = nick;
		this.isNone = isNone;
	}

	public Player getOnlinePlayer() {
		return Bukkit.getPlayer(this.nick);
	}

	@SuppressWarnings("deprecation")
	public OfflinePlayer getOfflinePlayer() {
		return Bukkit.getOfflinePlayer(this.nick);
	}

	public String getName() {
		if (!isNone)
			return this.nick;
		else
			return Lang.getMessageStorage().get(Message.GENERATORS_ANY_OWNER_NONE, false);
	}

	/*
	 * Limits
	 */

	private HashMap<String, Integer> playersGenerators = new HashMap<String, Integer>();

	public void loadPlayer() {

		for (GeneratorLocation gl : Main.getDatabases().getDb().getGenerators(nick)) {
			this.increaseGeneratorsAmount(gl.getGenerator());
		}
		this.loaded = true;
	}

	public void addGeneratorToPlayer(Generator generator) {
		if (isNone)
			return;

		if (!this.isLoaded())
			this.loadPlayer();
		
		this.increaseGeneratorsAmount(generator);
	}
	
	private void increaseGeneratorsAmount(Generator generator) {
		if (playersGenerators.containsKey(generator.getId())) {
			int nr = playersGenerators.get(generator.getId());
			nr++;
			playersGenerators.put(generator.getId(), nr);
		} else {
			playersGenerators.put(generator.getId(), 1);
		}
	}

	public void removeGeneratorFromPlayer(Generator generator) {
		if (isNone)
			return;
		
		if (!this.isLoaded())
			this.loadPlayer();

		if (playersGenerators.containsKey(generator.getId())) {
			int nr = playersGenerators.get(generator.getId());
			nr--;
			playersGenerators.put(generator.getId(), nr);
		}
	}

	public Integer getAllGeneratorsCount() {
		if (isNone)
			return 0;
		
		if (!this.isLoaded())
			this.loadPlayer();

		int nr = 0;
		for (Entry<String, Integer> e : playersGenerators.entrySet()) {
			nr = nr + e.getValue();
		}
		return nr;
	}

	public Integer getGeneratorsCount(Generator generator) {
		if (isNone)
			return 0;
		
		if (!this.isLoaded())
			this.loadPlayer();

		if (playersGenerators.containsKey(generator.getId()))
			return playersGenerators.get(generator.getId());
		else
			return 0;
	}

	/**
	 * Online player only
	 * 
	 * @param player
	 * @param generatorId
	 * @return
	 */
	public Boolean canPlace(GeneratorLocation gLoc) {
		if (!Main.getLimits().hasLimits())
			return true;

		PlayerLimits pl = new PlayerLimits(this.getOnlinePlayer());
		for (Limit limit : Main.getLimits().getValues()) {
			if (!limit.fulfillsPlaceLimit(this, gLoc, pl))
				return false;
		}
		return true;
	}

	/**
	 * Online player only
	 * 
	 * @param player
	 * @param gLocation
	 * @return
	 */
	public Boolean canPickUp(GeneratorLocation gLoc) {
		if (!Main.getLimits().hasLimits())
			return true;

		for (Limit limit : Main.getLimits().getValues()) {
			if (!limit.fulfillsOnlyOwnerPickUp(this, gLoc))
				return false;
		}
		return true;
	}

	public Boolean canUse(GeneratorLocation gLoc) {
		if (!Main.getLimits().hasLimits())
			return true;

		for (Limit limit : Main.getLimits().getValues()) {
			if (!limit.fulfillsOnlyOwnerUse(this, gLoc))
				return false;
		}
		return true;
	}
}
