package me.kryniowesegryderiusz.kgenerators.generators.players;

import java.util.HashMap;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;

public class PlayersManager {

	private GeneratorPlayer nullPlayer = new GeneratorPlayer(null, true);

	private HashMap<String, GeneratorPlayer> players = new HashMap<String, GeneratorPlayer>();

	public GeneratorPlayer getPlayer(String nick) {
		if (nick == null)
			return nullPlayer;

		this.createPlayer(nick);

		if (!players.get(nick).isLoaded())
			players.get(nick).loadPlayer();

		return players.get(nick);

	}

	public GeneratorPlayer getPlayer(Player p) {
		return getPlayer(p.getName());
	}

	/**
	 * Creates player and does not load it. Player will be loaded if plugin will
	 * need that (getPlayer()); This method should be used specifically in loading
	 * generators from database
	 * 
	 * @param not loaded player if he is not loaded yet, loaded player otherwise
	 * @return GeneratorPlayer
	 */
	public GeneratorPlayer createPlayer(String nick) {
		if (nick == null)
			return this.nullPlayer;

		if (!players.containsKey(nick))
			players.put(nick, new GeneratorPlayer(nick));

		return players.get(nick);
	}

	public void clear() {
		this.players.clear();
	}

}
