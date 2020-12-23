package org.whyisthisnecessary.eps.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.whyisthisnecessary.eps.Main;

public class EnchantGUICaller implements Listener, CommandExecutor {

	private Main plugin;
	private EnchantGUI EnchantGUI;
	private Set<String> guis;
	private List<List<Material>> list;
	private List<String> listnames;
	
	String perm = "eps.enchants";
	
	public EnchantGUICaller(Main plugin) {
		this.plugin = plugin;
		EnchantGUI = new EnchantGUI(plugin);
		list = new ArrayList<List<Material>>(Arrays.asList());
		listnames = new ArrayList<String>(Arrays.asList());
		guis = plugin.config.getConfigurationSection("guis").getKeys(false);
		for (String i : guis) {
			listnames.add(i);
			List<String> slist = plugin.config.getStringList("guis."+i+".items");
			List<Material> tlist = new ArrayList<Material>(Arrays.asList());
			for (String i1 : slist)
			{
				tlist.add(Material.getMaterial(i1));
			}
			if (!(tlist.isEmpty()))
			list.add(tlist);
		    }
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
        	sender.sendMessage(plugin.translatebukkittext("messages.invalidplayertype"));
        	return true;
        }
        Player p = (Player) sender;
        
        if (p.hasPermission(perm))
        {        	
        	for (int i=0;i<list.size();i++)
        	{
                if (list.get(i).contains(p.getInventory().getItemInMainHand().getType()))
                {
            	    sender.sendMessage(plugin.translatebukkittext("messages.openenchantsgui"));
            	    EnchantGUI.OpenInventory(p, listnames.get(i));
            	    return true;
                }
        	}
        	sender.sendMessage(plugin.translatebukkittext("messages.invaliditem"));
        	return false;
        }
        else
        {
        	sender.sendMessage(plugin.translatebukkittext("messages.insufficientpermission"));
        }
		return false;
	}
}
