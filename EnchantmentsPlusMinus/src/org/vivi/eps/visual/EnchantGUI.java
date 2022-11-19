package org.vivi.eps.visual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.vivi.eps.EPS.EnchantMetaWriter;
import org.vivi.eps.api.EnchantFile;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.inventory.GUIBuilder;

@SuppressWarnings("deprecation")
public class EnchantGUI implements Listener, Reloadable
{

	public static Map<Player, Inventory> GUIs = new HashMap<Player, Inventory>();
	public static Map<Player, List<Enchantment>> cachedOpenGUIs = new HashMap<Player, List<Enchantment>>();
	private static List<Player> disabled = new ArrayList<Player>();
	private static Player modifying = null;
	private static Enchantment enchantToMove = null;
	private static String nextPageName = Language.getLangMessage("next-page", false);
	private static ItemStack fillerItemStack = Sekai.getMCVersion() < 13
			? new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 15)
			: new ItemStack(Material.matchMaterial("BLACK_STAINED_GLASS_PANE"), 1);
	private static ItemStack modifyGuiItemStack = new ItemStack(Material.BOOK, 1);
	private static ItemStack nextPageItemStack = Sekai.getMCVersion() < 13
			? new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 13)
			: new ItemStack(Material.matchMaterial("GREEN_STAINED_GLASS_PANE"), 1);
	private static String modifyGuiToggleLabel = "";
	private static List<String> modifyGuiEnchantLore = new ArrayList<String>();

	public EnchantGUI()
	{
		reload();
	}

	public static Inventory setupGUI(Player player)
	{
		Inventory inventory = new GUIBuilder(
				Bukkit.createInventory(player, 36, Language.getLangMessage("enchants-gui-label", false)))
				.constructBorder(fillerItemStack).toInventory();
		GUIs.put(player, inventory);
		cachedOpenGUIs.put(player, new ArrayList<Enchantment>());
		return inventory;
	}

	public static void openInventory(Player player, List<Enchantment> enchants)
	{
		if (disabled.contains(player))
		{
			Language.sendMessage(player, "cannot-open-gui");
			return;
		}
		if (modifying == player)
			modifying = null;
		enchantToMove = null;
		cachedOpenGUIs.put(player, enchants);
		Inventory inventory = GUIs.get(player);
		if (inventory == null)
			inventory = setupGUI(player);

		player.openInventory(inventory);
		loadInventory(player);

	}

	public static void loadInventory(Player player)
	{
		Inventory inventory = new GUIBuilder(GUIs.get(player)).clear().constructBorder(fillerItemStack).toInventory();
		List<Enchantment> enchants = cachedOpenGUIs.get(player);
		if (enchants.size() > 14)
		{
			inventory.setItem(17, nextPageItemStack);
			inventory.setItem(26, nextPageItemStack);
		}
		if (player.hasPermission("eps.admin.changegui"))
		{
			ItemStack i = modifyGuiItemStack.clone();
			inventory.setItem(8, i);
		}
		for (Enchantment enchant : enchants)
			put(player, inventory, enchant);

		ItemStack i = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(Language.getLangMessage("balance-display-in-gui", false).replaceAll("%balance%",
				EPS.abbreviate(EPS.getEconomy().getBalance(player))));
		i.setItemMeta(meta);
		inventory.setItem(4, i);
		inventory.setItem(31, i);
	}

	/**
	 * Using the amount of items specified in slot 35, calculates the next enchants
	 * to show and displays it.
	 * 
	 * Stupid
	 * 
	 * @param player      The player to show the GUI to (assuming their enchants GUI
	 *                    is open in the first place)
	 * @param enchants The list of enchants to cycle through
	 */
	private static void nextPage(Player player)
	{
		Inventory inventory = GUIs.get(player);
		List<Enchantment> enchants = cachedOpenGUIs.get(player);
		
		ItemStack nextPage = inventory.getItem(17);
		if (nextPage.getAmount() * 14 > enchants.size())
			nextPage.setAmount(1);
		else
			nextPage.setAmount(nextPage.getAmount() + 1);
		inventory = new GUIBuilder(inventory).clear().constructBorder(fillerItemStack).toInventory();
		ItemStack balanceDisplayItemStack = new ItemStack(Material.GOLD_INGOT, 1);
		ItemMeta balanceDisplayItemMeta = balanceDisplayItemStack.getItemMeta();
		balanceDisplayItemMeta.setDisplayName(Language.getLangMessage("balance-display-in-gui", false)
				.replaceAll("%balance%", Double.toString(EPS.getEconomy().getBalance(player))));
		balanceDisplayItemStack.setItemMeta(balanceDisplayItemMeta);
		inventory.setItem(4, balanceDisplayItemStack);
		inventory.setItem(31, balanceDisplayItemStack);
		if (player.hasPermission("eps.admin.changegui"))
		{
			ItemStack i = modifyGuiItemStack.clone();
			inventory.setItem(8, i);
		}
		for (int i = nextPage.getAmount() * 14 - 14; i < enchants.size(); i++)
			put(player, inventory, enchants.get(i));
		inventory.setItem(17, nextPage);
		inventory.setItem(26, nextPage);

		if (modifying == player)
			addModifyLore(inventory);
	}

	private static void put(Player player, Inventory inventory, Enchantment enchant)
	{
		if (inventory.firstEmpty() == -1)
			return;
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		ItemMeta itemMeta = itemStack.getItemMeta();
		EnchantFile enchantFile = EPS.getEnchantFile(enchant);
		String displayCost = !(itemMeta.getEnchantLevel(enchant) >= enchantFile.getMaxLevel())
				|| player.hasPermission("eps.admin.bypassmaxlevel")
						? EPS.abbreviate((long) Math.floor(EPS.getCost(enchant, itemMeta.getEnchantLevel(enchant), 1)))
						: "Maxed!";

		if (enchantFile.getEnchantDescription() == null)
			EPS.logger.log(Level.WARNING, "Invalid upgrade description for enchant " + EnchantmentInfo.getKey(enchant));
		if (enchantFile.getMaxLevel() < 1)
			EPS.logger.log(Level.WARNING,
					"Max level for enchant " + EnchantmentInfo.getKey(enchant) + " less than 1; may be unintentional");
		if (enchantFile.getUpgradeIcon() == null)
			EPS.logger.log(Level.INFO, "Invalid material type for enchantment " + EnchantmentInfo.getKey(enchant)
					+ ". Setting to default BOOK.");

		ItemStack slot = enchantFile.getUpgradeIcon() == null ? new ItemStack(Material.BOOK, 1)
				: new ItemStack(enchantFile.getUpgradeIcon(), 1);
		ItemMeta meta = slot.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + EnchantmentInfo.getName(enchant));

		@SuppressWarnings("unchecked")
		List<String> lore = (List<String>) EPS.getEnchantDescriptionLines(enchant).clone();

		for (String s : EPS.languageFile.getStringList("enchant-gui-item-lore"))
			lore.add(ChatColor.translateAlternateColorCodes('&',
					s.replaceAll("%cost%", displayCost)
							.replaceAll("%maxlevel%", Integer.toString(enchantFile.getMaxLevel()))
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

	private static void addModifyLore(Inventory inventory)
	{
		for (ItemStack itemStack : inventory.getContents())
			if (itemStack != null && itemStack.getItemMeta() != null)
			{
				ItemMeta itemMeta = itemStack.getItemMeta();
				if (!itemStack.isSimilar(fillerItemStack) && !itemStack.isSimilar(modifyGuiItemStack)
						&& !itemStack.isSimilar(nextPageItemStack))
				{
					itemMeta.setLore(modifyGuiEnchantLore);
					itemStack.setItemMeta(itemMeta);
				}
			}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(final InventoryClickEvent e) throws IOException
	{
		if (e.getInventory().getHolder() != e.getWhoClicked() || e.getClickedInventory() != e.getInventory())
			return;

		final ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR)
			return;

		final Player player = (Player) e.getWhoClicked();

		if ((Sekai.getMCVersion() < 13 && !Sekai.isSameInventory(e.getInventory(), GUIs.get(player)))
				|| (Sekai.getMCVersion() >= 13 && e.getInventory() != GUIs.get(player)))
			return;
		e.setCancelled(true);

		String displayName = clickedItem.getItemMeta().getDisplayName();
		if (displayName.equals(nextPageName))
		{
			nextPage(player);
			return;
		} else if (displayName.equals(modifyGuiToggleLabel))
		{
			modifying = player;
			addModifyLore(e.getInventory());
			return;
		}

		try
		{
			Enchantment enchant = clickedItem.getItemMeta().getEnchants().keySet().iterator().next();

			int pageNumber = e.getInventory().getItem(17).getAmount();

			if (modifying == player)
			{
				String path = "guis." + cachedOpenGUIs.get(player) + ".enchants";
				List<String> list = EPS.guisFile.getStringList(path);

				if (e.isRightClick())
				{
					list.remove(EnchantmentInfo.getKey(enchant));
					EPS.guisFile.set(path, list);
					EPS.guisFile.saveYaml();
					e.getInventory().remove(clickedItem);
					EPS.reloadConfigs();
					modifying = null;
				} else if (e.isShiftClick())
				{
					e.setCurrentItem(fillerItemStack);
					enchantToMove = enchant;
					player.sendMessage(ChatColor.translateAlternateColorCodes('&',
							EPS.languageFile.getString("modify-gui.move-notification")));
					return;
				} else if (enchantToMove != null)
				{
					Collections.swap(list, list.indexOf(EnchantmentInfo.getKey(enchantToMove)),
							list.indexOf(EnchantmentInfo.getKey(enchant)));
					EPS.guisFile.set(path, list);
					EPS.guisFile.saveYaml();
					EPS.reloadConfigs();
				} else
				{
					new EditEnchantGUI(player, enchant);
					return;
				}
			} else
				upgradeItemInMainHand(player, enchant, e.isLeftClick() ? 1 : (e.isShiftClick() ? 50 : 5));
			loadInventory(player);
			for (int i = 1; i < pageNumber; i++)
				nextPage(player);
		} catch (NoSuchElementException e1)
		{
		}

	}

	protected void upgradeItemInMainHand(Player player, Enchantment enchant, int levelsToIncrease)
	{
		EnchantFile enchantFile = EPS.getEnchantFile(enchant);
		ItemStack itemStack = player.getInventory().getItemInMainHand();
		ItemMeta itemMeta = itemStack.getItemMeta();
		int currentLevel = itemMeta.getEnchantLevel(enchant);
		int upgradedLevel = currentLevel + levelsToIncrease;

		double cost = EPS.getCost(enchant, currentLevel, levelsToIncrease);
		if (!(upgradedLevel - 1 >= enchantFile.getMaxLevel()) || player.hasPermission("eps.admin.bypassmaxlevel"))
		{
			if (!player.hasPermission("eps.admin.bypassincompatibilities"))
			{
				for (Set<Enchantment> incompatibleEnchants : EPS.incompatibilities)
				{
					if (incompatibleEnchants.contains(enchant))
						for (Enchantment e : incompatibleEnchants)
							if (e != null)
								if (itemMeta.hasEnchant(e) && !e.equals(enchant))
								{
									Language.sendMessage(player, "lockedupgrade");
									return;
								}
				}
			}

			if (EPS.getEconomy().getBalance(player) >= cost)
			{
				player.sendMessage(Language.getLangMessage("upgraded-item")
						.replaceAll("%enchant%", EnchantmentInfo.getName(enchant))
						.replaceAll("%lvl%", Integer.toString(upgradedLevel)));
				itemStack.addUnsafeEnchantment(enchant, upgradedLevel);
				EPS.getEconomy().setBalance(player, EPS.getEconomy().getBalance(player) - cost);
				itemStack.setItemMeta(EnchantMetaWriter.getWrittenMeta(itemStack));
			} else
				Language.sendMessage(player, "insufficienttokens");
		} else if ((currentLevel >= enchantFile.getInt("maxlevel")))
			Language.sendMessage(player, "exceedmaxlvl");
		else
			Language.sendMessage(player, "maxedupgrade");

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
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
		nextPageName = Language.getLangMessage("next-page", false);
		modifyGuiToggleLabel = ChatColor.translateAlternateColorCodes('&',
				EPS.languageFile.getString("modify-gui.toggle-label"));
		modifyGuiEnchantLore.clear();
		EPS.languageFile.getStringList("modify-gui.modify-enchant-lore")
				.forEach(line -> modifyGuiEnchantLore.add(ChatColor.translateAlternateColorCodes('&', line)));

		ItemMeta fillermeta = fillerItemStack.getItemMeta();
		fillermeta.setDisplayName(" ");
		fillerItemStack.setItemMeta(fillermeta);
		ItemMeta meta = nextPageItemStack.getItemMeta();
		meta.setDisplayName(nextPageName);
		nextPageItemStack.setItemMeta(meta);
		ItemMeta baq = modifyGuiItemStack.getItemMeta();
		baq.setDisplayName(modifyGuiToggleLabel);
		modifyGuiItemStack.setItemMeta(baq);
	}
}
