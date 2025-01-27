package me.kryniowesegryderiusz.kgenerators.dependencies.objects;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.PlayerPointsHook;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class PlayerPointsUpgradeCost implements IUpgradeCost {
	
	public PlayerPointsUpgradeCost() {}
	
	private int cost;

	@Override
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!config.contains(generatorId+".player-points"))
			return false;
				
		cost = config.getInt(generatorId+".player-points");
		return true;
	}

	@Override
	public boolean checkRequirements(Player p, int amount) {
		if (this.cost*amount <= PlayerPointsHook.getPlayerPoints(p))
			return true;
		return false;
	}

	@Override
	public void takeRequirements(Player p, int amount) {
		PlayerPointsHook.takePlayerPoints(p, this.cost*amount);
	}

	@Override
	public String getCostFormatted(int amount) {
		return String.valueOf(amount*this.cost) + " " + Lang.getMessageStorage().get(Message.UPGRADES_COST_PLAYERPOINTS);
	}

}
