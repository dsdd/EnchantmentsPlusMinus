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
import org.vivi.eps.util.LangUtil;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.eps.workbench.CustomEnchantedBook;

public class EPSCommand implements CommandExecutor, TabCompleter {
	
	private List<String> enchantTabList = new ArrayList<String>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "Usage:");
			sender.sendMessage(ChatColor.RED + "/eps reload");
			sender.sendMessage(ChatColor.RED + "/eps settokens [plr] [amount]");
			sender.sendMessage(ChatColor.RED + "/eps changetokens [plr] [amount]");
			sender.sendMessage(ChatColor.RED + "/eps give [plr] [amount]");
			sender.sendMessage(ChatColor.RED + "/eps take [plr] [amount]");
			sender.sendMessage(ChatColor.RED + "/eps enchant [enchant] [lvl]");
			sender.sendMessage(ChatColor.RED + "/eps book [player] [enchant:lvl]");
			sender.sendMessage(ChatColor.RED + "/eps tokenpouch [player] [tokens]");
			return false;
		}
		
		if (args[0].equalsIgnoreCase("reload"))
		{
			String perm = "eps.admin.reload";
			if (!sender.hasPermission(perm))
			{
				LangUtil.sendMessage(sender, "insufficientpermission");
				return false;
			}
			EPS.reloadConfigs();
			LangUtil.sendMessage(sender, "reloadconfig");
			return false;
		}
		
		if (args[0].equalsIgnoreCase("settokens"))
		{
			String perm = "eps.admin.settokens";
			if (!sender.hasPermission(perm))
			{
				LangUtil.sendMessage(sender, "insufficientpermission");
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps settokens [player] [amount]"));
				return true;
			}
			try
			{
			EPS.getEconomy().setBalance(args[1],Integer.parseInt(args[2]));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSet "+args[1]+"'s tokens to "+Integer.parseInt(args[2])));
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps settokens [player] [amount]"));
			}
			return false;
		}
		
		if (args[0].equalsIgnoreCase("changetokens"))
		{
			String perm = "eps.admin.changetokens";
			if (!sender.hasPermission(perm))
			{
				LangUtil.sendMessage(sender, "insufficientpermission");
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changetokens [player] [amount]"));
				return true;
			}
			try
			{
			EPS.getEconomy().changeBalance(args[1],Integer.parseInt(args[2]));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aChanged "+args[1]+"'s tokens by "+Integer.parseInt(args[2])));
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changetokens [player] [amount]"));
			}
			return false;
		}
		
		if (args[0].equalsIgnoreCase("enchant"))
		{
			String perm = "eps.admin.enchant";
			
			if (!(sender instanceof Player))
			{
				LangUtil.sendMessage(sender, "invalidplayertype");
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
						LangUtil.sendMessage(p, "invalid-enchant");
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
					LangUtil.sendMessage(p, "invaliditem");
					return false;
				}
	        }
			else
			{
				LangUtil.sendMessage(p, "insufficientpermission");
				return false;
			}
		}
		if (args[0].equalsIgnoreCase("book"))
		{
			if (!sender.hasPermission("eps.admin.book"))
			{
				LangUtil.sendMessage(sender, "insufficientpermission");
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
				LangUtil.sendMessage(sender, "invalidplayer");
				return false;
			}
			if (p.getInventory().firstEmpty() == -1)
			{
				LangUtil.sendMessage(sender, "inventoryfull");
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
					LangUtil.sendMessage(sender, ChatColor.RED+"Invalid enchant "+EPS.getDictionary().getName(enchant).toUpperCase()+"!");
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
				LangUtil.sendMessage(sender, "insufficientpermission");
				return false;
			}
			Player p = Bukkit.getPlayer(args[1]);
			if (p == null)
			{
				LangUtil.sendMessage(sender, "invalidplayer");
				return false;
			}
			if (args.length < 1)
			{
				sender.sendMessage(ChatColor.RED+"Usage: /eps tokenpouch [player] [tokens]");
				return false;
			}
			p.getInventory().addItem(new TokenPouch(Integer.parseInt(args[2])));
		}
		if (args[0].equalsIgnoreCase("give"))
		{
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps give [player] [amount]"));
				return true;
			}
			String[] t = args;
			t[0] = "changetokens";
			onCommand(sender, cmd, label, t);
		}
		if (args[0].equalsIgnoreCase("take"))
		{
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps take [player] [amount]"));
				return true;
			}
			String[] t = args;
			t[0] = "changetokens";
			t[2] = "-"+t[2];
			onCommand(sender, cmd, label, t);
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
