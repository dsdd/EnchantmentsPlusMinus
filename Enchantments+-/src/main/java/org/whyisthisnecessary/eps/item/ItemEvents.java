package org.whyisthisnecessary.eps.item;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;

public class ItemEvents implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) ||	e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			Player p = e.getPlayer();
			ItemStack item = p.getInventory().getItemInMainHand();
			
			if (item.getItemMeta() != null)
			if (item.getItemMeta().hasLore())
			{
				String str = item.getItemMeta().getLore().get(0);
				if (str.startsWith(ChatColor.BLACK+"T:"))
				{
					int tokens = Integer.parseInt(str.split(":")[1]);
					TokenUtil.changeTokens(p, tokens);
					p.getInventory().remove(item);
					p.sendMessage(LangUtil.getLangMessage("claimed-token-pouch").replaceAll("%tokens%", Integer.toString(tokens)));
				}
			}
		}
	}

}
