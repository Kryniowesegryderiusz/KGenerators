package me.kryniowesegryderiusz.kgenerators.classes;

import java.util.Optional;

import org.bukkit.entity.Player;

import me.kryniowesegryderiusz.kgenerators.Logger;
import me.kryniowesegryderiusz.kgenerators.utils.Config;
import me.kryniowesegryderiusz.kgenerators.xseries.XSound;

public class Sound {
	
	XSound xsound;
	float volume = 0.2f;
	float pitch = 0.0f;
	
	/**
	 * Constructor loading sound from config
	 * @param cfg
	 * @param path to sound
	 */
	public Sound(Config cfg, String path)
	{
		if(cfg.contains(path+".sound"))
		{
			String name = cfg.getString(path+".sound");
			if (!name.toLowerCase().equals("none"))
			{
				Optional<XSound> xso = XSound.matchXSound(name);
				if (!xso.isPresent())
				{
					Logger.error("Config file: Sound " + name + " doesnt exist! It'll not work!");
					return;
				}
				this.xsound = xso.get();
				
				if (cfg.contains(path+".volume"))
					this.volume = (float) cfg.getDouble(path+".volume");
				if (cfg.contains(path+".pitch"))
					this.pitch = (float) cfg.getDouble(path+".pitch");
			}
		}
	}
	
	public Sound(XSound xsound)
	{
		this.xsound = xsound;
	}
	
	public void play(Player p)
	{
		if (this.xsound != null)
			this.xsound.play(p, this.volume, this.pitch);
	}

}
