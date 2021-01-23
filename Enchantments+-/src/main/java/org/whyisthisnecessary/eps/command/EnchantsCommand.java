package org.whyisthisnecessary.eps.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.visual.EnchantGUI;

public class EnchantsCommand implements CommandExecutor {

	private static Set<String> guis;
	private static List<List<Material>> list;
	private static List<String> listnames;
	
	/** Sets up enchant GUIs for use.
	 * Should never be used by plugins, for internal use only!
	 */
	public static void setupGUIs()
	{
		list = new ArrayList<List<Material>>(Arrays.asList());
		listnames = new ArrayList<String>(Arrays.asList());
		guis = Main.GuisConfig.getConfigurationSection("guis").getKeys(false);
		for (String i : guis) {
			listnames.add(i);
			List<String> slist = Main.GuisConfig.getStringList("guis."+i+".items");
			List<Material> tlist = new ArrayList<Material>(Arrays.asList());
			for (String i1 : slist)
			{
				if (Material.getMaterial(i1) != null)
				tlist.add(Material.getMaterial(i1));
			}
			if (!(tlist.isEmpty()))
			list.add(tlist);
		    }
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player)) {
        	sender.sendMessage(LangUtil.getLangMessage("invalidplayertype"));
        	return true;
        }
        Player p = (Player) sender;

        if (p.hasPermission("eps.enchants"))
        {        	
        	for (int i=0;i<list.size();i++)
        	{
                if (list.get(i).contains(p.getInventory().getItemInMainHand().getType()))
                {
            	    sender.sendMessage(LangUtil.getLangMessage("openenchantsgui"));
            	    EnchantGUI.openInventory(p, listnames.get(i));
            	    return true;
                }
        	}
        	if (args.length > 0)
        	{
        	if (args[0] != "dontshow")
        		sender.sendMessage(LangUtil.getLangMessage("invaliditem"));
        	}
        	else
        	{
        		sender.sendMessage(LangUtil.getLangMessage("invaliditem"));
        	}
        	return false;
        }
        else
        {
        	if (args.length > 0)
        	{
        	if (args[0] != "dontshow")
        		sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
        	}
        	else
        		sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
        }
		return false;
	}
}
