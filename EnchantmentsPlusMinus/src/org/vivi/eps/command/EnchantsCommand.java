package org.vivi.eps.command;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.util.Language;
import org.vivi.eps.visual.EnchantGUI;

public class EnchantsCommand implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			Language.sendMessage(sender, "invalidplayertype");
			return true;
		}
		Player player = (Player) sender;

		if (player.hasPermission("eps.enchants"))
		{
			for (int i = 0; i < EPS.guis.size(); i++)
			{
				for (Map.Entry<List<Material>, String> entry : EPS.guis.entrySet())
					if (entry.getKey().contains(player.getInventory().getItemInMainHand().getType()))
					{
						Language.sendMessage(sender, "openenchantsgui");
						EnchantGUI.openInventory(player, entry.getValue());
						return true;
					}
			}
			if (args.length > 0)
			{
				if (args[0] != "dontshow")
					Language.sendMessage(sender, "invaliditem");
			} else
			{
				Language.sendMessage(sender, "invaliditem");
			}
			return false;
		} else
		{
			if (args.length > 0)
			{
				if (args[0] != "dontshow")
					Language.sendMessage(sender, "insufficientpermission");
			} else
				Language.sendMessage(sender, "insufficientpermission");
		}
		return false;
	}
}
