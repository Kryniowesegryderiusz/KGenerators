package me.kryniowesegryderiusz.kgenerators.generators.locations.handlers.enums;

import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public enum InteractionType
{
	BREAK,
	LEFT_CLICK,
	RIGHT_CLICK,
	NONE,
	;
	
	public static class Functions
	{
		public static InteractionType getModeByString(String s)
		{
			for (InteractionType mode : InteractionType.values())
			{
				if (mode.toString().toLowerCase().equals(s.toLowerCase())) return mode;
			}
			Logger.error("Config file: Action mode " + s + " doesnt exist! Using NONE!");
			return InteractionType.NONE;
		}
	}
}