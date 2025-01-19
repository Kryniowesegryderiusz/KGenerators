package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class CraftingListeners implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCraftItem(final CraftItemEvent e) {

		try {

			if (!(e.getWhoClicked() instanceof Player) || e.isCancelled()) {
				return;
			}

			Player p = (Player) e.getWhoClicked();
			
			Generator resultGenerator = Main.getGenerators().get(e.getRecipe().getResult());
			
			/*
			 * Check for crafting something with generator
			 */
			for (ItemStack i : e.getInventory().getMatrix()) {
				if (i != null 
						&& Main.getGenerators().get(i) != null 
						&& e.getCurrentItem() != null
						&& resultGenerator == null) {
					Lang.getMessageStorage().send(p, Message.GENERATORS_CRAFTING_CANT_USE);
					e.setResult(Result.DENY);
					closeInv(p);
					return;
				}
			}
			
			/*
			 * Permission check
			 */
			if (Main.getRecipes().isEnabled()
					&& resultGenerator != null) {
				String permission = "kgenerators.craft." + resultGenerator.getId();
				if (!p.hasPermission(permission)) {
					Lang.getMessageStorage().send(p, Message.GENERATORS_CRAFTING_NO_PERMISSION, "<generator>",
							resultGenerator.getGeneratorItem().getItemMeta().getDisplayName(), "<permission>", permission);
					e.setResult(Result.DENY);
					closeInv(p);
					return;
				}
			}

		} catch (Exception exception) {
			Logger.error(exception);
		}
	}

	private void closeInv(Player p) {
		Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				p.closeInventory();
			}
		});
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepareItemCraft(PrepareItemCraftEvent e) {

		try {
			/*
			 * PreCheck for trying craft something with generator
			 */
			for (Generator g : Main.getGenerators().getAll()) {
				for (ItemStack i : e.getInventory().getMatrix()) {
					if (i != null && i.isSimilar(g.getGeneratorItem()) && e.getInventory().getResult() != null
							&& Main.getGenerators().get(e.getInventory().getResult()) == null) {
						Lang.getMessageStorage().send(e.getInventory().getViewers().get(0),
								Message.GENERATORS_CRAFTING_CANT_USE);
						e.getInventory().setResult(new ItemStack(Material.AIR));
						return;
					}
				}
			}

		} catch (Exception exception) {
			Logger.error(exception);
		}
	}

}
