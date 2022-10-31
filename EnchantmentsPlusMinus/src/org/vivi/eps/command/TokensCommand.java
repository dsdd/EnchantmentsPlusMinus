package org.vivi.eps.command;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;
import org.vivi.sekai.Sekai;

public class TokensCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player) && args.length == 0)
		{
			sender.sendMessage(Language.getLangMessage("unspecifiedplayer"));
			return false;
		}

		String targetName = args.length == 0 ? sender.getName() : args[0];
		UUID targetUUID = EPS.getUUID(targetName);
		if (targetUUID == null)
		{
			Language.sendMessage(sender, "invalidplayer");
			return false;
		}
		sender.sendMessage(Language.getLangMessage("tokenbalance")
				.replaceAll("%tokens%",
						ConfigSettings.isAbbreviateLargeNumbers()
								? Sekai.abbreviate(EPS.getEconomy().getBalance(targetUUID))
								: Double.toString(EPS.getEconomy().getBalance(targetUUID)))
				.replaceAll("%player%", targetName));
		return true;
	}
}
