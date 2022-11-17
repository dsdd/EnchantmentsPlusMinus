package org.vivi.eps.visual;

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

	private Inventory inventory = null;
	private Player player = null;
	private boolean editing = false;
	private String key = null;
	private EnchantFile currentEnchantFile = null;

	public EditEnchantGUI(Player player, Enchantment enchant)
	{
		Bukkit.getPluginManager().registerEvents(this, EPS.plugin);
		this.player = player;
		currentEnchantFile = EPS.getEnchantFile(enchant);
		Map<String, Object> entries = currentEnchantFile.getValues(true);
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
		player.openInventory(inventory);
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

		player.closeInventory();
		key = clickedItem.getItemMeta().getDisplayName().substring(4);
		player.sendMessage(Language.getLangMessage("modifying-config").replace("%entry%", key));
		editing = true;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		if (e.getPlayer() == player && editing)
		{
			e.setCancelled(true);
			if (!e.getMessage().equals("cancel")) // Not using ignore case just in case :O
			{
				currentEnchantFile.set(key, parse(e.getMessage()));
				currentEnchantFile.saveYaml();
				EPS.reloadConfigs();
				Language.sendMessage(player, "modified-config");
				EPS.logger.log(Level.INFO, "Player " + player.getName() + " changed " + key + " in "
						+ currentEnchantFile.getPath() + " to " + e.getMessage());
				Bukkit.getScheduler().runTask(EPS.plugin, new Runnable() {
					@Override
					public void run()
					{
						EnchantGUI.openInventory(player, EnchantGUI.guiNames.get(player));
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
