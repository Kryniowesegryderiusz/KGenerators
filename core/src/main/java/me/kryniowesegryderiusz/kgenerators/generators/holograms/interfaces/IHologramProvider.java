package me.kryniowesegryderiusz.kgenerators.generators.holograms.interfaces;

import java.util.ArrayList;

import me.kryniowesegryderiusz.kgenerators.generators.locations.objects.GeneratorLocation;

public interface IHologramProvider {
  void createHologram(GeneratorLocation gLocation, ArrayList<String> lines);
  
  void updateHologram(GeneratorLocation gLocation, ArrayList<String> lines);
  
  void removeHologram(GeneratorLocation gLocation);
}
