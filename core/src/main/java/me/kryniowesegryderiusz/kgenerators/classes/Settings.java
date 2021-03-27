package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Enums.EnumAction;

public class Settings {
	
	@Setter @Getter
	private ArrayList<ItemStack> generatingWhitelist = new ArrayList<ItemStack>();
	
	@Setter @Getter
	private String lang = "en";
	
	@Setter @Getter
	private boolean perPlayerGenerators = false;
	
	@Setter @Getter
	private int perPlayerGeneratorsPlaceLimit = -1;
	
	@Setter @Getter
	private boolean actionbarMessages = true;
	
	@Getter
	private LinkedHashMap<EnumAction, GeneratorAction> actions = new LinkedHashMap<EnumAction, GeneratorAction>();
	
	@Setter @Getter
	private boolean pickUpSneak = true;
	
	@Setter @Getter
	private ItemStack pickUpItem = null;
	
	@Setter @Getter
	private short explosionHandler = 0;
	
	@Setter @Getter
	private int generationCheckFrequency = 10;
	@Setter @Getter
	private int hologramUpdateFrequency = 20;
	@Setter @Getter
	private int guiUpdateFrequency = 20;
	
	@Setter @Getter
	private HashMap<EnumAction, GeneratorAction> guis = new HashMap<EnumAction, GeneratorAction>();
	
	public Settings()
	{
		
	}
	
	public void addGeneratorAction(EnumAction action, GeneratorAction gaction)
	{
		actions.put(action, gaction);
	}
	
	public GeneratorAction getAction(EnumAction action)
	{
		return actions.get(action);
	}
	
}
