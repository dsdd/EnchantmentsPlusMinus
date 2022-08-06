package org.vivi.eps.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.util.LangUtil;

public class TokensCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player)) {
			if (args.length == 0) 
			{
				sender.sendMessage(LangUtil.getLangMessage("unspecifiedplayer"));
				return true;
			}
			else
			{
			    Integer tokens = EPS.getEconomy().getBalance(args[0]);
			    sender.sendMessage((LangUtil.getLangMessage("tokenbalance").replaceAll("%tokens%", tokens.toString())));
			    return false;
			}
        }
		if (args.length == 0)
		{
			Integer tokens = EPS.getEconomy().getBalance(sender.getName());
			sender.sendMessage(LangUtil.getLangMessage("tokenbalance").replaceAll("%tokens%", tokens.toString()));
		    return true;
		}
		else
		{
			Integer tokens = EPS.getEconomy().getBalance(args[0]);
			sender.sendMessage(LangUtil.getLangMessage("tokenbalance").replaceAll("%tokens%", tokens.toString()));
		    return false;
		}
	}
}
