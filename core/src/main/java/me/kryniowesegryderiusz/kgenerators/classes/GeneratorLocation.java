package me.kryniowesegryderiusz.kgenerators.classes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.handlers.Remove;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;
import me.kryniowesegryderiusz.kgenerators.managers.Schedules;

public class GeneratorLocation {
	@Getter
	String generatorId;
	@Getter
	GeneratorPlayer owner;
	
	@Getter
	Location location;
	
	@Getter
	Location hologramLocation;
	
	/**
	 * 
	 * @param generatorId
	 * @param location
	 * @param owner
	 */
	public GeneratorLocation(String generatorId, Location location, GeneratorPlayer owner)
	{
		this.generatorId = generatorId;
		this.owner = owner;
		this.location = location;
		
		this.hologramLocation = location.clone();
		if(Generators.get(generatorId).getType() == GeneratorType.SINGLE) this.hologramLocation.add(0.5,1,0.5);
		else if(Generators.get(generatorId).getType() == GeneratorType.DOUBLE) this.hologramLocation.add(0.5,2,0.5);
		if (getGenerator().getPlaceholder() != null) this.hologramLocation.add(0,1,0);
	}
	
	public Generator getGenerator()
	{
		return Generators.get(generatorId);
	}
		
	public void regenNow()
	{
		Schedules.scheduleNow(this);
	}
	
	public void regenSchedule()
	{
		Schedules.schedule(this);
	}
	
	public void remove(boolean drop)
	{
		Remove.removeGenerator(this, drop);
	}
	
	public void remove(Player toWho)
	{
		Remove.removeGenerator(this, toWho);
	}
	
	/**
	 * Changes this generatorLocation to another generator
	 * @param Generator generator
	 */
	public void changeTo(Generator generator)
	{
		Logger.info("Generator " + this.generatorId +  " placed in " + this.toStringLocation() + " was transformed to " + generator.getId());
		Schedules.remove(this);
			
		this.generatorId = generator.getId();
		if (this.getGenerator().getType() == GeneratorType.SINGLE)
		{
			Main.getBlocksUtils().setBlock(this.location, new ItemStack(Material.AIR));
		}
		else
		{
			Main.getBlocksUtils().setBlock(this.location, this.getGenerator().getGeneratorBlock());
			Main.getBlocksUtils().setBlock(this.location.clone().add(0,1,0), new ItemStack(Material.AIR));
		}
		
		Main.getDb().savePlacedGenerator(this);
		
		this.regenNow();
	}
	
	/**
	 * Returns whether this generatorLocation is corrupted.
	 * More precisely if there isnt any scheduled regeneration referred to it and there is nothing in place of that generator.
	 * @return
	 */
	public boolean isBroken()
	{
		if (!Locations.exists(this.location))
			return false;
		
		if (!Schedules.getSchedules().containsKey(this))
		{
			if (this.getGenerator().getType() == GeneratorType.SINGLE && Main.getBlocksUtils().isAir(this.location.getBlock()))
				return true;
			if (this.getGenerator().getType() == GeneratorType.DOUBLE && Main.getBlocksUtils().isAir(this.location.clone().add(0,1,0).getBlock()))
				return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		if (this.owner != null)
			return this.generatorId + " owned by " + this.owner.getName() 
			+ " placed in " + toStringLocation();
		else
			return this.generatorId + " owned by no one"
			+ " placed in " + toStringLocation();
	}
	public String toStringLocation()
	{
		return "world " + this.location.getWorld().getName()
		+ " at " + String.valueOf(this.location.getX()) + ", " + String.valueOf(this.location.getY()) + ", " + String.valueOf(this.location.getZ());
	}
}
