package me.kryniowesegryderiusz.kgenerators.lang.enums;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IHologramText;
import me.kryniowesegryderiusz.kgenerators.lang.objects.StringContent;

public enum HologramText implements IHologramText
{
	REMAINING_TIME("remaining-time", new StringContent("","<generator_name>", "&6Time left:", "&e<time>", ""));

	@Getter
	String key;
	@Getter
	StringContent stringContent;
	
	HologramText(String key, StringContent stringContent) {
		this.key = key;
		this.stringContent = stringContent;
	}
}
