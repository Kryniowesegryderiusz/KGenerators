package me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.events.PostGeneratorUpgradeGeneratorLocationEvent;
import me.kryniowesegryderiusz.kgenerators.api.events.PostGeneratorUpgradeItemEvent;
import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.upgrades.UpgradesManager;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.MenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.lang.objects.StringContent;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class Upgrade {

	@Getter private String generatorId;
	@Getter private String nextGeneratorId;	
	
	ArrayList<IUpgradeCost> upgradeCosts = new ArrayList<IUpgradeCost>();
	
	/**
	 * Loads upgrade from file and adds to UpgradeManager
	 * @param upgradesManager 
	 * @param config
	 * @param generatorId aka path
	 * @throws CannnotLoadUpgradeException
	 */
	public Upgrade(UpgradesManager upgradesManager, Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!generatorId.equals("example_generator_id_level_1") && !generatorId.equals("enabled")) {
			this.generatorId = generatorId;
			
			if (config.contains(generatorId+".next-level"))
				this.nextGeneratorId = config.getString(generatorId+".next-level");
			else
				throw(new CannnotLoadUpgradeException("Next-level generator is not set!"));
			
			for (IUpgradeCost upgradeCost : upgradesManager.getUpgradesCostsManager().getUpgradeCosts()) {
				if (upgradeCost.load(config, generatorId))
					upgradeCosts.add(upgradeCost);
			}
			
			if (upgradeCosts.isEmpty())
				throw(new CannnotLoadUpgradeException("Cant find costs! They are not set!"));
			
			upgradesManager.addUpgrade(generatorId, this);
			
			Logger.debugPluginLoad("Upgrades file: Loaded upgrade " + this.toString());
			
			
		}
	}
	
	public Generator getNextGenerator() {
		return Main.getGenerators().get(this.nextGeneratorId);
	}
	
	public Generator getPreviousGenerator() {
		String previousGeneratorId = Main.getUpgrades().getPreviousGeneratorId(this.generatorId);
		if (!previousGeneratorId.isEmpty())
			return Main.getGenerators().get(previousGeneratorId);
		return null;	
	}
	
	public void blockUpgrade(GeneratorLocation gl, Player p) {
		if (Main.getPlacedGenerators().getLoaded(gl.getLocation()) == null) {
			Lang.getMessageStorage().send(p, Message.GENERATORS_ANY_NO_LONGER_THERE);
			return;
		}
		
		if (this.upgrade(p, 1)) {
			gl.changeTo(getNextGenerator());
			Main.getInstance().getServer().getPluginManager().callEvent(new PostGeneratorUpgradeGeneratorLocationEvent(this, p, gl));
		}
		
	}
	
	public void handUpgrade(Player p) {
		ItemStack item = p.getItemInHand();
		Generator generator = Main.getGenerators().get(item);
		if (generator == null) {
			Lang.getMessageStorage().send(p, Message.UPGRADES_NOT_A_GENERATOR);
			return;
		}

		
		if (this.upgrade(p, item.getAmount())) {
			ItemStack newItem = this.getNextGenerator().getGeneratorItem();
			newItem.setAmount(item.getAmount());
			p.setItemInHand(newItem);
			Main.getInstance().getServer().getPluginManager().callEvent(new PostGeneratorUpgradeItemEvent(this, p, newItem));
		}

	}
	
	private boolean upgrade(Player p, int amount) {
		
		if (!p.hasPermission("kgenerators.upgrade")) {
			Lang.getMessageStorage().send(p, Message.UPGRADES__NO_PERMISSION,
					"<permission>", "kgenerators.upgrade");
			return false;
		}
		
		for (IUpgradeCost uc : upgradeCosts) {
			if (!uc.checkRequirements(p, amount)) {
				Lang.getMessageStorage().send(p, Message.UPGRADES_COST_NOT_FULFILLED,
						"<cost>", uc.getCostFormatted(amount));
				return false;
			}
		}
		
		for (IUpgradeCost uc : upgradeCosts) {
			uc.takeRequirements(p, amount);
		}
		
		Main.getSettings().getUpgradeSound().play(p);
		Lang.getMessageStorage().send(p, Message.UPGRADES_UPGRADED,
				"<costs>", this.getCostsFormatted(amount),
				"<number>", String.valueOf(amount));
		
		return true;
	}
	
	public String getCostsFormatted(int amount) {
		String s = "";
		for (int i = 0; i < this.upgradeCosts.size(); i++) {
			s = s + this.upgradeCosts.get(i).getCostFormatted(amount);
			if (i != this.upgradeCosts.size()-1)
				s = s + Lang.getMessageStorage().get(Message.UPGRADES_COSTS_SEPARATOR, false);
		}
		return s;
	}
	
	public StringContent getCostsFormattedGUI() {
		ArrayList<String> costs = new ArrayList<String>();
		for (IUpgradeCost uc : this.upgradeCosts) {
			costs.addAll(Lang.getMenuItemAdditionalLinesStorage().get(MenuItemAdditionalLines.UPGRADE_COST).replace("<cost>", uc.getCostFormatted(1)).getLines());
		}
		return new StringContent(costs);
	}
	
	public String toString() {
		return String.format("generator: %s | Next generator: %s | Costs: %s", this.generatorId, this.nextGeneratorId, this.getCostsFormatted(1));
	}
}
