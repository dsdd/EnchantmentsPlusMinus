package org.vivi.eps.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EPSPlayerData;
import org.vivi.eps.items.CustomEnchantedBook;
import org.vivi.eps.items.TokenPouch;
import org.vivi.eps.util.Language;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.sekai.enchantment.EnchantmentInfo;

/**
 * Relax its just a colossal mess :)
 * 
 * @author vivisan
 *
 */
public class EPSCommand implements CommandExecutor, TabCompleter
{

	private List<String> enchantTabList = new ArrayList<String>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			for (String line : EPS.languageFile.getStringList("eps-command-usage"))
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
		} else if (args[0].equalsIgnoreCase("setbal"))
		{
			String perm = "eps.admin.setbal";
			if (!sender.hasPermission(perm))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps setbal [player] [amount]"));
				return true;
			}
			try
			{
				UUID targetUUID = EPSPlayerData.getUUID(args[1]);
				if (targetUUID != null)
				{
					EPS.getEconomy().setBalance(targetUUID, Long.parseLong(args[2]));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&aSet " + args[1] + "'s balance to " + Long.parseLong(args[2])));
				} else
				{
					Language.sendMessage(sender, "invalidplayer");
				}

			} catch (NullPointerException e)
			{
				e.printStackTrace();
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps setbal [player] [amount]"));
			}
			return false;
		} else if (args[0].equalsIgnoreCase("changebal"))
		{
			String perm = "eps.admin.changebal";
			if (!sender.hasPermission(perm))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changebal [player] [amount]"));
				return true;
			}
			try
			{
				UUID targetUUID = EPSPlayerData.getUUID(args[1]);
				if (targetUUID != null)
				{
					EPS.getEconomy().changeBalance(targetUUID, Long.parseLong(args[2]));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&aChanged " + args[1] + "'s balance by " + Long.parseLong(args[2])));
				}
			} catch (NullPointerException e)
			{
				e.printStackTrace();
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changebal [player] [amount]"));
			}
			return false;
		} else if (args[0].equalsIgnoreCase("enchant"))
		{
			String perm = "eps.admin.enchant";

			if (!(sender instanceof Player))
			{
				Language.sendMessage(sender, "invalidplayertype");
				return true;
			}

			Player player = (Player) sender;

			if (player.hasPermission(perm))
			{
				if (args.length < 3)
				{
					sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps enchant [enchant] [lvl]"));
					return true;
				}
				if (player.getInventory().getItemInMainHand().getAmount() > 0)
				{
					int num = Integer.parseInt(args[2]);
					Enchantment enchant = EnchantmentInfo.findEnchantByKey(args[1].toLowerCase());
					if (enchant == null)
					{
						Language.sendMessage(player, "invalid-enchant");
						return true;
					}
					if (num != 0)
						player.getInventory().getItemInMainHand().addUnsafeEnchantment(enchant, num);
					else
						player.getInventory().getItemInMainHand().removeEnchantment(enchant);
					ItemMeta meta = EnchantMetaWriter.getWrittenMeta(player.getInventory().getItemInMainHand());
					player.getInventory().getItemInMainHand().setItemMeta(meta);
					return true;
				} else
				{
					Language.sendMessage(player, "invaliditem");
					return false;
				}
			} else
			{
				Language.sendMessage(player, "insufficientpermission");
				return false;
			}
		} else if (args[0].equalsIgnoreCase("book"))
		{
			if (!sender.hasPermission("eps.admin.book"))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}
			if (args.length == 0)
			{
				sender.sendMessage(ChatColor.RED + "Usage: /eps book [player] [enchant:lvl] [additionalenchs]");
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

			for (int i = 2; i < args.length; i++)
			{
				String[] parts = args[i].split(":");

				if (parts.length == 0)
					continue;

				Enchantment enchant = EnchantmentInfo.findEnchantByKey(parts[0]);
				if (parts.length == 1)
				{
					map.put(enchant, 1);
					continue;
				}

				Integer lvl = Integer.parseInt(parts[1]);
				if (enchant == null)
				{
					sender.sendMessage(ChatColor.RED + "Invalid enchant " + EnchantmentInfo.getKey(enchant) + "!");
					continue;
				}
				map.put(enchant, lvl);
			}

			CustomEnchantedBook book = new CustomEnchantedBook(map);
			book.setItemMeta(EnchantMetaWriter.getWrittenMetaBook(book));
			p.getInventory().addItem(book);
		} else if (args[0].equalsIgnoreCase("tokenpouch"))
		{
			if (!sender.hasPermission("eps.admin.tokenpouch"))
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
				sender.sendMessage(ChatColor.RED + "Usage: /eps tokenpouch [player] [tokens]");
				return false;
			}
			p.getInventory().addItem(new TokenPouch(Integer.parseInt(args[2])));
		} else if (args[0].equalsIgnoreCase("baltop"))
		{
			if (!sender.hasPermission("eps.admin.baltop"))
			{
				Language.sendMessage(sender, "insufficientpermission");
				return false;
			}

			Map<String, Double> balances = new HashMap<String, Double>();

			for (String key : EPS.uuidDataStore.getKeys(false))
			{
				File file = new File(EPS.dataFolder, EPS.uuidDataStore.getString(key) + ".yml");
				if (file.exists())
					balances.put(key, YamlConfiguration.loadConfiguration(file).getDouble("tokens", 0));
			}

			balances = balances.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
					.limit(50).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
							LinkedHashMap::new));

			String baltopPosition = Language.getLangMessage("baltop-position");
			int i = 0;
			for (Map.Entry<String, Double> entry : balances.entrySet())
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						baltopPosition.replaceAll("%position%", Integer.toString(i + 1))
								.replaceAll("%player%", entry.getKey())
								.replaceAll("%tokens%", Double.toString(entry.getValue()))));
				i++;
			}

		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(cmd.getName() == "eps"))
			return null;

		if (enchantTabList.isEmpty())
			for (Enchantment e : EPS.getRegisteredEnchants())
				enchantTabList.add(EnchantmentInfo.getKey(e));

		if (args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("book"))
			return enchantTabList;

		return null;
	}
}
