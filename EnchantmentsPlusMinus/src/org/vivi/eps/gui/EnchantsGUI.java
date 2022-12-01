package org.vivi.eps.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.Events;
import org.vivi.eps.api.EnchantFile;
import org.vivi.eps.util.Language;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.inventory.GUIBuilder;
import org.vivi.sekai.inventory.ItemBuilder;

public class EnchantsGUI
{
	private static final Set<EnchantsGUI> ENCHANTS_GUIS = new HashSet<EnchantsGUI>();
	public static ItemStack fillerItemStack;
	public static ItemStack modifyGuiItemStack;
	public static ItemStack nextPageItemStack;
	public static Inventory itemSelectorInventory;

	private static Player currentlyEditing = null;
	private final Player player;
	private final Inventory inventory;
	private ItemStack itemStackToEnchant;
	private List<Enchantment> enchants = new ArrayList<Enchantment>();
	private int pageNumber = 1;
	private boolean disabled = false;

	private EnchantsGUI(Player player)
	{
		this.player = player;
		inventory = GUIBuilder.build(player, 36, Language.getLangMessage("enchants-gui-label", false))
				.constructBorder(fillerItemStack).toInventory();
		ENCHANTS_GUIS.add(this);
	}

	/**
	 * Opens the Enchants GUI of the specified {@link ItemStack}.
	 * 
	 * @param itemStackToEnchant Item to enchant
	 * @param pageNumber         Which page the Enchants GUI will be in, default 1
	 * @return True if an enchants GUI was opened, false if not
	 */
	public boolean open(ItemStack itemStackToEnchant, int pageNumber)
	{
		if (disabled)
		{
			Language.sendMessage(player, "cannot-open-gui");
			return false;
		}
		if (player.equals(currentlyEditing)
				&& ((this.itemStackToEnchant == itemStackToEnchant && this.pageNumber == pageNumber)
						|| (this.itemStackToEnchant != itemStackToEnchant)))
		{
			currentlyEditing = null;
			Events.enchantToMove = null;
		}

		enchants = null;
		for (Map.Entry<Set<Material>, List<Enchantment>> entry : EPS.guis.entrySet())
			if (entry.getKey().contains(itemStackToEnchant.getType()))
				enchants = entry.getValue();

		if (enchants == null)
			return false;

		if (pageNumber * 14 - 14 > enchants.size())
			pageNumber = 1;

		this.itemStackToEnchant = itemStackToEnchant;
		this.pageNumber = pageNumber;
		GUIBuilder.build(inventory).clear().constructBorder(fillerItemStack);

		if (enchants.size() > 14)
		{
			inventory.setItem(17, nextPageItemStack);
			inventory.setItem(26, nextPageItemStack);
		}
		if (player.hasPermission("eps.admin.changegui"))
			inventory.setItem(8, modifyGuiItemStack);

		ItemStack balanceDisplay = new ItemBuilder(Material.GOLD_INGOT, 1)
				.displayName(Language.getLangMessage("balance-display-in-gui", false).replaceAll("%balance%",
						EPS.abbreviate(EPS.getEconomy().getBalance(player))));
		inventory.setItem(4, balanceDisplay);
		inventory.setItem(31, balanceDisplay);

		for (int i = pageNumber * 14 - 14; i < enchants.size(); i++)
			addEnchant(enchants.get(i));

		player.openInventory(inventory);
		return true;
	}

	public void refresh()
	{
		open(itemStackToEnchant, pageNumber);
	}

	public void nextPage()
	{
		open(itemStackToEnchant, pageNumber + 1);
	}

	private void addEnchant(Enchantment enchant)
	{
		if (inventory.firstEmpty() == -1)
			return;
		ItemMeta itemMeta = itemStackToEnchant.getItemMeta();
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

		List<String> lore = new ArrayList<String>(EPS.getEnchantDescriptionLines(enchant));
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

	public void setOpenable(boolean openable)
	{
		disabled = !openable;
	}

	public Inventory toInventory()
	{
		return inventory;
	}

	public int getPageNumber()
	{
		return pageNumber;
	}

	public List<Enchantment> getEnchants()
	{
		return enchants;
	}

	public ItemStack getItemStackToEnchant()
	{
		return itemStackToEnchant;
	}

	public static void setCurrentlyEditing(Player player)
	{
		currentlyEditing = player;
		if (player == null)
			return;

		List<String> modifyGuiEnchantLore = Sekai.translateAlternateColorCodes('&',
				EPS.languageFile.getStringList("modify-gui.modify-enchant-lore"));
		for (ItemStack itemStack : EnchantsGUI.get(player).inventory.getContents())
			if (itemStack != null && itemStack.getItemMeta() != null)
			{
				ItemMeta itemMeta = itemStack.getItemMeta();
				String displayName = itemMeta.getDisplayName();
				if (!displayName.equals(fillerItemStack.getItemMeta().getDisplayName())
						&& !displayName.equals(modifyGuiItemStack.getItemMeta().getDisplayName())
						&& !displayName.equals(nextPageItemStack.getItemMeta().getDisplayName()))
				{
					itemMeta.setLore(modifyGuiEnchantLore);
					itemStack.setItemMeta(itemMeta);
				}
			}
	}

	public static Player getCurrentlyEditing()
	{
		return currentlyEditing;
	}

	public static EnchantsGUI get(Player player)
	{
		for (EnchantsGUI enchantsGui : ENCHANTS_GUIS)
			if (enchantsGui.player.equals(player))
				return enchantsGui;
		return new EnchantsGUI(player);
	}
}
