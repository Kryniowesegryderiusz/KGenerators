package me.kryniowesegryderiusz.kgenerators.generators.players.limits.objects;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.generators.players.limits.LimitsManager;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.immutable.Config;
import me.kryniowesegryderiusz.kgenerators.xseries.XUtils;

public class Limit {
	
	@Getter private String id;
	@Getter private String name;
	@Getter
	private ItemStack item;
	@Getter private ArrayList<Generator> generators = new ArrayList<Generator>();
	@Getter private int placeLimit = -1;
	@Getter private boolean onlyOwnerPickUp = false;
	@Getter private boolean onlyOwnerUse = false;
	
	public Limit(LimitsManager limitsManager, Config config, String id)
	{
		boolean error = false;

		String name = "";
		if (config.contains(id+".name"))
			this.name = config.getString(id+".name");
		else {
			Logger.error("Limits file: " + id + " doesnt have name set!");
			error = true;
		}
		
		ItemStack item = null;
		if (config.contains(id+".item"))
			this.item = XUtils.parseItemStack(config.getString(id+".item"), "Limits file", false);
		else
		{
			Logger.error("Limits file: " + id + " doesnt have item set!");
			error = true;
		}
		
		if (config.contains(id+".generators"))
		{
			ArrayList<String> gensIds = (ArrayList<String>) config.getList(id+".generators");
			for (String s : gensIds)
			{
				Generator g = Main.getGenerators().get(s);
				if (g != null)
				{
					this.generators.add(g);
				}
				else
				{
					Logger.error("Limits file: Cannot load generator " + s + " in " + id + ", because it doesnt exist!");
					error = true;
				}
			}
		}
		else
		{
			if (id.equals("global_limits"))
			{
				this.generators.addAll(Main.getGenerators().getAll());
			}
			else
			{
				Logger.error("Limits file: " + id + " doesnt have generators set!");
				error = true;
			}
		}
		
		if (config.contains(id+".place-limit"))
			this.placeLimit = config.getInt(id+".place-limit");
		
		if (config.contains(id+".only-owner-pickup"))
			this.onlyOwnerPickUp = config.getBoolean(id+".only-owner-pickup");
		
		if (config.contains(id+".only-owner-use"))
			this.onlyOwnerUse =config.getBoolean(id+".only-owner-use");
		
		if (error) {
			Logger.error("Limits file: Couldnt load " + id + " limit!");
		} else {
			limitsManager.add(this);
			Logger.debug("Limits file: Loaded " + this.toString());
		}
	}
	
	public boolean fulfillsPlaceLimit(GeneratorPlayer gp, GeneratorLocation gloc, PlayerLimits pLimits)
	{
		if (!generators.contains(gloc.getGenerator()))
			return true;
		
		if (getPlacedGenerators(gp) < pLimits.getAdjustedPlaceLimits().get(this))
			return true;
		else
		{
			Lang.getMessageStorage().send(gp.getOnlinePlayer(), Message.GENERATORS_LIMITS_CANT_MORE,
					"<number>", String.valueOf(this.placeLimit),
					"<generator>", gloc.getGenerator().getGeneratorItem().getItemMeta().getDisplayName(),
					"<limit>", this.name);
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
			Lang.getMessageStorage().send(player, message,
					"<owner>", gLocation.getOwner().getName());
			return false;
		}
		
		return true;
	}
	
	public String toString() {
		ArrayList<String> generators = new ArrayList<String>();
		for (Generator g : this.generators) {
			generators.add(g.getId());
		}
		return String.format("ID: %s | PlaceLimit: %s | OnlyOwnerUse: %s | OnlyOwnerPickup: %s | Affected generators: %s", this.id, this.placeLimit, this.onlyOwnerUse, generators);
	}
}
