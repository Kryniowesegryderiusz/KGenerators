package me.kryniowesegryderiusz.kgenerators.classes;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class UpgradeCostExp implements IUpgradeCost  {
	
	public UpgradeCostExp() {}
	
	int cost;

	@Override
	public boolean checkRequirements(Player p, int amount) {
		if (this.cost*amount <= p.getTotalExperience())
			return true;
		return false;
	}

	@Override
	public void takeRequirements(Player p, int amount) {
		p.setTotalExperience(p.getTotalExperience()-(this.cost*amount));
	}

	@Override
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!config.contains(generatorId+".exp"))
			return false;
				
		cost = config.getInt(generatorId+".exp");
		return true;
	}

	@Override
	public String getCostFormatted() {
		return String.valueOf(this.cost) + " xp";
	}
}
