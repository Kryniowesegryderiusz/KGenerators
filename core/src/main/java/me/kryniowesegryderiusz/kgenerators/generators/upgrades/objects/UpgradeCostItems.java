package me.kryniowesegryderiusz.kgenerators.generators.upgrades.objects;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.exceptions.CannnotLoadUpgradeException;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IUpgradeCost;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.VaultHook;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;

public class UpgradeCostItems implements IUpgradeCost  {
	
	public UpgradeCostItems() {}
	
	private ArrayList<ItemStack> items = new ArrayList<ItemStack>();

	@Override
	public boolean checkRequirements(Player p, int amount) {
		
		for (ItemStack item : items) {
			int found = 0;
			for (ItemStack is : p.getInventory().getStorageContents()) {
				if (is != null && is.isSimilar(item))
					found += is.getAmount();
			}
			
			if (found < item.getAmount())
				return false;
			
		}
		return true;
	}

	@Override
	public void takeRequirements(Player p, int amount) {
		for(ItemStack item : items) {
			ItemStack is = item.clone();
			p.getInventory().removeItem(is);
		}
	}

	@Override
	public boolean load(Config config, String generatorId) throws CannnotLoadUpgradeException {
		if (!config.contains(generatorId+".items"))
			return false;

		for (String s : config.getConfigurationSection(generatorId+".items").getKeys(false)) {
			items.add(FilesUtils.loadItemStack(config, generatorId+".items", s, false));
		}

		return true;
	}

	@Override
	public String getCostFormatted(int amount) {
		String cost = "";
		
		for (ItemStack is : items) {
			if (!cost.isEmpty()) cost+=Lang.getMessageStorage().get(Message.UPGRADES_COSTS_SEPARATOR, false);
			cost+= Lang.getMessageStorage().get(Message.GENERATORS_ANY_OBJECT_AMOUNT, false, "<amount>", is.getAmount()+"", "<object>", Lang.getCustomNamesStorage().getItemTypeName(is));
		}
		
		return cost;	
	}
}
