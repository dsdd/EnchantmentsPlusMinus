package org.whyisthisnecessary.eps;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class scrap implements CommandExecutor {

	private Main plugin;
	
	String perm = "eps.scrap";
	
	public scrap(Main plugin){
        this.plugin = plugin;
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
        	sender.sendMessage(plugin.translatebukkittext("messages.invalidplayertype"));
        	return true;
        }
		
        Player p = (Player) sender;
        PlayerInventory pinv = p.getInventory();
        
        if (p.hasPermission(perm))
        {  
        	ItemStack item = pinv.getItemInMainHand();
        	Map<Enchantment, Integer> map = item.getEnchantments();
        	Integer scrapvalue = 0;
        	
        	for (Map.Entry<Enchantment,Integer> entry : map.entrySet())
        	{
        		scrapvalue = scrapvalue + plugin.getConfig().getInt("enchants."+entry.getKey().getKey().getKey().toLowerCase()+".scrapvalue");
        	}
        	
        	if (scrapvalue > 0)
        	{
        	    TokenManager.ChangeTokens(p.getName(),scrapvalue);
        	    pinv.removeItem(pinv.getItemInMainHand());
        	    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aScraped "+scrapvalue.toString()+" tokens!"));
        	}
        	else
        	{
        		p.sendMessage(plugin.translatebukkittext("messages.cannotscrap"));
        	}
        }
        else
        {
        	p.sendMessage(plugin.translatebukkittext("messages.insufficientpermission"));
			return false;
        }
		return false;
	}
	

}
