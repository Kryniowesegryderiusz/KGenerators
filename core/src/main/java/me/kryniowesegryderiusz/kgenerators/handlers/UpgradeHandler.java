package me.kryniowesegryderiusz.kgenerators.handlers;

import org.bukkit.entity.Player;
import me.kryniowesegryderiusz.kgenerators.Lang;
import me.kryniowesegryderiusz.kgenerators.Main;
import me.kryniowesegryderiusz.kgenerators.classes.GeneratorLocation;
import me.kryniowesegryderiusz.kgenerators.classes.Upgrade;
import me.kryniowesegryderiusz.kgenerators.enums.Message;
import me.kryniowesegryderiusz.kgenerators.managers.Locations;

public class UpgradeHandler {

	public static void handleBlockUpgrade(Player p, GeneratorLocation gl) {
		
		if (!Locations.exists(gl.getLocation()))
		{
			Lang.sendMessage(p, Message.GENERATORS_ANY_NO_LONGER_THERE);
			return;
		}
		
		Upgrade upgrade = gl.getGenerator().getUpgrade();
		
		if(Vault.takeMoney(p, upgrade.getCost()))
		{
			gl.changeTo(upgrade.getNextGenerator());
			
			Lang.addReplecable("<cost>", upgrade.getCostFormatted());
			Lang.addReplecable("<number>", String.valueOf(1));
			Lang.sendMessage(p, Message.VAULT_ECONOMY_GENERATOR_UPGRADED);
			
			Main.getSettings().getUpgradeSound().play(p);
		}
		else
		{
			Lang.addReplecable("<cost>", upgrade.getCostFormatted());
			Lang.sendMessage(p, Message.VAULT_ECONOMY_NOT_ENOUGH_MONEY);
		}
	}
	
}
