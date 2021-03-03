package me.kryniowesegryderiusz.kgenerators;

import lombok.Getter;
import me.kryniowesegryderiusz.kgenerators.classes.MenuItem;
import me.kryniowesegryderiusz.kgenerators.classes.MenuInventory;

public abstract class Enums {
	public enum EnumMessage {
		Prefix("prefix", "&8[&6KGenerators&8] "),
		
		GeneratorsDiggingOnlyGen("generators.digging.only-gen", "&cYou can only dig generated blocks here!"),
		GeneratorsDiggingNoPermission("generators.digging.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to mine <generator>&c!"),
		
    	GeneratorsCraftingCantUse("generators.crafting.cant-use", "&cYou cant craft anything from generator!"),
    	GeneratorsCraftingNoPermission("generators.crafting.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to craft <generator>&c!"),

    	GeneratorsTimeLeftOutput("generators.timeleft.output", "&aThis generator will be regenerated in &e<time>"),
    	
    	GeneratorsPickUpSuccesful("generators.pick-up.successful", "&e<generator> &apicked up"),
    	GeneratorsPickUpCantHere("generators.pick-up.cant-here", "&cYou cant pick up generator here!"),
    	
    	GeneratorsPlaceCantHere("generators.place.cant-here", "&cYou cant place generator here!"),
    	GeneratorsPlaceCantPlaceBlock("generators.place.cant-place-block", "&cYou cant place any block in generator's place"),
    	GeneratorsPlaceDoubleBelowBlock("generators.place.cant-place-double-below-block", "&cYou cant place double generator below any block!"),
    	GeneratorsPlaceDoubleBelowGenerator("generators.place.cant-place-double-below-generator", "&cYou cant place double generator below other generator!"),
    	
    	GeneratorsPPGCantPlaceMore("generators.per-player-generator.cant-place-more", "&cYou cant place more than &6<number> &cgenerators!"),
    	GeneratorsPPGCantPlaceMoreType("generators.per-player-generator.cant-place-more-type", "&cYou cant place more than &6<number> <generator> &cgenerators!"),
    	GeneratorsPPGCantPickUp("generators.per-player-generator.cant-pick-up", "&cYou cant pick up generator owned by &6<owner>&c!"),
    	GeneratorsPPGCantUse("generators.per-player-generator.cant-use", "&cYou cant use generator owned by &6<owner>&c!"),
    	
    	GeneratorsProtectionCantChangeGeneratorItem("generators.protection.cant-change-generator-item", "&cYou cant change generator item!"),
    	
    	VaultEconomyNoEconomyAvaible("economy.no-economy-avaible", "&cThere is no economy system avaible!"),
    	VaultEconomyNotEnoughMoney("economy.not-enough-money", "&cYou dont have enough money! You need &6<cost>$&c!"),
    	VaultEconomyGeneratorUpgraded("economy.done", "&aYou upgraded &e<number> &agenerators for &e<cost>$&a!"),
    	
    	CommandsAnyWrong("commands.any.wrong", "&cWrong command! Type /kgenerators for help"),
    	CommandsAnyNoPermission("commands.any.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to use KGenerators commands"),
    	CommandsAnyPlayerNotOnline("commands.any.player-not-online", "&cPlayer is not online"),
    	CommandsAnyPlayerDoesntExist("commands.any.player-doesnt-exist", "&cPlayer doesnt exist"),
    	
    	CommandsListHeader("commands.list.header", "&aGenerators:"),
    	CommandsListList("commands.list.list", "&8- <generator> &8(&7ID: <generatorID>&8)"),
    	CommandsListNoPermission("commands.list.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to list generators"),
    	CommandsListHelp("commands.list.help", "List all generators"),
    	
    	CommandsGiveGeneratorDoesntExist("commands.give.generator-doesnt-exist", "&cThat generator doesn't exist"),
    	CommandsGiveGeneratorGiven("commands.give.generator-given", "&aGenerator <generator> &awas given to &e<player>"),
    	CommandsGiveGeneratorRecieved("commands.give.generator-recieved", "&aGenerator <generator> &arecieved!"),
    	CommandsGiveUsage("commands.give.usage", "&cUsage: &e/kgenerators give <player> <generator>"),
    	CommandsGiveNoPermission("commands.give.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to give generators"),
    	CommandsGiveHelp("commands.give.help", "Give generator to player"),
    	
    	CommandsGetallRecieved("commands.getall.recieved", "&aGenerators recieved"),
    	CommandsGetallNoPermission("commands.getall.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to get all generators"),
    	CommandsGetallHelp("commands.getall.help", "Get all generators"),
    	
    	CommandsCheckHeader("commands.check.header", "&aPlayer &e<player> &ahas placed:"),
    	CommandsCheckList("commands.check.list", "&8- &e<number> <generator>"),
    	CommandsCheckNoPermission("commands.check.no-permission-own", "&cYou dont have permission &8(&7<permission>&8)&c to check yourself!!"),
    	CommandsCheckNoPermissionOthers("commands.check.no-permission-own", "&cYou dont have permission &8(&7<permission>&8)&c to check others!"),
    	CommandsCheckHelp("commands.check.help", "Check generators and limits of player"),
    	
    	CommandsLimitsHeader("commands.limits.header", "&aPlayer &e<player> &ahas limits:"),
    	CommandsLimitsList("commands.limits.specific", "&8- <generator>&a: &e<limit>"),
    	CommandsLimitsOverall("commands.limits.overall", "&8- &aOverall limit: &e<limit>"),
    	CommandsLimitsNone("commands.limits.None", "None"),
    	
    	CommandsActionsNoPermission("commands.actions.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to check actions!"),
    	CommandsActionsHelp("commands.actions.help", "Check possible actions"),
    	CommandsActionsHeader("commands.actions.header", "&aPossible actions for generators:"),
    	CommandsActionsList("commands.actions.list", "&6<action> &8- &e<sneak><mode><item>"),
    	CommandsActionsSneak("commands.actions.sneak", "sneak and "),
    	CommandsActionsBreak("commands.actions.break", "break block"),
    	CommandsActionsLeftClick("commands.actions.left-click", "left click"),
    	CommandsActionsRightClick("commands.actions.right-click", "right click"),
    	CommandsActionsItem("commands.actions.item", " with <itemname>"),
    	CommandsActionsPickUp("commands.actions.pick-up", "Pick up"),
    	CommandsActionsOpenGui("commands.actions.gui", "Open GUI"),
    	CommandsActionsTimeLeft("commands.actions.time-left", "Time left"),
    	
    	CommandsTimeLeftNoGenerator("commands.timeleft.no-generator", "&cYou are not looking at generator waiting for regeneration!"),
    	CommandsTimeLeftNoPermission("commands.timeleft.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to check generation remaining time"),
    	CommandsTimeLeftHelp("commands.timeleft.help", "Check time remaining to regeneration"),
    	CommandsTimeLeftFormatSec("commands.timeleft.format.seconds", "s"),
    	CommandsTimeLeftFormatMin("commands.timeleft.format.minutes", "m"),
    	CommandsTimeLeftFormatHour("commands.timeleft.format.hours", "h"),
    	CommandsTimeLeftFormatDay("commands.timeleft.format.days", "d"),
    	CommandsTimeLeftFormatNone("commands.timeleft.format.none", "None"),
    	
    	CommandsChancesNoPermission("commands.chances.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to check generators chances!"),
    	CommandsChancesHelp("commands.chances.help", "Checks regeneration chances"),
    	
    	CommandsUpgradeNoNextLevel("commands.upgrade.no-next-level", "&cThis is maximum level of that generator!"),
    	CommandsUpgradeNoPermission("commands.upgrade.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to upgrade generator!"),
    	CommandsUpgradeNotAGenerator("commands.upgrade.no-a-generator", "&cYou dont have generator in hand!"),
    	CommanUpgradeHelp("commands.upgrade.help", "Upgrade generator"),
    	

    	
    	CommandsReloadDone("commands.reload.done", "&aPlugin reloaded! Check console or log.txt in plugin''s directory for possible errors!"),
    	CommandsReloadNoPermission("commands.reload.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to reload plugin"),
    	CommandsReloadHelp("commands.reload.help", "Plugin reload"),
    	
    	CommandsDebugDone("commands.debug.done", "&aYour debug URL is: &e<url>"),
    	CommandsDebugError("commands.debug.error", "&cAn error occured, while making debug output!"),
    	CommandsDebugNoPermission("commands.debug.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to paste debug"),
    	
    	CommandsHelpLabel("commands.help.label", "&6&lKGenerators &aCommands help:"),
    	CommandsHelpFormat("commands.help.format", "&e/kgenerators <subcommand> &7<help>"),
		;
		
    	private String key;
		private String message;
		
		EnumMessage(String key, String message) {
			this.key = key;
			this.message = message;
		}
		
		public String getKey() {
			return this.key;
		}
		public String getDefaultMessage() {
			return this.message;
		}
	}
	
	public static enum EnumHologram
	{
		RemainingTime("remaining-time");

		String key;
		EnumHologram(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return this.key;
		}
	}
	
	public static enum EnumWGFlags
	{
		PICK_UP ("kgenerators-pick-up", true),
		ONLY_GEN_BREAK ("kgenerators-only-gen-break", false);
		
		String flagId;
		Boolean defaultState;

		EnumWGFlags(String flagId, Boolean defaultState) {
			this.flagId = flagId;
			this.defaultState = defaultState;
		}
		
		public String getFlagId()
		{
			return this.flagId;
		}
		
		public Boolean getFlagDefault()
		{
			return this.defaultState;
		}
	}
	
	public static enum EnumAction
	{
		PICKUP,
		OPENGUI,
		TIMELEFT
	}
	
	public static enum EnumInteraction
	{
		BREAK,
		LEFT_CLICK,
		RIGHT_CLICK,
		NONE,
	}
	
	public static EnumInteraction getModeByString(String s)
	{
		for (EnumInteraction mode : EnumInteraction.values())
		{
			if (mode.toString().toLowerCase().equals(s.toLowerCase())) return mode;
		}
		Logger.error("Config file: Action mode " + s + " doesnt exist! Using NONE!");
		return EnumInteraction.NONE;
	}
	
	public static enum EnumLog
	{
		INFO(""),
		ERROR("!!! ERROR !!! "),
		DEBUG("[Debug] "),
		WARNINGS("!!! WARNING !!! "),
	;
		String header;
		
		EnumLog(String header) {
			this.header = header;
		}
		
		public String getHeader()
		{
			return this.header;
		}
	
	}
	
	public static enum GeneratorType
	{
		SINGLE,
		DOUBLE
	}
	
	public static GeneratorType getGeneratorTypeByString(String s)
	{
		for (GeneratorType gen : GeneratorType.values())
		{
			if (gen.toString().toLowerCase().equals(s.toLowerCase())) return gen;
		}
		Logger.error("Generators file: Generator type " + s + " doesnt exist! Using SINGLE!");
		return GeneratorType.SINGLE;
	}
	
	public static enum EnumDependency
	{
		WorldGuard,
		JetsMinions,
		HolographicDisplays,
		SuperiorSkyblock2,
		BentoBox,
		VaultEconomy,
	}
	
	public static enum EnumMenuInventory
	{
		Generator("generator", new MenuInventory("&9Generator info", 45)),
		ChancesList("chances.list", new MenuInventory("&9Blocks chances", 45)),
		ChancesSpecific("chances.specific", new MenuInventory("&9Blocks chances", 45)),
		;

		@Getter
		String key;
		@Getter
		MenuInventory menuInventory;
		EnumMenuInventory(String key, MenuInventory menuInventory) {
			this.key = key;
			this.menuInventory = menuInventory;
		}
	}
	
	public static enum EnumMenuItem
	{
		GeneratorMenuFiller(EnumMenuInventory.Generator, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,37,38,39,40,41,42,43,44")),
		GeneratorMenuTimeLeft(EnumMenuInventory.Generator,"time-left", new MenuItem("CLOCK", "&aTime left for regeneration", false, "21" , "&a<time>")),
		GeneratorMenuOwner(EnumMenuInventory.Generator,"owner", new MenuItem("BOOK", "&aGenerator owner", false, "22", "&e<owner>")),
		GeneratorMenuPickUp(EnumMenuInventory.Generator,"pick-up", new MenuItem("BARRIER", "&aPick up", false, "34", "&aClick here to pick up generator")),
		
		ChancesListMenuFiller(EnumMenuInventory.ChancesList, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44")),
		ChancesListMenuGenerator(EnumMenuInventory.ChancesList,"generator", new MenuItem("<generator>", "&a<generator_name>", false, "9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26" , "&6Click to check", "&6this generator chances")),
		
		ChancesSpecificMenuFiller(EnumMenuInventory.ChancesSpecific, "filler", new MenuItem("GRAY_STAINED_GLASS_PANE", "&r", false, "0,1,2,3,4,5,6,7,8,36,37,38,39,40,41,42,43,44")),
		ChancesSpecificMenuChance(EnumMenuInventory.ChancesSpecific,"block", new MenuItem("<block>", "&a<block_name>", false, "9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26" , "&6Chance: &e<chance>%")),
		ChancesSpecificMenuBack(EnumMenuInventory.ChancesSpecific,"back", new  MenuItem("ARROW", "&cBack", false, "31", "&6Get back to", "&cprevious page"));
		;
		@Getter
		EnumMenuInventory menuInventory;
		String subKey;
		MenuItem menuItem;
		
		EnumMenuItem(EnumMenuInventory menuInventory, String subKey, MenuItem menuItem) {
			this.menuInventory = menuInventory;
			this.subKey = subKey;
			this.menuItem = menuItem;
		}
		
		public String getKey()
		{
			return this.menuInventory.getKey() + "." + this.subKey;
		}
		
		public MenuItem getMenuItem()
		{
			return menuItem;
		}

	}	
}
