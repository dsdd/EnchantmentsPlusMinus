package org.whyisthisnecessary.eps.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.item.TokenPouch;
import org.whyisthisnecessary.eps.legacy.NameUtil;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;
import org.whyisthisnecessary.eps.visual.EnchantMetaWriter;
import org.whyisthisnecessary.eps.workbench.CustomEnchantedBook;

public class EPSCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "Usage:");
			sender.sendMessage(ChatColor.RED + "/eps reload");
			sender.sendMessage(ChatColor.RED + "/eps settokens [plr] [amount]");
			sender.sendMessage(ChatColor.RED + "/eps changetokens [plr] [amount]");
			sender.sendMessage(ChatColor.RED + "/eps enchant [enchant] [lvl]");
			sender.sendMessage(ChatColor.RED + "/eps reloadpack [packname]");
			sender.sendMessage(ChatColor.RED + "/eps book [enchant:lvl]");
			sender.sendMessage(ChatColor.RED + "/eps tokenpouch [tokens]");
			return false;
		}
		
		if (args[0].equalsIgnoreCase("reload"))
		{
			String perm = "eps.admin.reload";
			if (!sender.hasPermission(perm))
			{
				sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
				return false;
			}
			Main.plugin.reloadConfig();
			Main.Config = Main.plugin.getConfig();
			Main.UUIDDataStoreConfig = YamlConfiguration.loadConfiguration(Main.UUIDDataStore);
			Main.LangConfig = YamlConfiguration.loadConfiguration(Main.LangFile);
			Main.InCPTConfig = YamlConfiguration.loadConfiguration(Main.InCPTFile);
			Main.GuisConfig = YamlConfiguration.loadConfiguration(Main.GuisFile);
			ConfigUtil.reloadConfigs();
			sender.sendMessage(LangUtil.getLangMessage("reloadconfig"));
			return false;
		}
		
		if (args[0].equalsIgnoreCase("settokens"))
		{
			String perm = "eps.admin.settokens";
			if (!sender.hasPermission(perm))
			{
				sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps settokens [player] [amount]"));
				return true;
			}
			try
			{
			TokenUtil.setTokens(args[1],Integer.parseInt(args[2]));
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
				sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changetokens [player] [amount]"));
				return true;
			}
			try
			{
			TokenUtil.changeTokens(args[1],Integer.parseInt(args[2]));
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
				sender.sendMessage(LangUtil.getLangMessage("invalidplayertype"));
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
					Integer num = Integer.parseInt(args[2]);
					Enchantment enchant = NameUtil.getByName(args[1]);
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
					p.sendMessage(LangUtil.getLangMessage("invaliditem"));
					return false;
				}
	        }
			else
			{
				p.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
				return false;
			}
		}
		if (args[0].equalsIgnoreCase("book"))
		{
			if (!sender.hasPermission("eps.admin.book"))
			{
				sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
				return false;
			}
			if (!(sender instanceof Player))
			{
				sender.sendMessage(LangUtil.getLangMessage("invalidplayertype"));
				return false;
			}
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.RED+"Usage: /eps book [enchant:lvl] [additionalenchs]");
				return false;
			}
			
			Player p = (Player)sender;
			if (p.getInventory().firstEmpty() == -1)
			{
				sender.sendMessage(LangUtil.getLangMessage("inventoryfull"));
				return false;
			}
			
			Map<Enchantment, Integer> map = new HashMap<Enchantment, Integer>();
			
			for (int i=1;i<args.length;i++)
			{
				String[] parts = args[i].split(":");
				
				if (parts.length == 0)
					continue;
				
				Enchantment enchant = NameUtil.getByName(parts[0]);
				if (parts.length == 1)
				{
					map.put(enchant, 1);
					continue;
				}
				
				
				Integer lvl = Integer.parseInt(parts[1]);
				if (enchant == null)
				{
					sender.sendMessage(LangUtil.getLangMessage(ChatColor.RED+"Invalid enchant "+NameUtil.getName(enchant).toUpperCase()+"!"));
					sender.sendMessage(LangUtil.getLangMessage(ChatColor.RED+"Invalid enchant level "+parts[1]+"!"));
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
				sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
				return false;
			}
			if (!(sender instanceof Player))
			{
				sender.sendMessage(LangUtil.getLangMessage("invalidplayertype"));
				return false;
			}
			Player p = (Player)sender;
			if (args.length < 1)
			{
				sender.sendMessage(ChatColor.RED+"Usage: /eps tokenpouch [tokens]");
				return false;
			}
			p.getInventory().addItem(new TokenPouch(Integer.parseInt(args[1])));
		}
		
		return false;
	}
}
