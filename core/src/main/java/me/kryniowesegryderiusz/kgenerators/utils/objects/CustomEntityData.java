package me.kryniowesegryderiusz.kgenerators.utils.objects;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.utils.EntityUtils;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;
import me.kryniowesegryderiusz.kgenerators.xseries.XMaterial;

@AllArgsConstructor
public class CustomEntityData {

	@Getter private EntityType entityType;
	@Getter @Setter private int amount = 1;
	@Getter @Setter private String name;
	
	public CustomEntityData(EntityType entityType) {
		this.entityType = entityType;
	}
	
	public void spawnMob(Location location) {
		
		for (int i = 0; i < amount; i++) {
			Entity e = location.getWorld().spawnEntity(location, entityType);
			if (name != null)
				e.setCustomName(Main.getMultiVersion().getChatUtils().colorize(name));
		}

	}
	
	public ItemStack getItem() {
		return ItemUtils.parseItemStack(this.entityType.name()+"_SPAWN_EGG", "Generators file: GeneratedEntity", false);
	}
	
	public String toString() {
		return "Entity: " + this.entityType.name() + "[" + String.valueOf(amount) + " " + name + "]";
	}
	
	/**
	 * Checks entityType
	 * @param item
	 * @return true if xMaterials are same
	 */
	public boolean isSimilar(CustomEntityData customEntityData) {
		return entityType == customEntityData.getEntityType();
	}
	
	public static CustomEntityData load(Map<?, ?> configMap, String keyName) {
		
		if (configMap.containsKey(keyName)) {
			if (configMap.get(keyName) instanceof String) {
				return new CustomEntityData(EntityUtils.getEntityType((String) configMap.get(keyName), "CustomEntityData"));
			} else {
				Map<?, ?> map;
				
				if (configMap.get(keyName) instanceof String)
					map = (Map<?, ?>) ((MemorySection) configMap.get(keyName)).getValues(false);
				else map = (Map<?, ?>) configMap.get(keyName);
				
				if (map != null && map.containsKey("type")) {
					CustomEntityData e = new CustomEntityData(EntityUtils.getEntityType((String) map.get("type"), "CustomEntityData"));
					if (map.containsKey("amount"))
						e.setAmount((int) map.get("amount"));
					if (map.containsKey("name"))
						e.setName((String) map.get("name"));
					return e;
				}
			}
		}
		return null;
	}
}
