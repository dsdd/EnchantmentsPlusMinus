package org.vivi.eps.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.util.Language;
import org.vivi.eps.visual.EnchantGUI;

public class EnchantsCommand implements CommandExecutor {

	private static Set<String> guis;
	private static Map<List<Material>, String> list = new HashMap<List<Material>, String>();
	
	public static void setupGUIs()
	{
		guis = EPS.guisFile.getConfigurationSection("guis").getKeys(false);
		for (String i : guis) 
		{
			List<Material> tlist = new ArrayList<Material>();
			for (String i1 : EPS.guisFile.getStringList("guis."+i+".items"))
			{
				if (Material.matchMaterial(i1) != null)
				tlist.add(Material.matchMaterial(i1));
			}
			if (!(tlist.isEmpty()))
			list.put(tlist, i);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player)) {
        	Language.sendMessage(sender, "invalidplayertype");
        	return true;
        }
        Player p = (Player) sender;

        if (p.hasPermission("eps.enchants"))
        {        	
        	for (int i=0;i<list.size();i++)
        	{
        		for (Map.Entry<List<Material>, String> entry : list.entrySet())
                if (entry.getKey().contains(p.getInventory().getItemInMainHand().getType()))
                {
            	    Language.sendMessage(sender, "openenchantsgui");
            	    EnchantGUI.openInventory(p, entry.getValue());
            	    return true;
                }
        	}
        	if (args.length > 0)
        	{
        	if (args[0] != "dontshow")
        		Language.sendMessage(sender, "invaliditem");
        	}
        	else
        	{
        		Language.sendMessage(sender, "invaliditem");
        	}
        	return false;
        }
        else
        {
        	if (args.length > 0)
        	{
        	if (args[0] != "dontshow")
        		Language.sendMessage(sender, "insufficientpermission");
        	}
        	else
        		Language.sendMessage(sender, "insufficientpermission");
        }
		return false;
	}
}
