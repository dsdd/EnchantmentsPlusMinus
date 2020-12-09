package org.whyisthisnecessary.eps;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gettokens implements CommandExecutor {

	private Main plugin;
	
	String perm = "eps.tokens";
	
	public gettokens(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			if (args.length == 0) 
			{
				sender.sendMessage(translatebukkittext("messages.unspecifiedplayer"));
				return true;
			}
			else
			{
			    Integer tokens = TokenManager.GetTokens(args[0]);
			    sender.sendMessage(String.format(translatebukkittext("messages.tokenbalance"), tokens.toString()));
			    return false;
			}
        }
		if (args.length == 0)
		{
			Integer tokens = TokenManager.GetTokens(sender.getName());
			sender.sendMessage(String.format(translatebukkittext("messages.tokenbalance"), tokens.toString()));
		    return true;
		}
		else
		{
			Integer tokens = TokenManager.GetTokens(args[0]);
			sender.sendMessage(String.format(translatebukkittext("messages.tokenbalance"), tokens.toString()));
		    return false;
		}
	}

	public String translatebukkittext(String text)
	{
		return plugin.config.getString("prefix") + ChatColor.translateAlternateColorCodes('&', plugin.config.getString(text));
	}
}
