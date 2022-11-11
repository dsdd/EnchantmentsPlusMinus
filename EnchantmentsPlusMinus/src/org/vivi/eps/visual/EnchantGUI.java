package org.vivi.eps.visual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EnchantFile;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.economy.Economy;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.yaml.YamlFile;

@SuppressWarnings("deprecation")
public class EnchantGUI implements Listener, Reloadable
{

	public static Map<Player, Inventory> GUIs = new HashMap<Player, Inventory>();
	public static Map<Player, String> guiNames = new HashMap<Player, String>();
	public static Map<List<String>, List<Enchantment>> incpts = new HashMap<List<String>, List<Enchantment>>();
	private static List<Material> hoes = new ArrayList<Material>(Arrays.asList(new Material[] {
			Material.matchMaterial("WOODEN_HOE"), Material.matchMaterial("WOOD_HOE"), Material.STONE_HOE,
			Material.IRON_HOE, Material.matchMaterial("GOLDEN_HOE"), Material.matchMaterial("GOLD_HOE"),
			Material.DIAMOND_HOE, Material.matchMaterial("NETHERITE_HOE") }));
	private static final Economy economy = EPS.getEconomy();
	private static List<Player> disabled = new ArrayList<Player>();
	private static Player modifying = null;
	private static String nextPageName = Language.getLangMessage("next-page", false);
	private static String modifyGuiName = Language.getLangMessage("modify-gui", false);
	private static String modifyLore1 = Language.getLangMessage("modify-lore-1", false);
	private static String modifyLore2 = Language.getLangMessage("modify-lore-2", false);
	private static ItemStack filler = Sekai.getMCVersion() < 13
			? new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 15)
			: new ItemStack(Material.matchMaterial("BLACK_STAINED_GLASS_PANE"), 1);
	private static ItemStack modifyingBook = new ItemStack(Material.BOOK, 1);
	private static ItemStack glasspane = Sekai.getMCVersion() < 13
			? new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 13)
			: new ItemStack(Material.matchMaterial("GREEN_STAINED_GLASS_PANE"), 1);

	public EnchantGUI()
	{
		ItemMeta fillermeta = filler.getItemMeta();
		fillermeta.setDisplayName(" ");
		filler.setItemMeta(fillermeta);
		ItemMeta meta = glasspane.getItemMeta();
		meta.setDisplayName(nextPageName);
		glasspane.setItemMeta(meta);
		ItemMeta baq = modifyingBook.getItemMeta();
		baq.setDisplayName(modifyGuiName);
		modifyingBook.setItemMeta(baq);
	}

	public static Inventory setupGUI(Player player)
	{
		Inventory inv = Bukkit.createInventory(player, 36, "Enchantments");
		createPanes(inv);
		GUIs.put(player, inv);
		guiNames.put(player, "null");
		return inv;
	}

	public static void openInventory(Player player, String listname)
	{
		if (disabled.contains(player))
		{
			Language.sendMessage(player, "cannot-open-gui");
			return;
		}
		if (modifying == player)
			modifying = null;
		guiNames.put(player, listname);
		Inventory gui = GUIs.get(player);
		if (gui == null)
			gui = setupGUI(player);

		player.openInventory(gui);
		loadInventory(player, listname);

	}

	public static void loadInventory(Player player, String guiToOpen)
	{
		Inventory inv = GUIs.get(player);
		inv.clear();
		createPanes(inv);
		List<String> l = EPS.guisFile.getStringList("guis." + guiToOpen + ".enchants");
		if (l.size() > 14)
		{
			ItemStack i = glasspane.clone();
			inv.setItem(35, i);
		}
		if (player.hasPermission("eps.admin.changegui"))
		{
			ItemStack i = modifyingBook.clone();
			inv.setItem(8, i);
		}
		for (String i : l)
			add(player, inv, i);

		ItemStack i = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(Language.getLangMessage("balance-display-in-gui", false).replaceAll("%balance%",
				ConfigSettings.isAbbreviateLargeNumbers() ? Sekai.abbreviate(economy.getBalance(player))
						: Double.toString(economy.getBalance(player))));
		i.setItemMeta(meta);
		inv.setItem(4, i);
		inv.setItem(31, i);
	}

	/**
	 * Using the amount of items specified in slot 35, calculates the next enchants
	 * to show and displays it.
	 * 
	 * Stupid
	 * 
	 * @param player      The player to show the GUI to (assuming their enchants GUI
	 *                    is open in the first place)
	 * @param enchantList The list of enchants to cycle through
	 */
	private static void nextPage(Player player, List<String> enchantList)
	{
		Inventory inv = GUIs.get(player);
		ItemStack nextPage = inv.getItem(35);
		if (nextPage.getAmount() * 14 > enchantList.size())
			nextPage.setAmount(1);
		else
			nextPage.setAmount(nextPage.getAmount() + 1);
		inv.clear();
		createPanes(inv);
		ItemStack item = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Language.getLangMessage("balance-display-in-gui", false).replaceAll("%balance%",
				Double.toString(economy.getBalance(player))));
		item.setItemMeta(meta);
		inv.setItem(4, item);
		inv.setItem(31, item);
		if (player.hasPermission("eps.admin.changegui"))
		{
			ItemStack i = modifyingBook.clone();
			inv.setItem(8, i);
		}
		for (int i = nextPage.getAmount() * 14 - 14; i < enchantList.size(); i++)
			add(player, inv, enchantList.get(i));
		inv.setItem(35, nextPage);
	}

	private static void createPanes(Inventory gui)
	{
		ItemStack slot = filler.clone();
		for (int i = 0; i < 9; i++)
			gui.setItem(i, slot);
		gui.setItem(9, slot);
		gui.setItem(17, slot);
		gui.setItem(18, slot);
		gui.setItem(26, slot);
		for (int i = 27; i < 36; i++)
			gui.setItem(i, slot);
	}

	private static void add(Player player, Inventory inventory, String key)
	{
		if (inventory.firstEmpty() == -1)
			return;
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		ItemMeta itemMeta = itemStack.getItemMeta();
		Enchantment enchant = EnchantmentInfo.findEnchantByKey(key);
		if (enchant == null)
		{
			EPS.logger.log(Level.INFO, "Invalid enchantment " + key);
			return;
		}
		YamlFile<?> enchantFile = EPS.getEnchantFile(enchant);
		Material upgradeIcon = enchantFile.getMaterialBySekai("upgradeicon");
		String desc = enchantFile.getString("upgradedesc");
		Integer maxlevel = enchantFile.getInt("maxlevel");
		String displayCost;

		if (!(itemMeta.getEnchantLevel(enchant) >= maxlevel) || player.hasPermission("eps.admin.bypassmaxlevel"))
		{
			long cost = (long) Math.floor(EPS.getCost(enchant, itemMeta.getEnchantLevel(enchant), 1));
			displayCost = ConfigSettings.isAbbreviateLargeNumbers() ? Sekai.abbreviate(cost) : Long.toString(cost);
		} else
			displayCost = "Maxed!";

		if (desc == null)
			EPS.plugin.getLogger().log(Level.WARNING,
					"Invalid upgrade description for enchant " + key.toUpperCase() + "!");
		if (maxlevel == 0)
			EPS.plugin.getLogger().log(Level.WARNING,
					"Max level for enchant " + key.toUpperCase() + " is zero! Is this intentional?");
		if (upgradeIcon == null)
			EPS.plugin.getLogger().log(Level.WARNING,
					"Invalid material type for enchantment " + key.toUpperCase() + ". Setting to default BOOK.");

		ItemStack slot = upgradeIcon == null ? new ItemStack(Material.BOOK, 1) : new ItemStack(upgradeIcon, 1);
		ItemMeta meta = slot.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + EnchantmentInfo.getDefaultName(enchant));

		List<String> lore = EnchantMetaWriter.getDescription(enchant);

		for (String s : EPS.languageFile.getStringList("enchant-gui-item-lore"))
			lore.add(ChatColor.translateAlternateColorCodes('&',
					s.replaceAll("%cost%", displayCost).replaceAll("%maxlevel%", maxlevel.toString())
							.replaceAll("%currentlevel%", Integer.toString(itemMeta.getEnchantLevel(enchant)))));

		meta.setLore(lore);
		meta.addEnchant(enchant, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		slot.setItemMeta(meta);
		inventory.setItem(inventory.firstEmpty(), slot);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryDrag(final InventoryDragEvent e)
	{
		if (Sekai.getMCVersion() < 13)
		{
			if (!Sekai.isSameInventory(e.getInventory(), GUIs.get(e.getWhoClicked())))
				return;
			e.setCancelled(true);
		} else
		{
			if (e.getInventory() == GUIs.get(e.getWhoClicked()))
				e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(final InventoryClickEvent e) throws IOException
	{
		if (e.getInventory().getHolder() != e.getWhoClicked())
			return;

		if (e.getClickedInventory() != e.getInventory())
			return;

		final ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR)
			return;

		final Player player = (Player) e.getWhoClicked();

		if (Sekai.getMCVersion() < 13)
		{
			if (!Sekai.isSameInventory(e.getInventory(), GUIs.get(player)))
				return;
			e.setCancelled(true);
		} else
		{
			if (e.getInventory() != GUIs.get(player))
				return;
			e.setCancelled(true);
		}

		String displayName = clickedItem.getItemMeta().getDisplayName();

		if (displayName.equals(nextPageName))
		{
			nextPage(player, EPS.guisFile.getStringList("guis." + guiNames.get(player) + ".enchants"));
		} else if (displayName.equals(modifyGuiName))
		{
			modifying = player;
			Inventory inv = e.getInventory();
			for (ItemStack i : inv.getContents())
			{
				ItemMeta meta = i.getItemMeta();
				if (meta.getDisplayName() != " ")
					meta.setLore(new ArrayList<String>(Arrays.asList(modifyLore1, modifyLore2)));
				i.setItemMeta(meta);
			}
		}

		Map<Enchantment, Integer> enchs = clickedItem.getItemMeta().getEnchants();
		Enchantment enchant = null;

		for (Map.Entry<Enchantment, Integer> entry : enchs.entrySet())
			enchant = entry.getKey();

		if (enchant == null)
			return;

		if (modifying == player)
		{
			if (e.isRightClick())
			{
				String path = "guis." + guiNames.get(player) + ".enchants";
				List<String> list = EPS.guisFile.getStringList(path);
				list.remove(EnchantmentInfo.getKey(enchant));
				EPS.guisFile.set(path, list);
				EPS.guisFile.saveYaml();
				e.getInventory().remove(clickedItem);
				EPS.reloadConfigs();
			} else
				new EditEnchantGUI(player, enchant);
			return;
		}

		if (e.isLeftClick())
			upgradeItemInMainHand(enchant, player, 1);

		else if (e.isRightClick())
			upgradeItemInMainHand(enchant, player, 5);

		else if (e.isShiftClick())
			upgradeItemInMainHand(enchant, player, 50);

	}

	private void upgradeItemInMainHand(Enchantment enchant, Player player, Integer levelsToIncrease)
	{
		EnchantFile enchantFile = EPS.getEnchantFile(enchant);
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		ItemMeta itemMeta = itemStack.getItemMeta();
		int currentLevel = itemMeta.getEnchantLevel(enchant);
		int upgradedLevel = currentLevel + levelsToIncrease;
		
		double cost = EPS.getCost(enchant, currentLevel, upgradedLevel);
		if (!(upgradedLevel - 1 >= enchantFile.getMaxLevel()
				|| player.hasPermission("eps.admin.bypassmaxlevel")))
		{
			if (!player.hasPermission("eps.admin.bypassincompatibilities"))
			{
				for (Map.Entry<List<String>, List<Enchantment>> entry : incpts.entrySet())
				{
					if (entry.getKey().contains(itemStack.getType().name()))
					{
						if (entry.getValue().contains(enchant))
							for (Enchantment e : entry.getValue())
								if (e != null)
									if (itemMeta.hasEnchant(e) && e != enchant)
									{
										Language.sendMessage(player, "lockedupgrade");
										return;
									}
					}
				}
			}

			if (economy.getBalance(player) >= cost)
			{
				player.sendMessage(Language.getLangMessage("upgraded-item")
						.replaceAll("%enchant%", EnchantmentInfo.getDefaultName(enchant))
						.replaceAll("%lvl%", Integer.toString(upgradedLevel)));
				itemStack.addUnsafeEnchantment(enchant, upgradedLevel);
				economy.setBalance(player, economy.getBalance(player) - cost);
				itemStack.setItemMeta(EnchantMetaWriter.getWrittenMeta(itemStack));
				loadInventory(player, guiNames.get(player));
			} else
			{
				Language.sendMessage(player, "insufficienttokens");
			}
		} else if ((currentLevel >= enchantFile.getInt("maxlevel")))
		{
			Language.sendMessage(player, "exceedmaxlvl");
		} else
		{
			Language.sendMessage(player, "maxedupgrade");
		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (e.getPlayer().isSneaking())
				return;
			PlayerInventory inv = e.getPlayer().getInventory();
			Material m = inv.getItemInMainHand().getType();
			if (m.equals(Material.BOW) || m.equals(Material.matchMaterial("CROSSBOW"))
					|| inv.getItemInOffHand().getType().equals(Material.SHIELD) || hoes.contains(m)
					|| m.equals(Material.FISHING_ROD) || (Sekai.getMCVersion() > 11 && e.getClickedBlock() != null
							&& e.getClickedBlock().getType().isInteractable()))
				return;
			if (EPS.configFile.yaml.getBoolean("open-enchant-gui-on-right-click") == true)
				EPS.enchantsCommand.onCommand(e.getPlayer(), Bukkit.getPluginCommand("enchants"), "enchants",
						new String[] { "dontshow" });
		}
	}

	public static void setupInCPTS()
	{
		ConfigurationSection cs = EPS.incompatibilitiesFile.getConfigurationSection("incompatibilities");
		incpts.clear();

		if (cs == null)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED
					+ "Invalid incompatibilities.yml file! Delete the current one to generate a new one.");
			return;
		}
		Set<String> a = cs.getKeys(false);
		for (String i : a)
		{
			List<Enchantment> enchs = new ArrayList<Enchantment>();
			for (String s : EPS.incompatibilitiesFile.getStringList("incompatibilities." + i + ".enchants"))
				enchs.add(EnchantmentInfo.findEnchantByKey(s));
			incpts.put(EPS.incompatibilitiesFile.getStringList("incompatibilities." + i + ".items"), enchs);
		}
	}

	public static void setOpenable(Player player, boolean openable)
	{
		if (openable)
			disabled.remove(player);
		else
			disabled.add(player);
	}

	@Override
	public void reload()
	{
		EnchantGUI.setupInCPTS();
		nextPageName = Language.getLangMessage("next-page", false);
		modifyGuiName = Language.getLangMessage("modify-gui", false);
		modifyLore1 = Language.getLangMessage("modify-lore-1", false);
		modifyLore2 = Language.getLangMessage("modify-lore-2", false);
		ItemMeta meta = glasspane.getItemMeta();
		meta.setDisplayName(nextPageName);
		glasspane.setItemMeta(meta);
		ItemMeta baq = modifyingBook.getItemMeta();
		meta.setDisplayName(modifyGuiName);
		modifyingBook.setItemMeta(baq);
	}
}
