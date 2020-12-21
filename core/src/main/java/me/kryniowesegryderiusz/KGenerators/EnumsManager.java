package me.kryniowesegryderiusz.KGenerators;

public abstract class EnumsManager {
	public enum Message {
		Prefix("prefix", "&8[&6KGenerators&8] "),
    	
    	GeneratorsCantBreak("generators.cant-break", "&cYou should sneak to pick up this generator!"),
    	GeneratorsCantPickUpHere("generators.cant-pick-up", "&cYou cant pick up generator here!"),
    	GeneratorsOnlyGenBreakHere("generators.only-gen-break", "&cYou can only dig generated blocks here!"),
    	GeneratorsCantCraft("generators.cant-craft", "&cYou cant craft anything from generator!"),
    	GeneratorsPickedUp("generators.picked-up", "&e<generator> &apicked up"),
    	GeneratorsNoCraftPermission("generators.no-craft-permission", "&cYou dont have permission &8(&7<permission>&8)&c to craft <generator>&c!"),
    	
    	GeneratorsPPGCantPlaceMore("generators.per-player-generator.cant-place-more", "&cYou cant place more than &6<number> &cgenerators!"),
    	GeneratorsPPGCantPlaceMoreType("generators.per-player-generator.cant-place-more-type", "&cYou cant place more than &6<number> <generator> &cgenerators!"),
    	GeneratorsPPGCantPickUp("generators.per-player-generator.cant-pick-up", "&cYou cant pick up generator owned by &6<owner>&c!"),
    	GeneratorsPPGCantUse("generators.per-player-generator.cant-use", "&cYou cant use generator owned by &6<owner>&c!"),
    	
    	CommandsAnyWrong("commands.any.wrong", "&cWrong command! Type /kgenerators for help"),
    	CommandsAnyNoPermission("commands.any.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to use KGenerators commands"),
    	CommandsAnyPlayerNotOnline("commands.any.player-not-online", "&cPlayer is not online"),
    	CommandsAnyPlayerDoesntExist("commands.any.player-doesnt-exist", "&cPlayer doesnt exist"),
    	
    	CommandsListHeader("commands.list.header", "&aGenerators:"),
    	CommandsListList("commands.list.list", "&8- <generator> &8(&7ID: <generatorID>&8)"),
    	CommandsListNoPermission("commands.list.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to list generators"),
    	CommandsListHelp("commands.list.help", "List all generators"),
    	
    	CommandsGiveGeneratorDoesntExist("commands.give.generator-doesnt-exist", "&cThat generator doesn't exist"),
    	CommandsGiveGeneratorGiven("commands.give.generator-given", "&aGenerator <generator> was given to <player>"),
    	CommandsGiveGeneratorRecieved("commands.give.generator-recieved", "&aGenerator <generator> recieved!"),
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
    	
    	
    	CommandsReloadDone("commands.reload.done", "&aConfig and messages reloaded"),
    	CommandsReloadNoPermission("commands.reload.no-permission", "&cYou dont have permission &8(&7<permission>&8)&c to reload config"),
    	CommandsReloadHelp("commands.reload.help", "Reload config and messages"),
    	
    	CommandsHelpLabel("commands.help.label", "&6&lKGenerators &aCommands help:"),
    	CommandsHelpFormat("commands.help.format", "&e/kgenerators <subcommand> &7<help>"),
		;
		
    	private String key;
		private String message;
		
		Message(String key, String message) {
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
	
	public static enum EnumWGFlags
	{
		PICK_IP ("kgenerators-pick-up", true),
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

}
