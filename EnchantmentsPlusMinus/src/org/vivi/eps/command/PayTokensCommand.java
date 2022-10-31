package org.vivi.eps.command;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EPSPlayerData;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.economy.Economy;

public class PayTokensCommand implements CommandExecutor
{

	private Economy economy = EPS.getEconomy();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			Language.sendMessage(sender, "invalidplayertype");
			return false;
		}
		if (sender.hasPermission("eps.paytokens"))
		{
			if (args.length == 0)
			{
				Language.sendMessage(sender, "unspecifiedplayer");
				return false;
			}
			if (args.length == 1)
			{
				Language.sendMessage(sender, "unspecifiedtokenspay");
				return false;
			}

			UUID targetUUID = EPSPlayerData.getUUID(args[0]);
			if (targetUUID != null)
			{
				try
				{
					int tokens = Integer.parseInt(args[1]);
					
					if (economy.getBalance((Player) sender) < tokens)
					{
						Language.sendMessage(sender, "invalidtokenamountpay");
						return true;
					}
					economy.changeBalance((Player) sender, -tokens);
					economy.changeBalance(targetUUID, tokens);
					return true;
				} 
				catch (NumberFormatException e)
				{
					Language.sendMessage(sender, "invalidtokenamountpay");
					return true;
				}
			} else
			{
				Language.sendMessage(sender, "invalidplayer");
				return false;
			}
		}
		return false;
	}
}
