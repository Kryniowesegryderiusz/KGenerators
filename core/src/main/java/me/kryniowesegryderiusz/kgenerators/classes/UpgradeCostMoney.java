package me.kryniowesegryderiusz.kgenerators.classes;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.handlers.Vault;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class UpgradeCostMoney implements IUpgradeCost  {
	
	public UpgradeCostMoney() {}
	
	double cost;

	@Override
	public boolean checkRequirements(Player p, int amount) {
		if (this.cost*amount <= Vault.getBalance(p))
			return true;
		return false;
	}

	@Override
	public void takeRequirements(Player p, int amount) {
		Vault.takeMoney(p, this.cost*amount);
	}

	@Override
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!config.contains(generatorId+".cost"))
			return false;
		
		if (!Main.getDependencies().contains(Dependency.VAULT_ECONOMY))
			throw(new CannnotLoadUpgradeException("Upgrades file: " + generatorId + " cost couldnt be load, because VAULT economy was not found!"));
		
		cost = config.getDouble(generatorId+".cost");
		return true;
	}

	@Override
	public String getCostFormatted(int amount) {
		return Vault.formatMoney(this.cost*amount);
	}
}
