package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;

public interface IHologramProvider {
	
	public void createGeneratorHologram(GeneratorLocation gLocation);
	
	public void removeHologram(GeneratorLocation gLocation);

}
