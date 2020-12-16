package org.whyisthisnecessary.eps.internal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.Main;

public class GetTokensCaller implements CommandExecutor {

	private Main plugin;
	
	String perm = "eps.tokens";
	
	public GetTokensCaller(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			if (args.length == 0) 
			{
				sender.sendMessage(plugin.translatebukkittext("messages.unspecifiedplayer"));
				return true;
			}
			else
			{
			    Integer tokens = InternalTokenManager.GetTokens(args[0]);
			    sender.sendMessage(String.format(plugin.translatebukkittext("messages.tokenbalance"), tokens.toString()));
			    return false;
			}
        }
		if (args.length == 0)
		{
			Integer tokens = InternalTokenManager.GetTokens(sender.getName());
			sender.sendMessage(String.format(plugin.translatebukkittext("messages.tokenbalance"), tokens.toString()));
		    return true;
		}
		else
		{
			Integer tokens = InternalTokenManager.GetTokens(args[0]);
			sender.sendMessage(String.format(plugin.translatebukkittext("messages.tokenbalance"), tokens.toString()));
		    return false;
		}
	}
}
