package me.kryniowesegryderiusz.kgenerators.enums;

import me.kryniowesegryderiusz.kgenerators.Logger;

public enum Interaction
{
	BREAK,
	LEFT_CLICK,
	RIGHT_CLICK,
	NONE,
	;
	
	public static class Functions
	{
		public static Interaction getModeByString(String s)
		{
			for (Interaction mode : Interaction.values())
			{
				if (mode.toString().toLowerCase().equals(s.toLowerCase())) return mode;
			}
			Logger.error("Config file: Action mode " + s + " doesnt exist! Using NONE!");
			return Interaction.NONE;
		}
	}
}