package me.kryniowesegryderiusz.kgenerators.classes;

import org.bukkit.Location;
import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.Enums.GeneratorType;
import me.kryniowesegryderiusz.kgenerators.handlers.Remove;
import me.kryniowesegryderiusz.kgenerators.managers.Generators;
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
}
