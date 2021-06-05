package me.kryniowesegryderiusz.kgenerators.enums;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.api.interfaces.IMessage;

public enum Message implements IMessage {
	Prefix("prefix", "&8[&6KGenerators&8] "),
	
	GENERATORS_ANY_OWNER_NONE("generators.any.owner-none", "&cNone"),
	
	GENERATORS_DIGGING_CANT_HERE("generators.digging.cant-here", "&cYou can't dig generated blocks here!"),
	GENERATORS_DIGGING_ONLY_GEN("generators.digging.only-gen", "&cYou can only dig generated blocks here!"),
	GENERATORS_DIGGING_NO_PERMISSION("generators.digging.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to mine <generator>&c!"),
	
	GENERATORS_CRAFTING_CANT_USE("generators.crafting.cant-use", "&cYou cant craft anything from generator!"),
	GENERATORS_CRAFTING_NO_PERMISSION("generators.crafting.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to craft <generator>&c!"),

	GENERATORS_TIME_LEFT_OUTPUT("generators.timeleft.output", "&aThis generator will be regenerated in &e<time>"),
	
	GENERATORS_PICK_UP_SUCCESFULL("generators.pick-up.successful", "&e<generator> &apicked up"),
	GENERATORS_PICK_UP_CANT_HERE("generators.pick-up.cant-here", "&cYou cant pick up generator here!"),
	
	GENERATORS_PLACE_CANT_HERE("generators.place.cant-here", "&cYou cant place generator here!"),
	GENERATORS_PLACE_CANT_PLACE_BLOCK("generators.place.cant-place-block", "&cYou cant place any block in generator's place"),
	GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_BLOCK("generators.place.cant-place-double-below-block", "&cYou cant place double generator below any block!"),
	GENERATORS_PLACE_CANT_PLACE_DOUBLE_BELOW_GENERATOR("generators.place.cant-place-double-below-generator", "&cYou cant place double generator below other generator!"),
	
	GENERATORS_LIMITS_CANT_MORE("generators.limits.cant-place-more-type", "&cYou cant place more than &6<number> <generator> &cgenerators, because of &6<limit>&c!"),
	GENERATORS_LIMITS_CANT_PICK_UP("generators.limits.cant-pick-up", "&cYou cant pick up generator owned by &6<owner>&c!"),
	GENERATORS_LIMITS_CANT_USE("generators.limits.cant-use", "&cYou cant use generator owned by &6<owner>&c!"),
	
	GENERATOR_PROTECTION_CANT_CHANGE_GENERATOR_ITEM("generators.protection.cant-change-generator-item", "&cYou cant change generator item!"),
	
	GENERATOR_MENU_CANT_OPEN_IN_USE("generators.menus.cant-open-already-viewed", "&cYou cant open this menu right now! Someone is using it!"),
	GENERATOR_MENU_CANT_OPEN_NOT_OWNER("generators.menus.cant-open-not-owner", "&cYou cant open this menu! Only generator owner can do it!"),
	GENERATOR_MENU_CANT_OPEN_HERE("generators.menus.cant-open-here", "&cYou cant open this menu here!"),
	
	VAULT_ECONOMY_NOT_AVAILABLE("economy.no-economy-avaible", "&cThere is no economy system avaible!"),
	VAULT_ECONOMY_NOT_ENOUGH_MONEY("economy.not-enough-money", "&cYou dont have enough money! You need &6<cost>$&c!"),
	VAULT_ECONOMY_GENERATOR_UPGRADED("economy.done", "&aYou upgraded &e<number> &agenerators for &e<cost>$&a!"),
	
	HOOKS_EXPLODE_PICKAXE_CANNOT_USE_ON_DOUBLE("hooks.explode-pickaxe.cannot-use-on-double", "&cYou cannot use explosion pickaxe on type double generator!"),
	
	COMMANDS_ANY_WRONG("commands.any.wrong", "&cWrong command! Type /kgenerators for help"),
	COMMANDS_ANY_NO_PERMISSION("commands.any.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to use KGenerators commands"),
	COMMANDS_ANY_PLAYER_NOT_ONLINE("commands.any.player-not-online", "&cPlayer is not online"),
	COMMANDS_ANY_PLAYER_DOESNT_EXIST("commands.any.player-doesnt-exist", "&cPlayer doesnt exist"),
	COMMANDS_ANY_GENERATOR_DOESNT_EXIST("commands.any.generator-doesnt-exist", "&cThat generator doesn't exist"),
	COMMANDS_ANY_MENU_DOESNT_EXIST("commands.any.menu-doesnt-exist", "&cThat menu doesn't exist"),
	
	COMMANDS_LIST_HEADER("commands.list.header", "&aGenerators:"),
	COMMANDS_LIST_LIST("commands.list.list", "&8- <generator> &8(&7ID: <generatorID>&8)"),
	COMMANDS_LIST_NO_PERMISSION("commands.list.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to list generators"),
	COMMANDS_LIST_HELP("commands.list.help", "List all generators"),
	
	COMMANDS_MENU_OPENED_OTHERS("commands.menu.opened-others", "&aOpened &e<generator> <menu> menu &afor &e<player>"),
	COMMANDS_MENU_ERROR("commands.menu.error", "&cAn error occured, while using menus! Contact with administrator!"),
	COMMANDS_MENU_NO_PERMISSION("commands.menu.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to open menu!"),
	COMMANDS_MENU_NO_PERMISSION_OTHERS("commands.menu.no-permission-others", "&cYou dont have permission &8(&7<permission>&8)&c to open menu for other players!"),
	COMMANDS_MENU_HELP("commands.menu.help", "Opens menu"),
	
	COMMANDS_GIVE_GENERATOR_GIVEN("commands.give.generator-given", "&aGenerator <generator> &awas given to &e<player>"),
	COMMANDS_GIVE_GENERATOR_RECIEVED("commands.give.generator-recieved", "&aGenerator <generator> &arecieved!"),
	COMMANDS_GIVE_USAGE("commands.give.usage", "&cUsage: &e/kgenerators give <player> <generator>"),
	COMMANDS_GIVE_NO_PERMISSION("commands.give.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to give generators"),
	COMMANDS_GIVE_HELP("commands.give.help", "Give generator to player"),
	
	COMMANDS_GET_ALL_RECIEVED("commands.getall.recieved", "&aGenerators recieved"),
	COMMANDS_GET_ALL_NO_PERMISSION("commands.getall.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to get all generators"),
	COMMANDS_GET_ALL_HELP("commands.getall.help", "Get all generators"),
	
	COMMANDS_LIMITS_NO_PERMISSION("commands.limits.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to open limits menu!"),
	COMMANDS_LIMITS_HELP("commands.limits.help", "Check limits"),
	
	COMMANDS_ACTIONS_NO_PERMISSION("commands.actions.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to check actions!"),
	COMMANDS_ACTIONS_HELP("commands.actions.help", "Check possible actions"),
	COMMANDS_ACTIONS_HEADER("commands.actions.header", "&aPossible actions for generators:"),
	COMMANDS_ACTIONS_LIST("commands.actions.list", "&6<action> &8- &e<sneak><mode><item>"),
	COMMANDS_ACTIONS_SNEAK("commands.actions.sneak", "sneak and "),
	COMMANDS_ACTIONS_BREAK("commands.actions.break", "break block"),
	COMMANDS_ACTIONS_LEFT_CLICK("commands.actions.left-click", "left click"),
	COMMANDS_ACTIONS_RIGHT_CLICK("commands.actions.right-click", "right click"),
	COMMANDS_ACTIONS_ITEM("commands.actions.item", " with <itemname>"),
	COMMANDS_ACTIONS_PICK_UP("commands.actions.pick-up", "Pick up"),
	COMMANDS_ACTIONS_OPEN_GUI("commands.actions.gui", "Open GUI"),
	COMMANDS_ACTIONS_TIME_LEFT("commands.actions.time-left", "Time left"),
	
	COMMANDS_TIME_LEFT_NO_GENERATOR("commands.timeleft.no-generator", "&cYou are not looking at generator waiting for regeneration!"),
	COMMANDS_TIME_LEFT_NO_PERMISSION("commands.timeleft.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to check generation remaining time"),
	COMMANDS_TIME_LEFT_HELP("commands.timeleft.help", "Check time remaining to regeneration"),
	COMMANDS_TIME_LEFT_FORMAT_SEC("commands.timeleft.format.seconds", "s"),
	COMMANDS_TIME_LEFT_FORMAT_MIN("commands.timeleft.format.minutes", "m"),
	COMMANDS_TIME_LEFT_FORMAT_HOUR("commands.timeleft.format.hours", "h"),
	COMMANDS_TIME_LEFT_FORMAT_DAY("commands.timeleft.format.days", "d"),
	COMMANDS_TIME_LEFT_FORMAT_NONE("commands.timeleft.format.none", "None"),
	
	COMMANDS_UPGRADE_NO_NEXT_LEVEL("commands.upgrade.no-next-level", "&cThis is maximum level of that generator!"),
	COMMANDS_UPGRADE_NO_PERMISSION("commands.upgrade.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to upgrade generator!"),
	COMMANDS_UPGRADE_NOT_A_GENERATOR("commands.upgrade.no-a-generator", "&cYou dont have generator in hand!"),
	COMMANDS_UPGRADE_HELP("commands.upgrade.help", "Upgrade generator"),
	
	COMMANDS_RELOAD_DONE("commands.reload.done", "&aPlugin reloaded! Check console or log.txt in plugin''s directory for possible errors!"),
	COMMANDS_RELOAD_NO_PERMISSION("commands.reload.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to reload plugin"),
	COMMANDS_RELOAD_HELP("commands.reload.help", "Plugin reload"),
	
	COMMANDS_DEBUG_DONE("commands.debug.done", "&aYour debug URL is: &e<url>"),
	COMMANDS_DEBUG_ERROR("commands.debug.error", "&cAn error occured, while making debug output!"),
	COMMANDS_DEBUG_NO_PERMISSION("commands.debug.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to paste debug"),
	
	COMMANDS_HELP_LABEL("commands.help.label", "&6&lKGenerators &aCommands help:"),
	COMMANDS_HELP_FORMAT("commands.help.format", "&e/kgenerators <subcommand> &7<help>"),
	;
	
	@Getter
	private String key;
	@Getter
	private String message;
	
	Message(String key, String message) {
		this.key = key;
		this.message = message;
	}
}
