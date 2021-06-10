package me.kryniowesegryderiusz.kgenerators.enums;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.classes.StringContent;

public enum HologramText
{
	REMAINING_TIME("remaining-time", new StringContent("", "&6Time left:", "&e<time>", ""));

	@Getter
	String key;
	@Getter
	StringContent stringContent;
	
	HologramText(String key, StringContent stringContent) {
		this.key = key;
		this.stringContent = stringContent;
	}
}
