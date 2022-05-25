package me.kryniowesegryderiusz.kgenerators.lang.enums;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.lang.interfaces.IMenuItemAdditionalLines;
import me.kryniowesegryderiusz.kgenerators.lang.objects.StringContent;

public enum MenuItemAdditionalLines implements IMenuItemAdditionalLines
{
	RECIPE("recipe.menu-how-to", new StringContent("", "&6Right click:", "&echeck the recipe")),
	UPGRADE("upgrades.menu-how-to", new StringContent("", "&6Right click:", "&echeck how to", "&eget from upgrade")),
	UPGRADE_COST("upgrades.upgrade-cost", new StringContent("&8- &7<cost>")),
	GENERATED_OBJECT_TYPE_ITEM("generated-object-type.item", new StringContent("&6Generates: &eitem")),
	GENERATED_OBJECT_TYPE_BLOCK("generated-object-type.block", new StringContent("&6Generates: &eblock")),
	GENERATED_OBJECT_TYPE_ENTITY("generated-object-type.block", new StringContent("&6Generates: &eentity")),
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