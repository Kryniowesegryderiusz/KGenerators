package me.kryniowesegryderiusz.kgenerators.classes;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.handlers.Vault;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;

public class Upgrade {

	@Getter
	String generatorId;
	@Getter
	String nextGeneratorId;
	@Getter
	Double cost;
	
	public Upgrade(String generatorId, String nextGeneratorId, Double cost)
	{
		this.generatorId = generatorId;
		this.nextGeneratorId = nextGeneratorId;
		this.cost = cost;
	}
	
	public Generator getNextGenerator()
	{
		return Generators.get(this.nextGeneratorId);
	}
	
	public String getCostFormatted()
	{
		return Vault.formatMoney(this.cost);
	}
}
