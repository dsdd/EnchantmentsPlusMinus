package org.vivi.eps;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.vivi.eps.api.EPSPlayerData;
import org.vivi.eps.items.CustomEnchantedBook;
import org.vivi.eps.items.TokenPouch;
import org.vivi.eps.util.Language;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.sekai.CommandProxy.CommandOptions;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.CommandProxy.CommandConnection;

public class Commands
{
	public static String playerOnlyMessage = Language.getLangMessage("invalidplayertype");
	public static String insufficientPermissionsMessage = Language.getLangMessage("insufficientpermission");

	public static void registerEPSCommand()
	{
		StringBuilder epsUsageStringBuilder = new StringBuilder();
		for (String line : EPS.languageFile.getStringList("eps-command-usage"))
			epsUsageStringBuilder.append(ChatColor.translateAlternateColorCodes('&', line)).append("\n");

		CommandOptions epsCommandOptions = new CommandOptions(false, false, 1, 1, "eps.admin", null, null,
				epsUsageStringBuilder.toString(), insufficientPermissionsMessage);

		epsCommandOptions.argument("reload", new CommandOptions(false, false, 0, 0, "eps.admin.reload", null, null,
				null, insufficientPermissionsMessage));

		// The usage messages will be transferred to lang.yml, using &c instead of
		// ChatColor.RED

		epsCommandOptions.argument("setbal",
				new CommandOptions(false, false, 2, 2, "eps.admin.setbal", null, null,
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps setbal [player] [amount]"),
						insufficientPermissionsMessage));

		epsCommandOptions.argument("changebal",
				new CommandOptions(false, false, 2, 2, "eps.admin.changebal", null, null,
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changebal [player] [amount]"),
						insufficientPermissionsMessage));

		epsCommandOptions.argument("enchant",
				new CommandOptions(true, false, 2, 2, "eps.admin.enchant", playerOnlyMessage, null,
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps enchant [enchant] [lvl]"),
						insufficientPermissionsMessage));

		epsCommandOptions.argument("book",
				new CommandOptions(false, false, 2, 2, "eps.admin.book", null, null,
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps book [player] [enchant:lvl].."),
						insufficientPermissionsMessage));

		epsCommandOptions.argument("tokenpouch",
				new CommandOptions(false, false, 2, 2, "eps.admin.tokenpouch", null, null,
						ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps tokenpouch [player] [tokens]"),
						insufficientPermissionsMessage));

		epsCommandOptions.argument("baltop", new CommandOptions(false, false, 0, 0, "eps.admin.baltop", null, null,
				null, insufficientPermissionsMessage));

		CommandConnection epsCommandConnection = new CommandConnection() {

			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
			{
				if (args[0].equalsIgnoreCase("reload"))
				{
					EPS.reloadConfigs();
					Language.sendMessage(sender, "reloadconfig");
				} else if (args[0].equalsIgnoreCase("setbal"))
				{

					if (!setBalance(args[1], args[2]))
					{
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps setbal [player] [amount]"));
						return false;
					}
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&aSet " + args[1] + "'s balance to " + Sekai.parseAbbreviated(args[2])));
				} else if (args[0].equalsIgnoreCase("changebal"))
				{
					if (!changeBalance(args[1], args[2]))
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&cUsage: /eps changebal [player] [amount]"));
						return false;
					}
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&cChanged " + args[1] + "'s balance by " + Sekai.parseAbbreviated(args[2])));
				} else if (args[0].equalsIgnoreCase("enchant"))
				{
					Player player = (Player) sender;

					ItemStack itemStack = player.getInventory().getItemInMainHand();
					if (itemStack.getAmount() > 0)
					{
						int level = Integer.parseInt(args[2]);
						Enchantment enchant = EnchantmentInfo.findEnchantByKey(args[1].toLowerCase());
						if (enchant == null)
						{
							Language.sendMessage(player, "invalid-enchant");
							return true;
						}
						if (level != 0)
							itemStack.addUnsafeEnchantment(enchant, level);
						else
							itemStack.removeEnchantment(enchant);
						itemStack.setItemMeta(EnchantMetaWriter.getWrittenMeta(itemStack));
						return true;
					} else
					{
						Language.sendMessage(player, "invaliditem");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("book"))
				{
					Player player = Bukkit.getPlayer(args[1]);
					if (player == null)
					{
						Language.sendMessage(sender, "invalidplayer");
						return false;
					}
					if (player.getInventory().firstEmpty() == -1)
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
							sender.sendMessage(
									ChatColor.RED + "Invalid enchant " + EnchantmentInfo.getKey(enchant) + "!");
							continue;
						}
						map.put(enchant, lvl);
					}

					CustomEnchantedBook book = new CustomEnchantedBook(map);
					book.setItemMeta(EnchantMetaWriter.getWrittenMetaBook(book));
					player.getInventory().addItem(book);
				} else if (args[0].equalsIgnoreCase("tokenpouch"))
				{
					Player player = Bukkit.getPlayer(args[1]);
					if (player == null)
					{
						Language.sendMessage(sender, "invalidplayer");
						return false;
					}
					player.getInventory().addItem(new TokenPouch(Integer.parseInt(args[2])));
				} else if (args[0].equalsIgnoreCase("baltop"))
				{
					Map<String, Double> balances = new HashMap<String, Double>();

					for (String key : EPS.uuidDataStore.getKeys(false))
					{
						File file = new File(EPS.dataFolder, EPS.uuidDataStore.getString(key) + ".yml");
						if (file.exists())
							balances.put(key, YamlConfiguration.loadConfiguration(file).getDouble("tokens", 0));
					}

					balances = balances.entrySet().stream()
							.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(50).collect(Collectors
									.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

					String baltopPosition = Language.getLangMessage("baltop-position");
					int i = 0;
					for (Map.Entry<String, Double> entry : balances.entrySet())
					{
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								baltopPosition.replaceAll("%position%", Integer.toString(i + 1))
										.replaceAll("%player%", entry.getKey())
										.replaceAll("%tokens%", Sekai.abbreviate(entry.getValue()))));
						i++;
					}
				}

				return true;
			}
		};
		
		PluginCommand epsPluginCommand = Bukkit.getPluginCommand("eps");
		Sekai.registerCommand(epsPluginCommand, epsCommandOptions);
		Sekai.connectCommand(epsPluginCommand, epsCommandConnection);
	}

	public static boolean setBalance(String playerName, String amountLabel)
	{
		UUID targetUUID = EPSPlayerData.getUUID(playerName);
		if (targetUUID != null)
		{
			EPS.getEconomy().setBalance(targetUUID, Sekai.parseAbbreviated(amountLabel));
			return true;

		} else
			return false;
	}

	public static boolean changeBalance(String playerName, String amountLabel)
	{
		UUID targetUUID = EPSPlayerData.getUUID(playerName);
		if (targetUUID != null)
		{
			EPS.getEconomy().changeBalance(targetUUID, Sekai.parseAbbreviated(amountLabel));
			return true;

		} else
			return false;
	}
}
