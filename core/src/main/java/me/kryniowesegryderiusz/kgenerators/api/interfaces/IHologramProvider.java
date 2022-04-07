package me.kryniowesegryderiusz.kgenerators.api.interfaces;

import java.util.ArrayList;
import org.bukkit.Location;

public interface IHologramProvider {
  void createHologram(Location paramLocation, ArrayList<String> paramArrayList);
  
  void updateHologramLine(Location paramLocation, int paramInt, String paramString);
  
  void removeHologram(Location paramLocation);
}
