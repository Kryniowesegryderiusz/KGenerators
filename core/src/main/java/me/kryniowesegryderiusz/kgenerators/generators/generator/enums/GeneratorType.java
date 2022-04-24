package me.kryniowesegryderiusz.kgenerators.generators.generator.enums;

import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public enum GeneratorType
{
	SINGLE,
	DOUBLE,
	;
	
	public static GeneratorType getGeneratorTypeByString(String s)
	{
		if (s == null) s = "";
		for (GeneratorType gen : GeneratorType.values())
		{
			if (gen.toString().toLowerCase().equals(s.toLowerCase())) return gen;
		}
		Logger.error("Generators file: Generator type " + s + " doesnt exist! Using SINGLE!");
		return GeneratorType.SINGLE;
	}
}