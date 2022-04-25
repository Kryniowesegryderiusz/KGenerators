package me.kryniowesegryderiusz.kgenerators.generators.locations.objects;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IGeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.Dependency;
import me.kryniowesegryderiusz.kgenerators.dependencies.enums.WGFlag;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.BentoBoxHook;
import me.kryniowesegryderiusz.kgenerators.dependencies.hooks.SuperiorSkyblock2Hook;
import me.kryniowesegryderiusz.kgenerators.generators.generator.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.generators.generator.objects.Generator;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationActionHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationPickUpHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationPlaceHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationRegenerateHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.GeneratorLocationRemoveHandler;
import me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums.InteractionType;
import me.kryniowesegryderiusz.kgenerators.generators.players.objects.GeneratorPlayer;
import me.kryniowesegryderiusz.kgenerators.lang.Lang;
import me.kryniowesegryderiusz.kgenerators.lang.enums.Message;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public class GeneratorLocation implements IGeneratorLocation {
	
	private static GeneratorLocationPickUpHandler pickUpHandler = new GeneratorLocationPickUpHandler();
	private static GeneratorLocationPlaceHandler placeHandler = new GeneratorLocationPlaceHandler();
	private static GeneratorLocationRegenerateHandler regenerateHandler = new GeneratorLocationRegenerateHandler();
	private static GeneratorLocationRemoveHandler removeHandler = new GeneratorLocationRemoveHandler();
	private static GeneratorLocationActionHandler actionHandler = new GeneratorLocationActionHandler();
	
	@Getter Generator generator;
	@Getter GeneratorPlayer owner;
	@Getter Location location;
	@Getter Location hologramLocation;
	
	/**
	 * Creates GeneratorLocation.
	 * **Note that you probably should use GeneratorLocation#save() method**
	 * @param Generator generator
	 * @param Location location
	 * @param GeneratorPlayer owner - nullable
	 */
	public GeneratorLocation(Generator generator, Location location, GeneratorPlayer owner)
	{
		this.generator = generator;
		this.owner = owner;
		this.location = location;
		
		this.hologramLocation = location.clone();
		
		if(generator.getType() == GeneratorType.SINGLE) this.hologramLocation.add(0.5,1,0.5);
		else if(generator.getType() == GeneratorType.DOUBLE) this.hologramLocation.add(0.5,2,0.5);
		
		if (getGenerator().getPlaceholder() != null) this.hologramLocation.add(0,1,0);
	}
	
	/*
	 * Getting GeneratorLocation info
	 */
	
	//api
	public Location getGeneratedBlockLocation() {
		if (this.getGenerator().getType() == GeneratorType.SINGLE)
			return location;
		else
			return location.clone().add(0, 1, 0);
	}
	
	//api
	public boolean isBlockPossibleToMine(Location location) {
		return this.getGeneratedBlockLocation().equals(location) && !this.getGenerator().getPlaceholder().equals(Main.getMultiVersion().getBlocksUtils().getItemStackByBlock(location.getBlock()));
	}
	
	//api
	public boolean isPermittedToMine(Player player)
	{
		String permission = "kgenerators.mine." + this.getGenerator().getId();
		if (!player.hasPermission(permission))
		{
			Lang.getMessageStorage().send(player, Message.GENERATORS_DIGGING_NO_PERMISSION,
					"<permission>", permission,
					"<generator>", this.getGenerator().getGeneratorItem().getItemMeta().getDisplayName());
			return false;
		}
		
		if (Main.getDependencies().isEnabled(Dependency.WORLD_GUARD) && !player.hasPermission("kgenerators.bypass.worldguard") && Main.getMultiVersion().getWorldGuardUtils().worldGuardFlagCheck(location, player, WGFlag.ONLY_GEN_BREAK))
		{
			Lang.getMessageStorage().send(player, Message.GENERATORS_DIGGING_ONLY_GEN);
			return false;
		}
		
		if (!BentoBoxHook.isAllowed(player, BentoBoxHook.Type.USE_FLAG, location) 
				|| !SuperiorSkyblock2Hook.isAllowed(player, SuperiorSkyblock2Hook.Type.USE_FLAG, location))
		{
			Lang.getMessageStorage().send(player, Message.GENERATORS_DIGGING_CANT_HERE);
			return false;
		}
		return true;
	}
	
	/**
	 * Returns whether this generatorLocation is corrupted.
	 * More precisely if there isnt any scheduled regeneration referred to it and there is nothing in place of that generator.
	 * @return
	 */
	//api
	public boolean isBroken()
	{
		if (!Main.getLocations().stillExists(this))
			return false;
		
		if (!Main.getSchedules().isScheduled(this))
		{
			if (Main.getMultiVersion().getBlocksUtils().isAir(this.getGeneratedBlockLocation().getBlock()))
				return true;
		}
		return false;
	}
	
	/*
	 * GeneratorLocation actions
	 */
	
	//api
	public void regenerateGenerator() {
		regenerateHandler.handle(this);
	}
	
	//api
	public void scheduleGeneratorRegeneration()
	{
		Main.getSchedules().schedule(this, false);
	}
	
	//api
	public void removeGenerator(boolean drop, @Nullable Player player)
	{
		removeHandler.handle(this, drop, player);
	}
	
	/**
	 * Places generator and saves it
	 * @return whether placing was successful
	 */
	public boolean placeGenerator(Player player) {
		return placeHandler.handle(this, player);
	}
	
	public void pickUpGenerator(Player player) {
		pickUpHandler.handle(this, player);
	}
	
	/**
	 * @return true if event should be cancelled
	 */
	public boolean handleAction(InteractionType usedActionType, Player player) {
		return actionHandler.handle(this, usedActionType, player);
	}
	
	/**
	 * Saves generator to Manager and/or database
	 * @param saveToDatabase - saves generator also to database
	 */
	public void save(boolean saveToDatabase) {
		Main.getLocations().add(this);
		if (saveToDatabase)
	    	Main.getDatabases().getDb().savePlacedGenerator(this);
	    
		if (!this.owner.isNone()) this.owner.addGeneratorToPlayer(generator);
	}
	
	/**
	 * Changes this generatorLocation to another generator
	 * @param Generator generator
	 */
	//api
	public void changeTo(Generator generator) {
		Logger.info("Generator " + this.generator.getId() +  " placed in " + this.toStringLocation() + " was transformed to " + generator.getId());
		Main.getSchedules().remove(this);
			
		this.generator = generator;
		if (this.getGenerator().getType() == GeneratorType.SINGLE)
		{
			Main.getMultiVersion().getBlocksUtils().setBlock(this.location, new ItemStack(Material.AIR));
		}
		else
		{
			Main.getMultiVersion().getBlocksUtils().setBlock(this.location, this.getGenerator().getGeneratorItem());
			Main.getMultiVersion().getBlocksUtils().setBlock(this.location.clone().add(0,1,0), new ItemStack(Material.AIR));
		}
		
		Main.getDatabases().getDb().savePlacedGenerator(this);
		
		this.regenerateGenerator();
	}
	
	/*
	 * Other
	 */
	
	@Override
	public String toString()
	{
		if (this.owner != null)
			return this.generator.getId() + " owned by " + this.owner.getName() 
			+ " placed in " + toStringLocation();
		else
			return this.generator.getId() + " owned by no one"
			+ " placed in " + toStringLocation();
	}
	
	public String toStringLocation()
	{
		return "world " + this.location.getWorld().getName()
		+ " at " + String.valueOf(this.location.getX()) + ", " + String.valueOf(this.location.getY()) + ", " + String.valueOf(this.location.getZ());
	}
}
