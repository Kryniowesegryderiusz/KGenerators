package me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class UpgradeCostExpLevel implements IUpgradeCost  {
	
	public UpgradeCostExpLevel() {}
	
	int cost;

	@Override
	public boolean checkRequirements(Player p, int amount) {
		if (this.cost*amount <= p.getLevel())
			return true;
		return false;
	}

	@Override
	public void takeRequirements(Player p, int amount) {
		p.giveExpLevels(-this.cost*amount);
	}

	@Override
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!config.contains(generatorId+".exp-levels"))
			return false;
				
		cost = config.getInt(generatorId+".exp-levels");
		return true;
	}

	@Override
	public String getCostFormatted(int amount) {
		return String.valueOf(amount*this.cost) + " " + Lang.getMessageStorage().get(Message.UPGRADES_COST_EXP_LEVELS);
	}
}
