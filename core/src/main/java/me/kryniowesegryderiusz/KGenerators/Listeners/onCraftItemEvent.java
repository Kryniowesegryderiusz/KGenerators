package me.kryniowesegryderiusz.kgenerators.listeners;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumMessage;
import me.kryniowesegryderiusz.kgenerators.classes.Generator;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;

public class onCraftItemEvent implements Listener {
	
	@EventHandler
	public void CraftItemEvent(final CraftItemEvent e){
		if (!(e.getWhoClicked() instanceof Player)){
			return;
		}
		
		Player p = (Player) e.getWhoClicked();
		
		for(Entry<String, Generator> entry : Generators.getEntrySet()) {
			
			String gName = entry.getKey();
			Generator g = entry.getValue();
			ItemStack item = g.getGeneratorItem();
			
			/* Check if not using generator for crafting */
			ItemStack[] items = e.getInventory().getMatrix();
			for (ItemStack i : items) {
				if (i != null && i.equals(item) && Generators.exactGeneratorItemExists(gName, item) == null) {
					Lang.sendMessage(p, EnumMessage.GeneratorsCraftingCantUse);
					e.setCancelled(true);
					closeInv(p);
					return;
				}
			}
			
			/* Check permission for crafting */
			ItemStack itemRecipe = e.getRecipe().getResult();
			if (item.equals(itemRecipe)) {
				String permission = "kgenerators.craft."+gName;
				if (!p.hasPermission(permission)) {
					Lang.addReplecable("<generator>", g.getGeneratorItem().getItemMeta().getDisplayName());
					Lang.addReplecable("<permission>", permission);
					Lang.sendMessage(p, EnumMessage.GeneratorsCraftingNoPermission);
					e.setCancelled(true);
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
