package org.vivi.eps.gui;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EnchantFile;
import org.vivi.eps.util.Language;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.inventory.GUIHolder;

public class EditEnchantGUI implements Listener
{
	private static Inventory inventory = null;
	private static Enchantment enchant = null;
	private static String key = null;
	private static boolean editing = false;

	private static EditEnchantGUI editEnchantGui = null;

	private EditEnchantGUI()
	{
		Bukkit.getPluginManager().registerEvents(this, EPS.plugin);
	}

	public static void open(Enchantment enchant)
	{
		if (editEnchantGui == null)
			editEnchantGui = new EditEnchantGUI();
		EditEnchantGUI.enchant = enchant;
		Map<String, Object> entries = EPS.getEnchantFile(enchant).getValues(true);
		int size = (int) (Math.ceil((double) entries.size() / 9) * 9);
		inventory = Bukkit.createInventory(new GUIHolder(), size,
				"Modifying " + EnchantmentInfo.getKey(enchant).toUpperCase() + "...");
		for (Map.Entry<String, Object> entry : entries.entrySet())
		{
			if (entry.getValue() instanceof MemorySection)
				continue;

			ItemStack itemStack = new ItemStack(Material.BOOK, 1);
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(ChatColor.GREEN + (ChatColor.BOLD + entry.getKey()));
			itemMeta.setLore(Arrays.asList(ChatColor.DARK_GREEN + "Value: " + entry.getValue().toString()));
			itemStack.setItemMeta(itemMeta);
			inventory.addItem(itemStack);
		}
		EnchantsGUI.getCurrentlyEditing().openInventory(inventory);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (e.getClickedInventory() != e.getInventory())
			return;

		if (!Sekai.isSameInventory(inventory, e.getInventory()))
			return;

		ItemStack clickedItem = e.getCurrentItem();

		if (clickedItem == null || clickedItem.getType() == Material.AIR)
			return;

		EnchantsGUI.getCurrentlyEditing().closeInventory();
		key = clickedItem.getItemMeta().getDisplayName().substring(4);
		EnchantsGUI.getCurrentlyEditing()
				.sendMessage(Language.getLangMessage("modifying-config").replace("%entry%", key));
		editing = true;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		Player player = EnchantsGUI.getCurrentlyEditing();
		if (e.getPlayer() == player && editing)
		{
			e.setCancelled(true);
			if (!e.getMessage().equals("cancel")) // Not using ignore case just in case :O
			{
				EnchantFile enchantFile = EPS.getEnchantFile(enchant);
				enchantFile.set(key, parse(e.getMessage()));
				enchantFile.saveYaml();
				EPS.reloadConfigs();
				Language.sendMessage(player, "modified-config");
				EPS.logger.log(Level.INFO, "Player " + player.getName() + " changed " + key + " in "
						+ enchantFile.getPath() + " to " + e.getMessage());
				Bukkit.getScheduler().runTask(EPS.plugin, new Runnable() {
					@Override
					public void run()
					{
						EnchantsGUI.get(player).refresh();
						;
					}
				});
			}
		}
	}

	private static Object parse(String s)
	{
		if (isNumeric(s))
			return Double.parseDouble(s);
		else if (isBoolean(s))
			return Boolean.parseBoolean(s);
		else if (s.length() == 1)
			return s.charAt(0);
		else
			return s;
	}

	private static boolean isNumeric(String s)
	{
		try
		{
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e)
		{
			return false;
		}
	}

	private static boolean isBoolean(String s)
	{
		return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
	}
}
