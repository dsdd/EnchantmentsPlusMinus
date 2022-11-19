package org.vivi.eps;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS.EnchantMetaWriter;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.items.CustomEnchantedBook;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.economy.Economy;
import org.vivi.eps.visual.EnchantGUI;
import org.vivi.epsbuiltin.enchants.Durability;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;

public class Events implements Listener, Reloadable
{

	public Map<Player, Integer> blocklog;
	private Economy economy;
	private String miningTokensGet;
	private String playerKillGet;
	private String mobKillGet;

	public Events()
	{
		EPS.registerReloadable(this);
	}

	@EventHandler
	public void onKill(EntityDeathEvent e)
	{
		if (!ConfigSettings.isMobKillRewardEnabled())
			return;
		if (e.getEntity() instanceof Player)
			return;
		if (e.getEntity().getKiller() == null)
			return;
		Player killer = e.getEntity().getKiller();
		if (killer instanceof Player)
		{
			double tokens = Sekai.randomDouble(ConfigSettings.getMobKillRewardMin(), ConfigSettings.getMobKillRewardMax());
			String name = e.getEntityType().name();
			killer.sendMessage(mobKillGet.replaceAll("%tokens%", EPS.abbreviate(tokens)).replaceAll("%mob%",
					WordUtils.capitalizeFully(name.replaceAll("_", " ").toLowerCase())));
			economy.changeBalance(killer, tokens);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		if (!ConfigSettings.isPlayerKillRewardEnabled())
			return;
		if (e.getEntity().getKiller() == null)
			return;
		Player killer = e.getEntity().getKiller();
		if (killer instanceof Player)
		{
			Player killed = (Player) e.getEntity();
			if (killer == killed)
				return;
			double tokens = Sekai.randomDouble(ConfigSettings.getPlayerKillRewardMin(), ConfigSettings.getPlayerKillRewardMax());
			killer.sendMessage(playerKillGet.replaceAll("%tokens%", EPS.abbreviate(tokens)).replaceAll("%victim%",
					killed.getName()));
			economy.changeBalance(killer, tokens);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e)
	{
		if (!ConfigSettings.isMiningRewardEnabled())
			return;
		if (blocklog.containsKey(e.getPlayer()))
		{
			blocklog.put(e.getPlayer(), blocklog.get(e.getPlayer()) + 1);
			if (blocklog.get(e.getPlayer()) >= ConfigSettings.getMiningRewardBlocksToBreak())
			{
				blocklog.put(e.getPlayer(), 0);
				double tokens = Sekai.randomDouble(ConfigSettings.getMiningRewardMin(), ConfigSettings.getMiningRewardMax());
				economy.changeBalance(e.getPlayer(), tokens);
				e.getPlayer().sendMessage(miningTokensGet.replaceAll("%tokens%", EPS.abbreviate(tokens)));
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		ItemStack itemStack = player.getInventory().getItemInMainHand();

		if (e.getAction() == Action.RIGHT_CLICK_AIR)
		{
			if (itemStack.getItemMeta() != null)
				if (itemStack.getItemMeta().hasLore())
				{
					String str = itemStack.getItemMeta().getLore().get(0);
					if (str.startsWith(ChatColor.BLACK + "T:"))
					{
						int tokens = Integer.parseInt(str.split(":")[1]);
						EPS.getEconomy().changeBalance(player, tokens);
						itemStack.setAmount(itemStack.getAmount() - 1);
						player.sendMessage(Language.getLangMessage("claimed-token-pouch").replaceAll("%tokens%",
								Integer.toString(tokens)));
					}
				}
		} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Block clickedBlock = e.getClickedBlock();

			if (itemStack != null && itemStack.getItemMeta() != null && clickedBlock.getState() instanceof Sign)
			{
				Sign sign = (Sign) clickedBlock.getState();
				if (sign.getLine(0).equals(Language.getLangMessage("enchant-sign-success", false)))
				{
					Enchantment enchant = EnchantmentInfo.getEnchantByName(sign.getLine(1));
					if (enchant == null)
					{
						EPS.logger.log(Level.WARNING,
								"Invalid enchant in enchant sign at " + clickedBlock.getLocation().toString());
						return;
					}

					double amount = Sekai.parseAbbreviated(sign.getLine(2));
					if (EPS.getEconomy().getBalance(player) < amount)
					{
						Language.sendMessage(player, "insufficienttokens");
						return;
					}

					int currentEnchantLevel = itemStack.getItemMeta().getEnchantLevel(enchant);
					if (currentEnchantLevel + 1 > EPS.getEnchantFile(enchant).getMaxLevel())
					{
						Language.sendMessage(player, "maxedupgrade");
						return;
					}

					if (enchant.canEnchantItem(itemStack))
					{
						EPS.getEconomy().changeBalance(player, -amount);
						itemStack.addUnsafeEnchantment(enchant, currentEnchantLevel + 1);
						itemStack.setItemMeta(EnchantMetaWriter.getWrittenMeta(itemStack));
						player.sendMessage(Language.getLangMessage("upgraded-item")
								.replaceAll("%enchant%", sign.getLine(1))
								.replaceAll("%lvl%", Integer.toString(currentEnchantLevel + 1)));
					}
				}
			}
		}

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();

		if (item.getItemMeta() != null)
			if (item.getItemMeta().hasLore())
			{
				String str = item.getItemMeta().getLore().get(0);
				if (str.startsWith(ChatColor.BLACK + "T:"))
				{
					e.setCancelled(true);
				}
			}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(PrepareAnvilEvent e)
	{
		if (!ConfigSettings.isAnvilCombiningEnabled() || Sekai.getMCVersion() < 12)
			return;
		AnvilInventory anvil = e.getInventory();

		if (anvil.getItem(0) == null)
			return;

		ItemStack slot1 = anvil.getItem(0);
		ItemStack slot2 = anvil.getItem(1);
		ItemStack item = new ItemStack(slot1.getType(), slot1.getAmount());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(anvil.getRenameText());
		meta.setLore(slot1.getItemMeta().getLore());
		item.setItemMeta(meta);
		Durability dura1 = new Durability(item);
		Durability dura2 = new Durability(slot1);
		dura1.setDurability(dura2.getDurability());

		Map<Enchantment, Integer> enchantments = slot2 == null ? CustomEnchantedBook.getEnchants(slot1)
				: CustomEnchantedBook.combineEnchants(slot1, slot2, false);

		int cost = 1;
		for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
		{
			int maxLevel = EPS.getEnchantFile(entry.getKey()).getMaxLevel();
			if (maxLevel != 0 && entry.getValue() > maxLevel)
			{
				item.addUnsafeEnchantment(entry.getKey(), maxLevel);
				cost = cost + maxLevel;
			} else
			{
				item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
				cost = cost + entry.getValue();
			}
		}
		final int cost1 = cost;
		ItemMeta lore = EnchantMetaWriter.getWrittenMeta(item);
		item.setItemMeta(lore);
		e.setResult(item);
		Bukkit.getServer().getScheduler().runTask(EPS.plugin, () -> e.getInventory().setRepairCost(cost1));
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignChangeEvent(SignChangeEvent e)
	{
		if (e.getLine(0).equalsIgnoreCase(Language.getLangMessage("enchant-sign-initiating-line", false)))
		{
			try
			{
				Enchantment enchant = EnchantmentInfo.getEnchantByKey(e.getLine(1));
				if (enchant == null)
				{
					enchant = EnchantmentInfo.getEnchantByName(e.getLine(1));
					if (enchant == null)
						throw new Exception();
				}

				String enchantName = EnchantmentInfo.getName(enchant);
				double amount = Sekai.parseAbbreviated(e.getLine(2));

				e.setLine(1, enchantName);
				e.setLine(2, EPS.abbreviate(amount));
				EPS.logger.log(Level.INFO,
						"Created enchant sign, selling " + enchantName + " for " + Double.toString(amount));
				EPS.logger.log(Level.FINE, e.getBlock().getLocation().toString());
			} catch (NumberFormatException e1)
			{
				EPS.logger.log(Level.WARNING, "Attempted creating enchant sign with invalid cost");
				e.setLine(0, Language.getLangMessage("enchant-sign-failure", false));
				return;
			} catch (Exception e1)
			{
				EPS.logger.log(Level.WARNING, "Attempted creating enchant sign with invalid enchant");
				e.setLine(0, Language.getLangMessage("enchant-sign-failure", false));
				return;
			}

			e.setLine(0, Language.getLangMessage("enchant-sign-success", false));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		EnchantMetaWriter.refreshItem(e.getPlayer().getInventory().getItemInMainHand());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) throws IOException
	{
		Player player = e.getPlayer();
		File dataFile = new File(EPS.dataFolder, player.getUniqueId().toString() + ".yml");
		EPS.uuidDataStore.set(player.getName(), player.getUniqueId().toString());
		EPS.uuidDataStore.saveYaml();

		if (!dataFile.exists())
			dataFile.createNewFile();

		FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
		dataConfig.set("tokens", dataConfig.get("tokens", 0));
		dataConfig.save(dataFile);
		EnchantGUI.setupGUI(e.getPlayer());

		if (!blocklog.containsKey(e.getPlayer()))
			blocklog.put(e.getPlayer(), 0);
	}

	public static void setDefault(String path, Object replace)
	{
		FileConfiguration fileConfiguration = EPS.configFile.yaml;
		if (!fileConfiguration.isSet(path))
			fileConfiguration.set(path, replace);
	}

	@Override
	public void reload()
	{
		setDefault("playerkilltokens.enabled", true);
		setDefault("playerkilltokens.min", 25);
		setDefault("playerkilltokens.max", 50);
		setDefault("mobkilltokens.enabled", true);
		setDefault("mobkilltokens.min", 5);
		setDefault("mobkilltokens.max", 10);
		Language.setDefaultLangMessage("playerkill", "&aYou received %tokens% tokens for killing %victim%!");
		Language.setDefaultLangMessage("mobkill", "&aYou received %tokens% tokens for killing %mob%!");

		setDefault("miningtokens.enabled", true);
		setDefault("miningtokens.min", 25);
		setDefault("miningtokens.max", 50);
		setDefault("miningtokens.blockstobreak", 1000);
		Language.setDefaultLangMessage("miningtokensget", "&aYou received %tokens% tokens for mining!");
		blocklog = new HashMap<Player, Integer>();
		economy = EPS.getEconomy();
		miningTokensGet = Language.getLangMessage("miningtokensget");
		playerKillGet = Language.getLangMessage("playerkill");
		mobKillGet = Language.getLangMessage("mobkill");
	}
}
