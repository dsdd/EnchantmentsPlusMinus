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
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.util.Language;

public class ScrapCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (!(sender instanceof Player)) {
        	sender.sendMessage(Language.getLangMessage("invalidplayertype"));
        	return false;
        }
		
        Player p = (Player) sender;
        PlayerInventory pinv = p.getInventory();
        
        if (p.hasPermission("eps.scrap"))
        {  
        	ItemStack item = pinv.getItemInMainHand();
        	Map<Enchantment, Integer> map = item.getItemMeta().getEnchants();
        	int scrapvalue = 0;
        	
        	for (Map.Entry<Enchantment,Integer> entry : map.entrySet())
        	{
        		scrapvalue = scrapvalue + EPSConfiguration.getConfiguration(entry.getKey()).getInt("scrapvalue")*entry.getValue();
        	}
        	
        	if (scrapvalue > 0)
        	{
        	    EPS.getEconomy().changeBalance(p.getName(),scrapvalue);
        	    pinv.removeItem(pinv.getItemInMainHand());
        	    p.sendMessage(Language.getLangMessage("scrapsuccess").replaceAll("%tokens%", Integer.toString(scrapvalue)));
        	}
        	else
        	{
        		p.sendMessage(Language.getLangMessage("cannotscrap"));
        	}
        }
        else
        {
        	p.sendMessage(Language.getLangMessage("insufficientpermission"));
			return false;
        }
		return false;
	}
}
