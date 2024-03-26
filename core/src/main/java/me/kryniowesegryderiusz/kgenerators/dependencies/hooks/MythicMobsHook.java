package me.kryniowesegryderiusz.kgenerators.dependencies.hooks;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.mobs.ActiveMob;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.utils.EntityUtils;

public class MythicMobsHook {
	
	public static ItemStack getItemStack(String string) {
		if (!Main.getDependencies().isEnabled(Dependency.MYTHIC_MOBS)) return null;
		
		Optional<MythicItem> omi = MythicBukkit.inst().getItemManager().getItem(string);
		
		if (omi.isPresent()) {
			return BukkitAdapter.adapt(omi.get().generateItemStack(1));
		} else {
			return null;
		}
	}
	
	public static EntityType getEntityType(String entityId) {
		if (!Main.getDependencies().isEnabled(Dependency.MYTHIC_MOBS))
			return null;

		Optional<MythicMob> oet = MythicBukkit.inst().getMobManager().getMythicMob(entityId);

		if (oet.isPresent()) {
			return EntityUtils.getEntityType(oet.get().getEntityType().name(), "MythicMobsHook");
		} else {
			return null;
		}
	}

	public static Entity spawnMythicMob(String string, Location location) {
		if (!Main.getDependencies().isEnabled(Dependency.MYTHIC_MOBS))
			return null;
		
		ActiveMob am = MythicBukkit.inst().getMobManager().spawnMob(string, BukkitAdapter.adapt(location));
		
		return BukkitAdapter.adapt(am.getEntity());
	}

}
