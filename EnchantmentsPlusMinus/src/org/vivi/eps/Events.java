package org.vivi.eps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS.EnchantMetaWriter;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.ArmorEffect;
import org.vivi.eps.api.EnchantHandler;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.gui.EditEnchantGUI;
import org.vivi.eps.gui.EnchantsGUI;
import org.vivi.eps.items.CustomEnchantedBook;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.economy.Economy;
import org.vivi.epsbuiltin.enchants.Durability;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.inventory.GUIBuilder;
import org.vivi.sekai.inventory.GUIHolder;
import org.vivi.sekai.inventory.ItemBuilder;
import org.vivi.sekai.misc.numbers.NumberAbbreviations;

public class Events implements Listener, Reloadable
{
	private static final Map<Player, Integer> blocklog = new HashMap<Player, Integer>();
	private static final List<EnchantHandler> HANDLERS = new ArrayList<EnchantHandler>();
	private static Economy economy;
	private static String miningTokensGet;
	private static String playerKillGet;
	private static String mobKillGet;

	public static Enchantment enchantToMove = null;

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
			double tokens = Sekai.randomDouble(ConfigSettings.getMobKillRewardMin(),
					ConfigSettings.getMobKillRewardMax());
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
			double tokens = Sekai.randomDouble(ConfigSettings.getPlayerKillRewardMin(),
					ConfigSettings.getPlayerKillRewardMax());
			killer.sendMessage(playerKillGet.replaceAll("%tokens%", EPS.abbreviate(tokens)).replaceAll("%victim%",
					killed.getName()));
			economy.changeBalance(killer, tokens);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
				double tokens = Sekai.randomDouble(ConfigSettings.getMiningRewardMin(),
						ConfigSettings.getMiningRewardMax());
				economy.changeBalance(e.getPlayer(), tokens);
				e.getPlayer().sendMessage(miningTokensGet.replaceAll("%tokens%", EPS.abbreviate(tokens)));
			}
		}

		ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
		if (itemStack != null)
		{
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemMeta != null)
			{
				Collection<ItemStack> drops = e.getBlock().getDrops(itemStack);
				EnchantAction.BlockBreak action = new EnchantAction.BlockBreak(e, drops);
				for (EnchantHandler handler : HANDLERS)
					if (itemMeta.hasEnchant(handler.getEnchant()))
					{
						action.setCurrentEnchant(handler.getEnchant());
						handler.blockBreak(action);
					}
				if (!action.getDrops().equals(drops))
				{
					e.setDropItems(false);
					for (ItemStack drop : action.getDrops())
						e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockDropItem(BlockDropItemEvent e)
	{
		// TODO: 1.15+ Compatibility with new EnchantHandler code system
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

					double amount = NumberAbbreviations.parseAbbreviated(sign.getLine(2));
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
						player.sendMessage(
								Language.getLangMessage("upgraded-item").replaceAll("%enchant%", sign.getLine(1))
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
	public void onPrepareAnvil(PrepareAnvilEvent e)
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
				double amount = NumberAbbreviations.parseAbbreviated(e.getLine(2));

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

		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (e.getPlayer().isSneaking())
				return;
			PlayerInventory inventory = e.getPlayer().getInventory();
			if (ConfigSettings.getEnchantGuiDisableIfHolding().contains(inventory.getItemInMainHand().getType())
					|| (Sekai.getMCVersion() > 11 && e.getClickedBlock() != null
							&& e.getClickedBlock().getType().isInteractable()))
				return;
			if (ConfigSettings.isEnchantGuiOnRightClick())
				Sekai.getCommandProxy().onCommand(e.getPlayer(), Bukkit.getPluginCommand("enchants"), "enchants",
						new String[] { "dontshow" });
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerItemHeld(PlayerItemHeldEvent e)
	{
		EnchantAction.EquipItem action = new EnchantAction.EquipItem(e);
		for (EnchantHandler handler : HANDLERS)
		{
			ItemStack newItemStack = action.getNewItemStack();
			ItemStack prevItemStack = action.getPreviousItemStack();
			if (prevItemStack != null && prevItemStack.getItemMeta() != null
					&& prevItemStack.getItemMeta().hasEnchant(handler.getEnchant()))
			{
				action.setCurrentEnchant(handler.getEnchant());
				handler.unequipItem(action);
			}
			if (newItemStack != null && newItemStack.getItemMeta() != null
					&& newItemStack.getItemMeta().hasEnchant(handler.getEnchant()))
			{
				action.setCurrentEnchant(handler.getEnchant());
				handler.equipItem(action);
			}
		}
		fireArmorEffects(action);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player)
		{
			EnchantAction.EntityDamage action = new EnchantAction.EntityDamage(e);
			ItemStack itemStack = action.getItemStack();
			if (itemStack != null)
			{
				ItemMeta itemMeta = itemStack.getItemMeta();
				if (itemMeta != null)
					for (EnchantHandler handler : HANDLERS)
						if (itemMeta.hasEnchant(handler.getEnchant()))
						{
							action.setCurrentEnchant(handler.getEnchant());
							handler.entityDamage(action);
						}
			}
				
			fireArmorEffects(action);
		}
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

		if (!blocklog.containsKey(e.getPlayer()))
			blocklog.put(e.getPlayer(), 0);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(final InventoryClickEvent e) throws IOException
	{
		ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR)
			return;

		Player player = (Player) e.getWhoClicked();
		EnchantsGUI enchantsGui = EnchantsGUI.get(player);

		if (Sekai.isSameInventory(e.getInventory(), enchantsGui.toInventory()))
		{
			e.setCancelled(true);

			if (clickedItem.getItemMeta().getDisplayName()
					.equals(EnchantsGUI.nextPageItemStack.getItemMeta().getDisplayName()))
			{
				EPS.logger.log(Level.FINE, "Clicked Next Page");
				enchantsGui.nextPage();
				return;
			} else if (clickedItem.getItemMeta().getDisplayName()
					.equals(EnchantsGUI.modifyGuiItemStack.getItemMeta().getDisplayName()))
			{
				EPS.logger.log(Level.FINE, "Clicked Modify GUI");
				EnchantsGUI.setCurrentlyEditing(player);
				return;
			} else if (e.getClickedInventory() instanceof PlayerInventory)
				Language.sendMessage(player, enchantsGui.open(clickedItem, 1) ? "openenchantsgui" : "invaliditem");

			try
			{
				Enchantment enchant = clickedItem.getItemMeta().getEnchants().keySet().iterator().next();

				if (player.equals(EnchantsGUI.getCurrentlyEditing()))
					if (e.isRightClick())
					{
						List<String> enchantKeys = EnchantmentInfo.enchantListToString(enchantsGui.getEnchants());
						for (Map.Entry<String, Object> entry : EPS.guisFile.getConfigurationSection("guis")
								.getValues(false).entrySet())
							if (entry.getValue() instanceof ConfigurationSection && enchantKeys
									.equals(((ConfigurationSection) entry.getValue()).getStringList("enchants")))
							{
								enchantKeys.remove(EnchantmentInfo.getKey(enchant));
								((ConfigurationSection) entry.getValue()).set("enchants", enchantKeys);
							}

						EPS.guisFile.saveYaml();
						EnchantsGUI.setCurrentlyEditing(null);
						EPS.reloadConfigs();
						enchantsGui.refresh();

					} else if (e.isShiftClick())
					{
						e.setCurrentItem(EnchantsGUI.fillerItemStack);
						Events.enchantToMove = enchant;
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								EPS.languageFile.getString("modify-gui.move-notification")));
						return;
					} else if (Events.enchantToMove != null)
					{
						List<String> enchantKeys = EnchantmentInfo.enchantListToString(enchantsGui.getEnchants());
						for (Map.Entry<String, Object> entry : EPS.guisFile.getConfigurationSection("guis")
								.getValues(false).entrySet())
							if (entry.getValue() instanceof ConfigurationSection && enchantKeys
									.equals(((ConfigurationSection) entry.getValue()).getStringList("enchants")))
							{
								Collections.swap(enchantKeys,
										enchantKeys.indexOf(EnchantmentInfo.getKey(Events.enchantToMove)),
										enchantKeys.indexOf(EnchantmentInfo.getKey(enchant)));
								((ConfigurationSection) entry.getValue()).set("enchants", enchantKeys);
							}

						EPS.guisFile.saveYaml();
						EnchantsGUI.setCurrentlyEditing(null);
					} else
					{
						EditEnchantGUI.open(enchant);
						return;
					}
				else
					EPS.purchaseEnchant(player, enchantsGui.getItemStackToEnchant(), enchant,
							e.isLeftClick() ? 1 : (e.isShiftClick() ? 50 : 5));
				enchantsGui.refresh();
			} catch (NoSuchElementException e1)
			{
			}
		} else if (Sekai.isSameInventory(e.getInventory(), EnchantsGUI.itemSelectorInventory))
		{
			e.setCancelled(true);
			Language.sendMessage(player, enchantsGui.open(clickedItem, 1) ? "openenchantsgui" : "invaliditem");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryDrag(final InventoryDragEvent e)
	{
		if (Sekai.getMCVersion() < 13)
		{
			if (!Sekai.isSameInventory(e.getInventory(), EnchantsGUI.get((Player) e.getWhoClicked()).toInventory()))
				return;
			e.setCancelled(true);
		} else
		{
			if (e.getInventory() == EnchantsGUI.get((Player) e.getWhoClicked()).toInventory())
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e)
	{
		if (e.getPlayer().getOpenInventory().getTopInventory().equals(EnchantsGUI.get(e.getPlayer()).toInventory()))
			e.getPlayer().closeInventory();
	}
	
	public static void fireArmorEffects(EnchantAction action)
	{
		EnchantAction.ArmorEffect armorEffect = new EnchantAction.ArmorEffect(action);
		PlayerInventory inventory = action.getPlayer().getInventory();
		fireArmorEffect(armorEffect, inventory.getHelmet());
		fireArmorEffect(armorEffect, inventory.getChestplate());
		fireArmorEffect(armorEffect, inventory.getLeggings());
		fireArmorEffect(armorEffect, inventory.getBoots());
	}

	public static void fireArmorEffect(ArmorEffect armorEffect, ItemStack armorPiece)
	{
		if (armorPiece != null)
		{
			armorEffect.setItemStack(armorPiece);
			for (EnchantHandler handler : HANDLERS)
				if (armorEffect.getItemStack().getItemMeta().hasEnchant(handler.getEnchant()))
				{
					armorEffect.setCurrentEnchant(handler.getEnchant());
					handler.armorEffect(armorEffect);
				}
		}
	}
	
	public static void setDefault(String path, Object replace)
	{
		FileConfiguration fileConfiguration = EPS.configFile.yaml;
		if (!fileConfiguration.isSet(path))
			fileConfiguration.set(path, replace);
	}

	public static void addHandler(EnchantHandler handler)
	{
		HANDLERS.add(handler);
		Collections.sort(HANDLERS);
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
		blocklog.clear();
		economy = EPS.getEconomy();
		miningTokensGet = Language.getLangMessage("miningtokensget");
		playerKillGet = Language.getLangMessage("playerkill");
		mobKillGet = Language.getLangMessage("mobkill");

		EnchantsGUI.fillerItemStack = (Sekai.getMCVersion() < 13
				? new ItemBuilder(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 15)
				: new ItemBuilder(Material.matchMaterial("BLACK_STAINED_GLASS_PANE"), 1)).displayName(" ");
		EnchantsGUI.modifyGuiItemStack = (new ItemBuilder(Material.BOOK, 1)).displayName(
				ChatColor.translateAlternateColorCodes('&', EPS.languageFile.getString("modify-gui.toggle-label")));
		EnchantsGUI.nextPageItemStack = (Sekai.getMCVersion() < 13
				? new ItemBuilder(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 13)
				: new ItemBuilder(Material.matchMaterial("GREEN_STAINED_GLASS_PANE"), 1))
				.displayName(Language.getLangMessage("next-page", false));
		EnchantsGUI.itemSelectorInventory = GUIBuilder
				.build(new GUIHolder(null), 27, Language.getLangMessage("item-selector-title", false))
				.fill(EnchantsGUI.fillerItemStack).setWritable(false).registerEvents(EPS.plugin).toInventory();
	}
}
