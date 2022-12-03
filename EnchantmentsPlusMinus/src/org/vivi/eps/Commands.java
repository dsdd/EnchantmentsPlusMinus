package org.vivi.eps;

import java.io.File;
//import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
//import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
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
import org.bukkit.inventory.PlayerInventory;
import org.vivi.eps.EPS.EnchantMetaWriter;
import org.vivi.eps.api.EPSPlayerData;
import org.vivi.eps.gui.EnchantsGUI;
import org.vivi.eps.items.CustomEnchantedBook;
import org.vivi.eps.items.TokenPouch;
import org.vivi.eps.util.Language;
import org.vivi.sekai.CommandProxy.CommandOptions;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.misc.numbers.NumberAbbreviations;
import org.vivi.sekai.CommandProxy.CommandConnection;

public class Commands
{
	//private static ConsoleHandler consoleHandler = new ConsoleHandler();;
	
	public static String playerOnlyMessage = Language.getLangMessage("invalidplayertype");
	public static String insufficientPermissionsMessage = Language.getLangMessage("insufficientpermission");

	public static final PluginCommand EPS_COMMAND = Bukkit.getPluginCommand("eps");
	public static final PluginCommand ENCHANTS_COMMAND = Bukkit.getPluginCommand("enchants");
	public static final PluginCommand PAYTOKENS_COMMAND = Bukkit.getPluginCommand("paytokens");
	public static final PluginCommand SCRAP_COMMAND = Bukkit.getPluginCommand("scrap");
	public static final PluginCommand TOKENS_COMMAND = Bukkit.getPluginCommand("tokens");

	/**
	 * I am sorry
	 */
	public static final CommandOptions EPS_COMMAND_OPTIONS = new CommandOptions(false, false, 1, 1, null, null,
			"eps.admin", Sekai.convertListToString(EPS.languageFile.getStringList("eps-command-usage")),
			insufficientPermissionsMessage)
			.argument("reload",
					new CommandOptions(false, false, 0, 0, null, null, "eps.admin.reload", null,
							insufficientPermissionsMessage))
			.argument("debug",
					new CommandOptions(false, false, 0, 0, null, null, "eps.admin.reload", null,
							insufficientPermissionsMessage))
			.argument("setbal",
					new CommandOptions(false, false, 2, 2, null, null, "eps.admin.setbal",
							ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps setbal [player] [amount]"),
							insufficientPermissionsMessage))
			.argument("changebal",
					new CommandOptions(false, false, 2, 2, null, null, "eps.admin.changebal",
							ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changebal [player] [amount]"),
							insufficientPermissionsMessage))
			.argument("enchant",
					new CommandOptions(true, false, 2, 2, playerOnlyMessage, null, "eps.admin.enchant",
							ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps enchant [enchant] [lvl]"),
							insufficientPermissionsMessage))
			.argument("book",
					new CommandOptions(false, false, 2, 2, null, null, "eps.admin.book",
							ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps book [player] [enchant:lvl].."),
							insufficientPermissionsMessage))
			.argument("tokenpouch",
					new CommandOptions(false, false, 2, 2, null, null, "eps.admin.tokenpouch",
							ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps tokenpouch [player] [tokens]"),
							insufficientPermissionsMessage))
			.argument("baltop", new CommandOptions(false, false, 0, 0, null, null, "eps.admin.baltop", null,
					insufficientPermissionsMessage));

	public static final CommandOptions ENCHANTS_COMMAND_OPTIONS = new CommandOptions(true, false, 0, 0,
			playerOnlyMessage, null, "eps.enchants", null, insufficientPermissionsMessage);

	public static final CommandOptions PAYTOKENS_COMMAND_OPTIONS = new CommandOptions(true, false, 2, 0,
			playerOnlyMessage, null, "eps.paytokens",
			ChatColor.translateAlternateColorCodes('&', "&cUsage: /paytokens [player] [amount]"),
			insufficientPermissionsMessage);

	public static final CommandOptions SCRAP_COMMAND_OPTIONS = new CommandOptions(true, false, 0, 0, playerOnlyMessage,
			null, "eps.scrap", null, insufficientPermissionsMessage);

	public static final CommandOptions TOKENS_COMMAND_OPTIONS = new CommandOptions(false, false, 0, 1, null, null,
			"eps.tokens", ChatColor.translateAlternateColorCodes('&', "&cUsage: /tokens [player]"),
			insufficientPermissionsMessage);

	/**
	 * I am sorry again
	 */
	public static final CommandConnection EPS_COMMAND_CONNECTION = new CommandConnection() {

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
		{
			if (args[0].equalsIgnoreCase("reload"))
			{
				EPS.reloadConfigs();
				Language.sendMessage(sender, "reloadconfig");
			} else if (args[0].equalsIgnoreCase("debug"))
			{
				if (EPS.logger.getLevel() == Level.INFO)
				{
					//if (!Arrays.asList(EPS.logger.getHandlers()).contains(consoleHandler))
					//	EPS.logger.addHandler(consoleHandler);
					
					EPS.logger.setLevel(Level.FINER);
					//consoleHandler.setLevel(Level.FINER);
					EPS.logger.log(Level.INFO, "Enabled debug mode");
				} else
				{
					//EPS.logger.removeHandler(consoleHandler);
					
					EPS.logger.setLevel(Level.INFO);
					//consoleHandler.setLevel(Level.OFF);
					EPS.logger.log(Level.INFO, "Disabled debug mode");
				}
				
			}
			else if (args[0].equalsIgnoreCase("setbal"))
			{

				if (!setBalance(args[1], args[2]))
				{
					sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps setbal [player] [amount]"));
					return false;
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&aSet " + args[1] + "'s balance to " + NumberAbbreviations.parseAbbreviated(args[2])));
			} else if (args[0].equalsIgnoreCase("changebal"))
			{
				if (!changeBalance(args[1], args[2]))
				{
					sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changebal [player] [amount]"));
					return false;
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						"&cChanged " + args[1] + "'s balance by " + NumberAbbreviations.parseAbbreviated(args[2])));
			} else if (args[0].equalsIgnoreCase("enchant"))
			{
				Player player = (Player) sender;

				ItemStack itemStack = player.getInventory().getItemInMainHand();
				if (itemStack.getAmount() > 0)
				{
					int level = Integer.parseInt(args[2]);
					Enchantment enchant = EnchantmentInfo.getEnchantByKey(args[1].toLowerCase());
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

					Enchantment enchant = EnchantmentInfo.getEnchantByKey(parts[0]);
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
									.replaceAll("%tokens%", EPS.abbreviate(entry.getValue()))));
					i++;
				}
			}

			return true;
		}
	};

	public static final CommandConnection ENCHANTS_COMMAND_CONNECTION = new CommandConnection() {

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
		{

			Player player = (Player) sender;

			boolean success = EnchantsGUI.get(player).open(player.getInventory().getItemInMainHand(), 1);

			if (args.length < 1 || !args[0].equalsIgnoreCase("dontshow"))
				if (success)
					Language.sendMessage(sender, "openenchantsgui");
				else
				{
					EPS.logger.log(Level.FINE, "Opened item selector GUI");
					player.openInventory(EnchantsGUI.itemSelectorInventory);
				}

			return true;
		}
	};

	public static final CommandConnection PAYTOKENS_COMMAND_CONNECTION = new CommandConnection() {

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
		{
			UUID targetUUID = EPSPlayerData.getUUID(args[0]);
			if (targetUUID != null)
			{
				try
				{
					int tokens = Integer.parseInt(args[1]);

					if (EPS.getEconomy().getBalance((Player) sender) < tokens)
					{
						Language.sendMessage(sender, "invalidtokenamountpay");
						return true;
					}
					EPS.getEconomy().changeBalance((Player) sender, -tokens);
					EPS.getEconomy().changeBalance(targetUUID, tokens);
					return true;
				} catch (NumberFormatException e)
				{
					Language.sendMessage(sender, "invalidtokenamountpay");
					return true;
				}
			} else
			{
				Language.sendMessage(sender, "invalidplayer");
				return false;
			}
		}
	};

	public static final CommandConnection SCRAP_COMMAND_CONNECTION = new CommandConnection() {

		@SuppressWarnings("deprecation")
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
		{
			Player player = (Player) sender;
			PlayerInventory playerInventory = player.getInventory();

			ItemStack item = Sekai.getMCVersion() < 9 ? playerInventory.getItemInHand()
					: playerInventory.getItemInMainHand();
			Map<Enchantment, Integer> map = item.getItemMeta().getEnchants();
			int scrapvalue = 0;

			for (Map.Entry<Enchantment, Integer> entry : map.entrySet())
			{
				scrapvalue += EPS.getEnchantFile(entry.getKey()).getScrapValue() * entry.getValue();
			}

			if (scrapvalue > 0)
			{
				EPS.getEconomy().changeBalance(player, scrapvalue);
				playerInventory.removeItem(Sekai.getMCVersion() < 9 ? playerInventory.getItemInHand()
						: playerInventory.getItemInMainHand());
				player.sendMessage(
						Language.getLangMessage("scrapsuccess").replaceAll("%tokens%", Integer.toString(scrapvalue)));
			} else
			{
				player.sendMessage(Language.getLangMessage("cannotscrap"));
			}

			return true;
		}
	};

	public static final CommandConnection TOKENS_COMMAND_CONNECTION = new CommandConnection() {

		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
		{
			String targetName = args.length == 0 ? sender.getName() : args[0];
			UUID targetUUID = EPSPlayerData.getUUID(targetName);
			if (targetUUID == null)
			{
				Language.sendMessage(sender, "invalidplayer");
				return false;
			}
			sender.sendMessage(Language.getLangMessage("tokenbalance")
					.replaceAll("%tokens%", EPS.abbreviate(EPS.getEconomy().getBalance(targetUUID)))
					.replaceAll("%player%", targetName));
			return true;
		}
	};

	public static void registerCommands()
	{
		Sekai.registerCommand(EPS_COMMAND, EPS_COMMAND_OPTIONS);
		Sekai.registerCommand(ENCHANTS_COMMAND, ENCHANTS_COMMAND_OPTIONS);
		Sekai.registerCommand(PAYTOKENS_COMMAND, PAYTOKENS_COMMAND_OPTIONS);
		Sekai.registerCommand(SCRAP_COMMAND, SCRAP_COMMAND_OPTIONS);
		Sekai.registerCommand(TOKENS_COMMAND, TOKENS_COMMAND_OPTIONS);

		Sekai.connectCommand(EPS_COMMAND, EPS_COMMAND_CONNECTION);
		Sekai.connectCommand(ENCHANTS_COMMAND, ENCHANTS_COMMAND_CONNECTION);
		Sekai.connectCommand(PAYTOKENS_COMMAND, PAYTOKENS_COMMAND_CONNECTION);
		Sekai.connectCommand(SCRAP_COMMAND, SCRAP_COMMAND_CONNECTION);
		Sekai.connectCommand(TOKENS_COMMAND, TOKENS_COMMAND_CONNECTION);
	}

	private static boolean setBalance(String playerName, String amountLabel)
	{
		UUID targetUUID = EPSPlayerData.getUUID(playerName);
		if (targetUUID != null)
		{
			EPS.getEconomy().setBalance(targetUUID, NumberAbbreviations.parseAbbreviated(amountLabel));
			return true;

		} else
			return false;
	}

	private static boolean changeBalance(String playerName, String amountLabel)
	{
		UUID targetUUID = EPSPlayerData.getUUID(playerName);
		if (targetUUID != null)
		{
			EPS.getEconomy().changeBalance(targetUUID, NumberAbbreviations.parseAbbreviated(amountLabel));
			return true;

		} else
			return false;
	}
}
