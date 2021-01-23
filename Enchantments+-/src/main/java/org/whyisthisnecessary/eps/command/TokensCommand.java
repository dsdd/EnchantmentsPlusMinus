package org.whyisthisnecessary.eps.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;

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
			    Integer tokens = TokenUtil.getTokens(args[0]);
			    sender.sendMessage((LangUtil.getLangMessage("tokenbalance").replaceAll("%tokens%", tokens.toString())));
			    return false;
			}
        }
		if (args.length == 0)
		{
			Integer tokens = TokenUtil.getTokens(sender.getName());
			sender.sendMessage(LangUtil.getLangMessage("tokenbalance").replaceAll("%tokens%", tokens.toString()));
		    return true;
		}
		else
		{
			Integer tokens = TokenUtil.getTokens(args[0]);
			sender.sendMessage(LangUtil.getLangMessage("tokenbalance").replaceAll("%tokens%", tokens.toString()));
		    return false;
		}
	}
}
