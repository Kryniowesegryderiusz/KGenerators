package me.kryniowesegryderiusz.kgenerators.enums;

import me.kryniowesegryderiusz.kgenerators.Logger;

public enum GeneratorType
{
	SINGLE,
	DOUBLE,
	;
	
	public static class Functions
	{
		public static GeneratorType getGeneratorTypeByString(String s)
		{
			for (GeneratorType gen : GeneratorType.values())
			{
				if (gen.toString().toLowerCase().equals(s.toLowerCase())) return gen;
			}
			Logger.error("Generators file: Generator type " + s + " doesnt exist! Using SINGLE!");
			return GeneratorType.SINGLE;
		}
	}
}