package me.kryniowesegryderiusz.kgenerators.enums;

import lombok.Getter;

public enum WGFlag
{
	PICK_UP ("kgenerators-pick-up", true),
	ONLY_GEN_BREAK ("kgenerators-only-gen-break", false);
	
	@Getter
	String flagId;
	@Getter
	Boolean defaultState;

	WGFlag(String flagId, Boolean defaultState) {
		this.flagId = flagId;
		this.defaultState = defaultState;
	}
}
