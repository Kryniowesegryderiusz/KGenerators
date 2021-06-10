package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.hooks.BentoBoxHook;

public class Limit {
	
	@Getter
	private String id;
	@Getter
	private String name;
	@Getter
	ItemStack item;
	@Getter
	private ArrayList<Generator> generators = new ArrayList<Generator>();
	@Getter @Setter
	private int placeLimit = -1;
	@Getter @Setter
	private boolean onlyOwnerPickUp = false;
	@Getter @Setter
	private boolean onlyOwnerUse = false;
	
	public Limit(String id, String name, ItemStack item, ArrayList<Generator> generators)
	{
		this.id = id;
		this.name = name;
		this.item = item;
		this.generators = generators;
	}
	
	public boolean fulfillsPlaceLimit(GeneratorPlayer gp, GeneratorLocation gloc, PlayerLimits pLimits)
	{
		if (!generators.contains(gloc.getGenerator()))
			return true;
		
		if (getPlacedGenerators(gp) < pLimits.getAdjustedPlaceLimits().get(this))
			return true;
		else
		{
			Lang.addReplecable("<number>", String.valueOf(this.placeLimit));
			Lang.addReplecable("<generator>", gloc.getGenerator().getGeneratorItem().getItemMeta().getDisplayName());
			Lang.addReplecable("<limit>", this.name);
			Lang.sendMessage(gp.getOnlinePlayer(), Message.GENERATORS_LIMITS_CANT_MORE);
			return false;
		}
	}
	
	public int getPlacedGenerators(GeneratorPlayer gp)
	{
		int amount = 0;
		for (Generator g : generators)
		{
			amount += gp.getGeneratorsCount(g);
		}
		return amount;
	}
	
	/**
	 * Online player only
	 * @param player
	 * @param gLocation
	 * @return
	 */
	public Boolean fulfillsOnlyOwnerPickUp(GeneratorPlayer gp, GeneratorLocation gLocation)
	{
		if (this.onlyOwnerPickUp)
			return ownerCheck(gp, gLocation, Message.GENERATORS_LIMITS_CANT_PICK_UP);
		else
			return true;
	}

	public Boolean fulfillsOnlyOwnerUse(GeneratorPlayer gp, GeneratorLocation gLocation){
		if (this.onlyOwnerUse)
			return ownerCheck(gp, gLocation, Message.GENERATORS_LIMITS_CANT_USE);
		else
			return true;
	}
	
	private boolean ownerCheck(GeneratorPlayer gp, GeneratorLocation gLocation, Message message)
	{
		if (!generators.contains(gLocation.getGenerator()))
			return true;
		
		Player player = gp.getOnlinePlayer();
		
		if (player.hasPermission("kgenerators.bypass.onlyownerchecks"))
			return true;
		
		/* To prevent errors if generator havent any owner */
		if (gLocation.getOwner().isNone())
			return true;
					
		if (gp != gLocation.getOwner())
		{
			Lang.addReplecable("<owner>", gLocation.getOwner().getName());
			Lang.sendMessage(player, message);
			return false;
		}
		
		return true;
	}
}
