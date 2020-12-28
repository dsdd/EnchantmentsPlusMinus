package org.whyisthisnecessary.eps.internal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.Main;

import net.md_5.bungee.api.ChatColor;

public class PayTokensCaller implements CommandExecutor {

    private Main plugin;
	
	String perm = "eps.paytokens";
	
	public PayTokensCaller(Main plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.translatebukkittext("messages.invalidplayertype"));
			return false;
		}
		if (sender.hasPermission(perm))
		{
			if (args.length == 0) {
			sender.sendMessage(plugin.translatebukkittext("messages.unspecifiedplayer"));
			return false;
			}
			if (args.length == 1)  {
			sender.sendMessage(ChatColor.RED+"You must specify the amount of tokens to give.");
			return false;
			}
			
			if (InternalTokenManager.PlayerExists(args[0]))
			{
				try {
				Integer tokens = Integer.parseInt(args[1]);
				InternalTokenManager.ChangeTokens(sender.getName(), -tokens);
				InternalTokenManager.ChangeTokens(args[0], tokens);
				return true;
				}
				catch (NumberFormatException e){
					sender.sendMessage(ChatColor.RED+"You must write a proper amount of tokens to give.");
					return true;
				}
			}
			else
			{
				sender.sendMessage(plugin.translatebukkittext("messages.invalidplayer"));
				return false;
			}
		}
		return false;
	}

}
