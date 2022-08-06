package org.vivi.eps.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.api.CustomEnchant;
import org.vivi.eps.item.TokenPouch;
import org.vivi.eps.util.Language;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.eps.workbench.CustomEnchantedBook;

public class EPSCommand implements CommandExecutor, TabCompleter {
	
	private List<String> enchantTabList = new ArrayList<String>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			for (String line : EPS.languageData.getStringList("eps-command-usage"))
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
			return false;
		}
		
		if (args[0].equalsIgnoreCase("reload"))
		{
			String perm = "eps.admin.reload";
			if (!sender.hasPermission(perm))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}
			EPS.reloadConfigs();
			Language.sendMessage(sender, "reloadconfig");
			return false;
		}
		
		if (args[0].equalsIgnoreCase("setbal"))
		{
			String perm = "eps.admin.setbal";
			if (!sender.hasPermission(perm))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps setbal [player] [amount]"));
				return true;
			}
			try
			{
			EPS.getEconomy().setBalance(args[1],Integer.parseInt(args[2]));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSet "+args[1]+"'s balance to "+Integer.parseInt(args[2])));
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps setbal [player] [amount]"));
			}
			return false;
		}
		
		if (args[0].equalsIgnoreCase("changebal"))
		{
			String perm = "eps.admin.changebal";
			if (!sender.hasPermission(perm))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changebal [player] [amount]"));
				return true;
			}
			try
			{
			EPS.getEconomy().changeBalance(args[1],Integer.parseInt(args[2]));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aChanged "+args[1]+"'s balance by "+Integer.parseInt(args[2])));
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changebal [player] [amount]"));
			}
			return false;
		}
		
		if (args[0].equalsIgnoreCase("enchant"))
		{
			String perm = "eps.admin.enchant";
			
			if (!(sender instanceof Player))
			{
				Language.sendMessage(sender, "invalidplayertype");
				return true;
			}
			
			Player p = (Player) sender;
			
			if (p.hasPermission(perm))
	        {
				if (args.length < 3)
				{
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps enchant [enchant] [lvl]"));
					return true;
				}
				if (p.getInventory().getItemInMainHand().getAmount() > 0)
				{
					int num = Integer.parseInt(args[2]);
					Enchantment enchant = EPS.getDictionary().findEnchant(args[1].toLowerCase());
					if (enchant == null)
					{
						Language.sendMessage(p, "invalid-enchant");
						return true;
					}
					if (num != 0)
						p.getInventory().getItemInMainHand().addUnsafeEnchantment(enchant, num);
					else
						p.getInventory().getItemInMainHand().removeEnchantment(enchant);
					ItemMeta meta = EnchantMetaWriter.getWrittenMeta(p.getInventory().getItemInMainHand());
		        	p.getInventory().getItemInMainHand().setItemMeta(meta);
					return true;
				}
				else
				{
					Language.sendMessage(p, "invaliditem");
					return false;
				}
	        }
			else
			{
				Language.sendMessage(p, "insufficientpermission");
				return false;
			}
		}
		if (args[0].equalsIgnoreCase("book"))
		{
			if (!sender.hasPermission("eps.admin.book"))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.RED+"Usage: /eps book [player] [enchant:lvl] [additionalenchs]");
				return false;
			}
			
			Player p = Bukkit.getPlayer(args[1]);
			if (p == null)
			{
				Language.sendMessage(sender, "invalidplayer");
				return false;
			}
			if (p.getInventory().firstEmpty() == -1)
			{
				Language.sendMessage(sender, "inventoryfull");
				return false;
			}
			
			Map<Enchantment, Integer> map = new HashMap<Enchantment, Integer>();
			
			for (int i=2;i<args.length;i++)
			{
				String[] parts = args[i].split(":");
				
				if (parts.length == 0)
					continue;
				
				Enchantment enchant = EPS.getDictionary().findEnchant(parts[0]);
				if (parts.length == 1)
				{
					map.put(enchant, 1);
					continue;
				}
				
				
				Integer lvl = Integer.parseInt(parts[1]);
				if (enchant == null)
				{
					Language.sendMessage(sender, ChatColor.RED+"Invalid enchant "+EPS.getDictionary().getName(enchant).toUpperCase()+"!");
					continue;
				}
				map.put(enchant, lvl);
			}
			
			CustomEnchantedBook book = new CustomEnchantedBook(map);
			book.setItemMeta(EnchantMetaWriter.getWrittenMetaBook(book));
			p.getInventory().addItem(book);
		}
		if (args[0].equalsIgnoreCase("tokenpouch"))
		{
			if (!sender.hasPermission("eps.admin.book"))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}
			Player p = Bukkit.getPlayer(args[1]);
			if (p == null)
			{
				Language.sendMessage(sender, "invalidplayer");
				return false;
			}
			if (args.length < 1)
			{
				sender.sendMessage(ChatColor.RED+"Usage: /eps tokenpouch [player] [tokens]");
				return false;
			}
			p.getInventory().addItem(new TokenPouch(Integer.parseInt(args[2])));
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(cmd.getName() == "eps"))
			return null;
		
		if (enchantTabList == null)
			for (Enchantment e : CustomEnchant.registeredEnchants)
				enchantTabList.add(EPS.getDictionary().getName(e));
		
		if (args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("book"))
			return enchantTabList;
		
		return null;
	}
}