package me.kryniowesegryderiusz.KGenerators.Classes;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.KGenerators.KGenerators;

public class GeneratorLocation {
	String generatorId;
	Player owner;
	
	public GeneratorLocation(String generatorId, Player owner)
	{
		this.generatorId = generatorId;
		this.owner = owner;
	}
	
	public Generator getGenerator()
	{
		return KGenerators.generators.get(generatorId);
	}
	
	public String getGeneratorId()
	{
		return generatorId;
	}
	
	public Player getOwner()
	{
		return this.owner;
	}
	
}
