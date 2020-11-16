package me.kryniowesegryderiusz.KGenerators;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Generator {
	
	int id;
	  
	private ItemStack generatorBlock;
	  
	private ItemStack generatorItem;
	  
	private int delay;
	  
	private String type;
	
	private ItemStack placeholder;
	
	private int afterPlaceWaitModifier = 0;
	
	private boolean allowPistonPush = false;
	
	HashMap<ItemStack, Double> chances = new HashMap<ItemStack, Double>();
	  
	Generator(ItemStack generatorBlock, ItemStack generatorItem, int delay, String type, HashMap<ItemStack, Double> chances) {
	    this.generatorBlock = generatorBlock;
	    this.generatorItem = generatorItem;
	    this.delay = delay;
	    this.type = type;
	    this.chances = chances;
	}
	
	public void setPlaceholder(ItemStack placeholder) {
		this.placeholder = placeholder;
	}
	  
	public ItemStack getGeneratorBlock() {
		return this.generatorBlock;
	}
	  
	public ItemStack getGeneratorItem() {
		return this.generatorItem;
	}
	  
	public int getDelay() {
		return this.delay;
	}
	  
	public String getType() {
		return this.type;
	}
	
	public ItemStack getPlaceholder() {
		return this.placeholder;
	}
	  
	public HashMap<ItemStack, Double> getChances(){
		  return this.chances;
	}
	
	public int getAfterPlaceWaitModifier() {
		return this.afterPlaceWaitModifier;
	}
	
	public void setAfterPlaceWaitModifier (int afterPlaceWaitModifier) {
		this.afterPlaceWaitModifier = afterPlaceWaitModifier;
	}
	
	public boolean isPistonPushAllowed() {
		return this.allowPistonPush;
	}
	
	public void setPistonPushAllowed (boolean allowPistonPush) {
		this.allowPistonPush = allowPistonPush;
	}
}
