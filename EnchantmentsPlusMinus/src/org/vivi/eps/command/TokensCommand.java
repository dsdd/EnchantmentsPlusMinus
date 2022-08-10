package org.vivi.eps.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.libs.NumberUtils;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;

public class TokensCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player) && args.length == 0)
		{
				sender.sendMessage(Language.getLangMessage("unspecifiedplayer"));
				return false;
        }
	
		showTokens(sender, args.length == 0 ? sender.getName() : args[0]);
	    return true;
	}

	private static void showTokens(CommandSender sender, String name) 
	{
		sender.sendMessage(Language.getLangMessage("tokenbalance").replaceAll("%tokens%", ConfigSettings.isAbbreviateLargeNumbers() ? NumberUtils.abbreviate(EPS.getEconomy().getBalance(name)) : Double.toString(EPS.getEconomy().getBalance(name))).replaceAll("%player%", name));
	}
}
