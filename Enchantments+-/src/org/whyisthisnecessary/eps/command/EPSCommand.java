package org.whyisthisnecessary.eps.command;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.PlugmanUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;
import org.whyisthisnecessary.eps.visual.EnchantMetaWriter;

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
			File[] files = Main.PackFolder.listFiles();
    	    if (files != null)
    	    {
    	    	for (File file : files)
    	    	{
					try {
						Plugin plugin1 = PlugmanUtil.getPluginFromFile(file);
						Bukkit.getPluginManager().disablePlugin(plugin1);
						PlugmanUtil.unload(plugin1);
						Plugin plugin2 = Bukkit.getPluginManager().loadPlugin(file);
						Bukkit.getPluginManager().enablePlugin(plugin2);
					} catch (Exception e) {
					}
    	    	}

    	    }
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
					p.getInventory().getItemInMainHand().addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(args[1])), Integer.parseInt(args[2]));
					ItemMeta meta = EnchantMetaWriter.getWrittenEnchantLore(p.getInventory().getItemInMainHand());
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
		if (args[0].equalsIgnoreCase("loadpack"))
		{
			if (sender.hasPermission("eps.admin.loadpack"))
			{
			try {
			    Plugin pl1 = Bukkit.getPluginManager().loadPlugin(new File(Main.PackFolder, args[1]+".jar"));
			    Bukkit.getPluginManager().enablePlugin(pl1);
			    Main.EnabledPacks.add(pl1);
			    sender.sendMessage(ChatColor.GREEN + "Loaded pack!");
			    }
			    catch (Exception e)
			    {
			    	e.printStackTrace();
			    }
			}
			else
			{
				sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
				return false;
			}
		}
		if (args[0].equalsIgnoreCase("reloadpack"))
		{
			if (sender.hasPermission("eps.admin.reloadpack"))
			{
			try {
		    Plugin pl = Bukkit.getPluginManager().getPlugin(args[1]);	
		    Bukkit.getPluginManager().disablePlugin(pl);
		    PlugmanUtil.unload(pl);
		    Plugin pl1 = Bukkit.getPluginManager().loadPlugin(Main.getJarFile(pl));
		    Bukkit.getPluginManager().enablePlugin(pl1);
		    sender.sendMessage(ChatColor.GREEN + "Reloaded pack!");
		    }
		    catch (Exception e)
		    {
		    	e.printStackTrace();
		    }
		    }
			else
			{
				sender.sendMessage(LangUtil.getLangMessage("insufficientpermission"));
				return false;
			}
		}
		
		return false;
	}
}
