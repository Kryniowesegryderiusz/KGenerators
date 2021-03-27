package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.managers.Upgrades;

public class Generator {
	
	@Getter
	private String id;
	@Getter
	private ItemStack generatorBlock;
	
	private ItemStack generatorItem;
	@Getter  
	private int delay;
	@Getter
	private GeneratorType type;
	@Getter
	private boolean secondMultiple = false;
	
    @Getter
    LinkedHashMap<ItemStack, Double> chances = new LinkedHashMap<ItemStack, Double>();
	
	@Getter @Setter
	private ItemStack placeholder;
	
	@Getter @Setter
	private int afterPlaceWaitModifier = 0;
	
	@Getter @Setter
	private boolean allowPistonPush = false;
	
	@Getter @Setter
	private boolean hologram = false;
	
	@Getter @Setter
	private Integer placeLimit = -1;
	@Getter @Setter
    private Boolean onlyOwnerPickUp = false;
    @Getter @Setter
    private Boolean onlyOwnerUse = false;
    @Getter @Setter
    private Boolean onlyOwnerStorage = false;
    
    private double fullChance = 0.0;
	  
	public Generator(String id, ItemStack generatorBlock, ItemStack generatorItem, int delay, GeneratorType type, LinkedHashMap<ItemStack, Double> chances) {
		this.id = id;
	    this.generatorBlock = generatorBlock;
	    this.generatorItem = generatorItem;
	    this.delay = delay;
	    this.type = type;
	    this.chances = chances;
	    
	    double sm = (double) delay/20;
	    if (sm == Math.floor(sm)) secondMultiple = true;
	    
	    for (Entry<ItemStack, Double> e : chances.entrySet())
	    {
	    	this.fullChance = this.fullChance + e.getValue();
	    }
	}
	
	public boolean doesChancesContain(ItemStack item)
	{
		for (Entry<ItemStack, Double> e : chances.entrySet())
		{
			if (e.getKey().equals(item)) return true;
		}
		return false;
	}
	
	public double getChancePercent(ItemStack item)
	{
		double chance = chances.get(item);
		double fullchance = this.fullChance;
		double percent = (chance/fullchance) * 100;
		return percent;
	}
	
	public boolean isGeneratingImmediately() {
		if (this.afterPlaceWaitModifier == 0) return true;
		return false;
	}
	
	public ItemStack getGeneratorItem()
	{
		return this.generatorItem.clone();
	}
	
	public Upgrade getUpgrade()
	{
		return Upgrades.getUpgrade(id);
	}
}
