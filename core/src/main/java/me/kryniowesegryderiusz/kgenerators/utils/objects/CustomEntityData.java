package me.kryniowesegryderiusz.kgenerators.utils.objects;

import java.util.ArrayList;
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
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.MythicMobsHook;
import me.kryniowesegryderiusz.kgenerators.utils.EntityUtils;
import me.kryniowesegryderiusz.kgenerators.utils.ItemUtils;

@AllArgsConstructor
public class CustomEntityData {

	@Getter private EntityType entityType;
	@Getter private String entityTypeString;
	@Getter @Setter private int amount = 1;
	@Getter @Setter private String name;
	
	public CustomEntityData(String entityTypeString) {
		this.entityTypeString = entityTypeString;
		if (!entityTypeString.contains(":"))
            this.entityType = EntityUtils.getEntityType(entityTypeString, "CustomEntityData");
		else {
			String[] splitted = entityTypeString.split(":");
			if(splitted[0].equalsIgnoreCase("mythicmobs")) {
				this.entityType = MythicMobsHook.getEntityType(splitted[1]);
			}
		}
		
	}
	
	public ArrayList<Entity> spawnEntities(Location location) {
		
		ArrayList<Entity> spawnedEntities = new ArrayList<Entity>();
		
		for (int i = 0; i < amount; i++) {
			if (this.entityTypeString.contains("mythicmobs")) {
				Entity e = MythicMobsHook.spawnMythicMob(this.entityTypeString.split(":")[1], location);
				spawnedEntities.add(e);
			} else {
				Entity e = location.getWorld().spawnEntity(location, entityType);
				if (name != null)
					e.setCustomName(Main.getMultiVersion().getChatUtils().colorize(name));
				spawnedEntities.add(e);
			}
		}
		
		return spawnedEntities;

	}
	
	public ItemStack getItem() {
		return ItemUtils.parseItemStack(this.entityType.name()+"_SPAWN_EGG", "Generators file: GeneratedEntity", false);
	}
	
	public String toString() {
		return "Entity: " + this.entityTypeString + "[" + String.valueOf(amount) + " " + name + "]";
	}
	
	/**
	 * Checks entityType
	 * @param item
	 * @return true if xMaterials are same
	 */
	public boolean isSimilar(CustomEntityData customEntityData) {
		return entityTypeString.equals(customEntityData.getEntityTypeString());
	}
	
	public static CustomEntityData load(Map<?, ?> configMap, String keyName) {
		
		if (configMap.containsKey(keyName)) {
			if (configMap.get(keyName) instanceof String) {
				//return new CustomEntityData(EntityUtils.getEntityType((String) configMap.get(keyName), "CustomEntityData"));
				return new CustomEntityData((String) configMap.get(keyName));
			} else {
				Map<?, ?> map;
				
				if (configMap.get(keyName) instanceof String)
					map = (Map<?, ?>) ((MemorySection) configMap.get(keyName)).getValues(false);
				else map = (Map<?, ?>) configMap.get(keyName);
				
				if (map != null && map.containsKey("type")) {
					//CustomEntityData e = new CustomEntityData(EntityUtils.getEntityType((String) map.get("type"), "CustomEntityData"));
					CustomEntityData e = new CustomEntityData((String) map.get("type"));
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
