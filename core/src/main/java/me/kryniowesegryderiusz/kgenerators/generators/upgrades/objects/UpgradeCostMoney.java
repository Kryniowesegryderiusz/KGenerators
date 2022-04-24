package me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.VaultHook;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class UpgradeCostMoney implements IUpgradeCost  {
	
	public UpgradeCostMoney() {}
	
	double cost;

	@Override
	public boolean checkRequirements(Player p, int amount) {
		if (this.cost*amount <= VaultHook.getBalance(p))
			return true;
		return false;
	}

	@Override
	public void takeRequirements(Player p, int amount) {
		VaultHook.takeMoney(p, this.cost*amount);
	}

	@Override
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!config.contains(generatorId+".cost"))
			return false;
		
		if (!Main.getDependencies().isEnabled(Dependency.VAULT_ECONOMY))
			throw(new CannnotLoadUpgradeException("VAULT economy was not found!"));
		
		cost = config.getDouble(generatorId+".cost");
		return true;
	}

	@Override
	public String getCostFormatted(int amount) {
		return VaultHook.formatMoney(this.cost*amount);
	}
}
