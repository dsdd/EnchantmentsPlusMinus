package org.whyisthisnecessary.eps.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.util.DataUtil;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;

public class PayTokensCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player)) {
			sender.sendMessage(LangUtil.getLangMessage("invalidplayertype"));
			return false;
		}
		if (sender.hasPermission("eps.paytokens"))
		{
			if (args.length == 0) {
			sender.sendMessage(LangUtil.getLangMessage("unspecifiedplayer"));
			return false;
			}
			if (args.length == 1)  {
			sender.sendMessage(LangUtil.getLangMessage("unspecifiedtokenspay"));
			return false;
			}
			
			if (DataUtil.playerExists(args[0]))
			{
				try {
				Integer tokens = Integer.parseInt(args[1]);
				TokenUtil.changeTokens(sender.getName(), -tokens);
				TokenUtil.changeTokens(args[0], tokens);
				return true;
				}
				catch (NumberFormatException e){
					sender.sendMessage(LangUtil.getLangMessage("invalidtokenamountpay"));
					return true;
				}
			}
			else
			{
				sender.sendMessage(LangUtil.getLangMessage("invalidplayer"));
				return false;
			}
		}
		return false;
	}
}
