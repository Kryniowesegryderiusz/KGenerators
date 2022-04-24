package me.kryniowesegryderiusz.kgenerators.generators.generator.enums;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.logger.Logger;

public enum GeneratingType {
	
	BLOCK(true),
	ITEM(false)
	;
	
	@Getter boolean canBePushedByPiston;
	
	GeneratingType(boolean canBePushedByPiston) {
		this.canBePushedByPiston = canBePushedByPiston;
	}

	public static GeneratingType getGeneratorTypeByString(String s)
	{
		if (s == null) s = "";
		for (GeneratingType gen : GeneratingType.values())
		{
			if (gen.toString().toLowerCase().equals(s.toLowerCase())) return gen;
		}
		Logger.error("Generators file: Generating type " + s + " doesnt exist! Using BLOCK!");
		return GeneratingType.BLOCK;
	}

}
