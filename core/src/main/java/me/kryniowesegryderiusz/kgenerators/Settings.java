package me.kryniowesegryderiusz.kgenerators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorAction;
import me.kryniowesegryderiusz.kgenerators.enums.Action;

public class Settings {
	
	@Setter @Getter
	private ArrayList<ItemStack> generatingWhitelist = new ArrayList<ItemStack>();
	
	@Setter @Getter
	private String lang = "en";
	
	@Setter @Getter
	private boolean actionbarMessages = true;
	
	@Getter
	private LinkedHashMap<Action, GeneratorAction> actions = new LinkedHashMap<Action, GeneratorAction>();
	
	@Setter @Getter
	private boolean pickUpSneak = true;
	
	@Setter @Getter
	private ItemStack pickUpItem = null;
	
	@Setter @Getter
	private short explosionHandler = 0;
	
	@Setter @Getter
	private boolean pickUpToEq = true;
	
	@Getter
	private ArrayList<String> disabledWorlds = new ArrayList<String>();
	
	@Setter @Getter
	private int generationCheckFrequency = 10;
	@Setter @Getter
	private int hologramUpdateFrequency = 20;
	@Setter @Getter
	private int guiUpdateFrequency = 20;
	
	@Setter @Getter
	private HashMap<Action, GeneratorAction> guis = new HashMap<Action, GeneratorAction>();
	
	
	@Setter @Getter
	private boolean limits = false;
	
	public Settings()
	{
		
	}
	
	public void addGeneratorAction(Action action, GeneratorAction gaction)
	{
		actions.put(action, gaction);
	}
	
	public GeneratorAction getAction(Action action)
	{
		return actions.get(action);
	}
	
	public boolean isWorldDisabled(World world)
	{
		return this.disabledWorlds.contains(world.getName());
	}
	
}
