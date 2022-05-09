package me.kryniowesegryderiusz.kgenerators.utils;

import org.bukkit.entity.EntityType;

import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class EntityUtils {
	
    public static EntityType getEntityType(String s, String place) {
    	try {
			return EntityType.valueOf(s);
		} catch (Exception e) {
			Logger.error(place+": " + s + " is not a proper entity! Using PIG!");
		}
    	return EntityType.PIG;
    }

}
