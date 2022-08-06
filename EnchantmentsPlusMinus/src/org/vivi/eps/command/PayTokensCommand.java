package org.vivi.eps.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.economy.Economy;
import org.vivi.eps.util.DataUtil;
import org.vivi.eps.util.LangUtil;

public class PayTokensCommand implements CommandExecutor {

	private Economy economy = EPS.getEconomy();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player)) {
			LangUtil.sendMessage(sender, "invalidplayertype");
			return false;
		}
		if (sender.hasPermission("eps.paytokens"))
		{
			if (args.length == 0) {
			LangUtil.sendMessage(sender, "unspecifiedplayer");
			return false;
			}
			if (args.length == 1)  {
			LangUtil.sendMessage(sender, "unspecifiedtokenspay");
			return false;
			}
			
			if (DataUtil.playerExists(args[0]))
			{
				try {
				Integer tokens = Integer.parseInt(args[1]);
				if (economy.getBalance((Player)sender) < tokens)
				{
					LangUtil.sendMessage(sender, "invalidtokenamountpay");
					return true;
				}
				economy.changeBalance((Player)sender, -tokens);
				economy.changeBalance(args[0], tokens);
				return true;
				}
				catch (NumberFormatException e){
					LangUtil.sendMessage(sender, "invalidtokenamountpay");
					return true;
				}
			}
			else
			{
				LangUtil.sendMessage(sender, "invalidplayer");
				return false;
			}
		}
		return false;
	}
}
