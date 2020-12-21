package me.kryniowesegryderiusz.KGenerators.MultiVersion;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBar_1_8 implements ActionBar {
	  public void sendActionBar(Player player, String msg) {
	    try {
	      Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { getNMSClass("IChatBaseComponent"), byte.class });
	      Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + msg + "\"}" });
	      Object packet = constructor.newInstance(new Object[] { icbc, Byte.valueOf((byte)2) });
	      Object entityPlayer = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
	      Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
	      playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
	    } catch (Exception e) {
	      e.printStackTrace();
	    } 
	  }
	  
	  public static Class<?> getNMSClass(String name) {
	    try {
	      return Class.forName("net.minecraft.server." + getVersion() + "." + name);
	    } catch (ClassNotFoundException e) {
	      e.printStackTrace();
	      return null;
	    } 
	  }
	  
	  public static String getVersion() {
	    return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	  }
	}