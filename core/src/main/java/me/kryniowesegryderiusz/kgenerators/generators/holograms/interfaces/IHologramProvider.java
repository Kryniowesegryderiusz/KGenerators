package me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces;

import org.bukkit.Location;

import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public interface IHologramProvider {
  void createHologram(GeneratorLocation gLocation);
  
  void updateHologramLine(GeneratorLocation gLocation, int paramInt, String paramString);
  
  void removeHologram(GeneratorLocation gLocation);
}
