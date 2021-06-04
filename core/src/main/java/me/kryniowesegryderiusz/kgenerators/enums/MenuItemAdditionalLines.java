package me.kryniowesegryderiusz.kgenerators.enums;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.classes.StringContent;

public enum MenuItemAdditionalLines implements IMenuItemAdditionalLines
{
	RECIPE("recipe", new StringContent("", "&6Right click:", "&echeck the recipe")),
	UPGRADE("upgrade", new StringContent("", "&6Right click:", "&echeck how to", "&eget from upgrade")),
	LIMITS_GENERATOR_LIST("limits.generator-list", new StringContent("&8- &e<generator_name>")),
	LIMITS_ALL_GENERATORS("limits.all-generators", new StringContent("&eall generators")),
	LIMITS_ONLY_OWNER_PICK_UP("limits.only-owner-pick-up", new StringContent("&cOnly owners could pick up them.")),
	LIMITS_ONLY_OWNER_USE("limits.only-owner-use", new StringContent("&cOnly owners could use them.")),
	;

	@Getter
	String key;
	@Getter
	StringContent stringContent;
	MenuItemAdditionalLines(String key, StringContent stringContent) {
		this.key = key;
		this.stringContent = stringContent;
	}
}