package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.VaultHook;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.PlayerUtils;
import me.kryniowesegryderiusz.kgenerators.xseries.XEnchantment;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

public class CustomDrops {

	boolean removeDefaults = false;

	private ItemStack item = null;
	private boolean itemFortune = true;

	private int exp = 0;
	private double money = 0.0;
	private ArrayList<String> commands = new ArrayList<String>();

	/**
	 * @param generatedObjectConfig
	 * @return null if object doesnt have custom drops configured
	 */
	public boolean loadCustomDrops(Map<?, ?> generatedObjectConfig) {
		if (generatedObjectConfig.containsKey("custom-drops")) {

			Map<?, ?> customDropsConfig = (Map<?, ?>) (((Map<?, ?>) generatedObjectConfig.get("custom-drops")));

			if (customDropsConfig.containsKey("remove-defaults"))
				this.removeDefaults = (boolean) customDropsConfig.get("remove-defaults");

			if (customDropsConfig.containsKey("item"))
				this.item = FilesUtils.loadItemStack(customDropsConfig, "item", "CustomDrops", false);

			if (customDropsConfig.containsKey("item-fortune"))
				this.itemFortune = (boolean) customDropsConfig.get("item-fortune");

			if (customDropsConfig.containsKey("exp"))
				this.exp = (int) customDropsConfig.get("exp");

			if (customDropsConfig.containsKey("money"))
				this.money = (double) customDropsConfig.get("money");
			
			if (customDropsConfig.containsKey("commands"))
				this.commands = (ArrayList<String>) customDropsConfig.get("commands");

			return true;
		} else
			return false;
	}

	private void doCustomDrops(Player p, Location location) {
		
		if (this.item != null) {
			ItemStack is = this.item.clone();
			
			if (this.itemFortune) {
				int level = is.getEnchantmentLevel(XEnchantment.LOOT_BONUS_BLOCKS.getEnchant());
				if (level > 0) {
					int randomNum = ThreadLocalRandom.current().nextInt(1, level + 2);
					is.setAmount(randomNum);
				}
			}
			
			PlayerUtils.dropBlockToInventory(p, location, is);

		}
		
		if (this.money > 0.0 && Main.getDependencies().isEnabled(Dependency.VAULT_ECONOMY)) {
			VaultHook.giveMoney(p, money);
		}
		
		if (!this.commands.isEmpty()) {
			for (String cmd : this.commands) {
				Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), cmd.replace("<player>", p.getName()));
			}
		}

	}

	public void doCustomDrops(BlockBreakEvent e) {
		
		/*
		 * Exp (event dependent) 
		 */
		
		if (this.removeDefaults)
			e.setExpToDrop(0);
		if (this.exp > 0)
			e.setExpToDrop(this.exp);
		if (Main.getSettings().isBlockDropUpToEq() && e.getPlayer().hasPermission("kgenerators.droptoinventory")) {
			e.getPlayer().giveExp(e.getExpToDrop());
			e.setExpToDrop(0);
		}
			
		/*
		 * Version dependent
		 */
		
		if (this.removeDefaults) {
			if (Main.getMultiVersion().isHigher(11)) e.setDropItems(false);
			else {
				Main.getMultiVersion().getBlocksUtils().setBlock(e.getBlock().getLocation(), XMaterial.AIR);
				e.setCancelled(true);
			}
		}
		
		/*
		 * Other
		 */
		
		this.doCustomDrops(e.getPlayer(), e.getBlock().getLocation());
	}
	
	public String toString() {
		String s = "RemoveDefaults: " + this.removeDefaults;
		
		if (this.item != null) s += ", Item: " + this.item.toString() + " ItemFortune: " + this.itemFortune;
		if (this.money > 0) s += ", Money:" + this.money;
		if (this.exp > 0) s += ", Exp: " + this.exp;
		if (!this.commands.isEmpty()) s += ", Commands: " + this.commands;
		return s;
	}

}
