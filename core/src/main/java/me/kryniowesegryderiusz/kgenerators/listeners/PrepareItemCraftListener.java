package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class PrepareItemCraftListener implements Listener {

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
