package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;
import me.kryniowesegryderiusz.kgenerators.utils.Config;

public class Upgrade {

	@Getter
	String generatorId;
	@Getter
	String nextGeneratorId;	
	
	ArrayList<IUpgradeCost> upgradeCosts = new ArrayList<IUpgradeCost>();
	
	public Upgrade(String generatorId, Config config) throws CannnotLoadUpgradeException
	{
		this.generatorId = generatorId;
		
		if (config.contains(generatorId+".next-level"))
			this.nextGeneratorId = config.getString(generatorId+".next-level");
		else
			throw(new CannnotLoadUpgradeException("Upgrades file: " + generatorId + " doesnt have next-level generator set!"));
		
		for (IUpgradeCost upgradeCost : Upgrades.getUpgradeCosts())
		{
			if (upgradeCost.load(config, generatorId))
				upgradeCosts.add(upgradeCost);
		}
		
		if (upgradeCosts.isEmpty())
			throw(new CannnotLoadUpgradeException("Upgrades file: " + generatorId + " doesnt have any cost set!"));
	}
	
	public Generator getNextGenerator()
	{
		return Generators.get(this.nextGeneratorId);
	}
	
	public Generator getPreviousGenerator()
	{
		String previousGeneratorId = Upgrades.getPreviousGeneratorId(this.generatorId);
		if (!previousGeneratorId.isEmpty())
			return Generators.get(previousGeneratorId);
		return null;	
	}
	
	public void blockUpgrade(GeneratorLocation gl, Player p)
	{
		if (!Locations.exists(gl.getLocation()))
		{
			Lang.getMessageStorage().send(p, Message.GENERATORS_ANY_NO_LONGER_THERE);
			return;
		}
		
		if (this.upgrade(p, 1))
		{
			gl.changeTo(getNextGenerator());
		}
		
	}
	
	public void handUpgrade(Player p)
	{
		ItemStack item = p.getItemInHand();
		Generator generator = Generators.get(item);
		if (generator == null)
		{
			Lang.getMessageStorage().send(p, Message.UPGRADES_NOT_A_GENERATOR);
			return;
		}

		
		if (this.upgrade(p, item.getAmount()))
		{
			ItemStack newItem = this.getNextGenerator().getGeneratorItem();
			newItem.setAmount(item.getAmount());
			p.setItemInHand(newItem);
		}

	}
	
	private boolean upgrade(Player p, int amount)
	{
		
		if (!p.hasPermission("kgenerators.upgrade"))
		{
			Lang.getMessageStorage().send(p, Message.UPGRADES__NO_PERMISSION,
					"<permission>", "kgenerators.upgrade");
			return false;
		}
		
		for (IUpgradeCost uc : upgradeCosts)
		{
			if (!uc.checkRequirements(p, amount))
			{
				Lang.getMessageStorage().send(p, Message.UPGRADES_COST_NOT_FULFILLED,
						"<cost>", uc.getCostFormatted());
				return false;
			}
		}
		
		for (IUpgradeCost uc : upgradeCosts)
		{
			uc.takeRequirements(p, amount);
		}
		
		Main.getSettings().getUpgradeSound().play(p);
		Lang.getMessageStorage().send(p, Message.UPGRADES_UPGRADED,
				"<costs>", this.getCostsFormatted(),
				"<number>", String.valueOf(amount));
		
		return true;
	}
	
	public String getCostsFormatted()
	{
		String s = "";
		for (int i = 0; i < this.upgradeCosts.size(); i++)
		{
			s = s + this.upgradeCosts.get(i).getCostFormatted();
			if (i != this.upgradeCosts.size()-1)
				s = s + Lang.getMessageStorage().get(Message.UPGRADES_COSTS_SEPARATOR, false);
		}
		return s;
	}
	
	public StringContent getCostsFormattedGUI()
	{
		ArrayList<String> costs = new ArrayList<String>();
		for (IUpgradeCost uc : this.upgradeCosts)
		{
			costs.addAll(Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.UPGRADE_COST).replace("<cost>", uc.getCostFormatted()).getLines());
		}
		return new StringContent(costs);
	}
	
	
}
