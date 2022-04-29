package me.kryniowesegryderiusz.kgenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;

public class CraftItemListener implements Listener {
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void CraftItemEvent(final CraftItemEvent e){
		
		if (!(e.getWhoClicked() instanceof Player)){
			return;
		}

		Player p = (Player) e.getWhoClicked();
	
		for (Generator g : Main.getGenerators().getAll()) {
			if (Main.getRecipes().isGeneratorRecipe(g, e.getInventory().getMatrix())) {
				
				/*
				 * Permission check
				 */
				
				String permission = "kgenerators.craft."+g.getId();
				if (!p.hasPermission(permission)) {
					Lang.getMessageStorage().send(p, Message.GENERATORS_CRAFTING_NO_PERMISSION,
							"<generator>", g.getGeneratorItem().getItemMeta().getDisplayName(),
							"<permission>", permission);
					e.setCancelled(true);
					closeInv(p);
					return;
				}
				
				/*
				 * Force recipe
				 */
				
				e.setCancelled(false);
				e.setResult(Result.ALLOW);
				e.setCurrentItem(g.getGeneratorItem());
			}	
		}
		
		/*
		 * Check for trying craft something with generator
		 */
		for (Generator g : Main.getGenerators().getAll()) {
			for (ItemStack i : e.getInventory().getMatrix()) {
				if (i != null && i.isSimilar(g.getGeneratorItem()) && e.getCurrentItem() != null && Main.getGenerators().get(e.getCurrentItem()) == null) {
					Lang.getMessageStorage().send(p, Message.GENERATORS_CRAFTING_CANT_USE);
					e.setCancelled(true);
					e.setResult(Result.DENY);
					closeInv(p);
					return;
				}
			}
		}
	}
	
	void closeInv(Player p)
	{
		Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			@Override
            public void run() {
				p.closeInventory();
            }
		});
	}
}
