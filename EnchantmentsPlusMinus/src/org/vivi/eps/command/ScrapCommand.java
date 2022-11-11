package org.vivi.eps.command;

import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.vivi.eps.EPS;
import org.vivi.eps.util.Language;
import org.vivi.sekai.Sekai;

/**
 * Planned for removal. Though, this concept may still be improved on.
 * 
 * @author vivisan
 *
 */
public class ScrapCommand implements CommandExecutor
{

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(Language.getLangMessage("invalidplayertype"));
			return false;
		}

		Player player = (Player) sender;
		PlayerInventory playerInventory = player.getInventory();

		if (player.hasPermission("eps.scrap"))
		{
			ItemStack item = Sekai.getMCVersion() < 9 ? playerInventory.getItemInHand()
					: playerInventory.getItemInMainHand();
			Map<Enchantment, Integer> map = item.getItemMeta().getEnchants();
			int scrapvalue = 0;

			for (Map.Entry<Enchantment, Integer> entry : map.entrySet())
			{
				scrapvalue += EPS.getEnchantFile(entry.getKey()).getScrapValue() * entry.getValue();
			}

			if (scrapvalue > 0)
			{
				EPS.getEconomy().changeBalance(player, scrapvalue);
				playerInventory.removeItem(
						Sekai.getMCVersion() < 9 ? playerInventory.getItemInHand() : playerInventory.getItemInMainHand());
				player.sendMessage(
						Language.getLangMessage("scrapsuccess").replaceAll("%tokens%", Integer.toString(scrapvalue)));
			} else
			{
				player.sendMessage(Language.getLangMessage("cannotscrap"));
			}
		} else
		{
			player.sendMessage(Language.getLangMessage("insufficientpermission"));
			return false;
		}
		return false;
	}
}
