package me.kryniowesegryderiusz.kgenerators.generators.generator.objects;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.VaultHook;
import me.kryniowesegryderiusz.kgenerators.utils.FilesUtils;
import me.kryniowesegryderiusz.kgenerators.utils.PlayerUtils;
import com.cryptomorin.xseries.XEnchantment;

public class CustomDrops {

	@Getter boolean removeDefaults = false;

	@Getter private ItemStack item = null;
	@Getter private boolean itemFortune = true;

	@Getter private int exp = 0;
	@Getter private double money = 0.0;
	@Getter private ArrayList<String> commands = new ArrayList<String>();

	/**
	 * @param generatedObjectConfig
	 * @return null if object doesn't have custom drops configured
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

			if (customDropsConfig.containsKey("money")) {
				if (customDropsConfig.get("money") instanceof Integer) {
					this.money = Double.valueOf((Integer) customDropsConfig.get("money"));
				} else
					this.money = (double) customDropsConfig.get("money");
			}

			if (customDropsConfig.containsKey("commands")) {
				this.commands = (ArrayList<String>) customDropsConfig.get("commands");
			}

			return true;
		} else
			return false;
	}
	
	public void doItemDrops(@Nullable Player p, Location location) {
		if (this.item != null) {
			ItemStack is = this.item.clone();

			if (p != null && this.itemFortune) {
				int level = 0;
				if (p.getInventory().getItemInMainHand() != null)
					level = p.getInventory().getItemInMainHand().getEnchantmentLevel(XEnchantment.FORTUNE.get());
				if (level > 0) {
					int randomNum = ThreadLocalRandom.current().nextInt(1, level + 2);
					is.setAmount(randomNum);
				}
			}

			PlayerUtils.dropBlockToInventory(p, location, is);

		}
	}
	
	public void doMoneyDrops(Player p) {
		if (p != null && this.money > 0.0 && Main.getDependencies().isEnabled(Dependency.VAULT_ECONOMY)) {
			VaultHook.giveMoney(p, money);
		}
	}
	
	public void doCommandDrops(String playerName, Location location) {
		if (!this.commands.isEmpty()) {
			for (String cmd : this.commands) {
				cmd = CoordControl(cmd, location, "<x");
				cmd = CoordControl(cmd, location, "<y");
				cmd = CoordControl(cmd, location, "<z");
				Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), cmd.replace("<player>", playerName));
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
		if (Main.getSettings().isBlockDropToEq() && e.getPlayer().hasPermission("kgenerators.droptoinventory")) {
			e.getPlayer().giveExp(e.getExpToDrop());
			e.setExpToDrop(0);
		}

		/*
		 * Version dependent
		 */

		if (this.removeDefaults) {
			if (Main.getMultiVersion().isHigher(11)) e.setDropItems(false);
			else {
				Main.getMultiVersion().getBlocksUtils().setBlock(e.getBlock().getLocation(), Material.AIR);
				e.setCancelled(true);
			}
		}

		/*
		 * Other
		 */

		this.doItemDrops(e.getPlayer(), e.getBlock().getLocation());

		this.doMoneyDrops(e.getPlayer());
		
		this.doCommandDrops(e.getPlayer().getName(), e.getBlock().getLocation());
	}

	public String toString() {
		String s = "RemoveDefaults: " + this.removeDefaults;

		if (this.item != null) s += ", Item: " + this.item.toString() + " ItemFortune: " + this.itemFortune;
		if (this.money > 0) s += ", Money:" + this.money;
		if (this.exp > 0) s += ", Exp: " + this.exp;
		if (!this.commands.isEmpty()) s += ", Commands: " + this.commands;
		return s;
	}
	
	/*
	 * CoordControl
	 */
	
	public String CoordControl(String cmd, Location loc, String coord){
		double coordValue = getCoordVal(coord, loc);
		if(cmd.contains(coord)) {
			String[] split = cmd.split(coord);
			String[] split2 = split[1].split(">");
			if (cmd.contains(coord + "-")||cmd.contains(coord + "+"))
				return cmd.replace((coord + split2[0] + ">"), String.valueOf(coordValue + Double.parseDouble(split2[0])));
		}
		return cmd.replace(coord + ">", String.valueOf(coordValue));
	}

	public double getCoordVal(String coord, Location loc) {
		switch (coord) {
			case "<x":
				return (loc.getX()+0.5);
			case "<y":
				return (loc.getY()+1);
			case "<z":
				return (loc.getZ()+0.5);
			default:
				return 0;
		}
	}

}
