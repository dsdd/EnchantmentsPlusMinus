package org.vivi.eps.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.items.CustomEnchantedBook;
import org.vivi.eps.visual.EnchantGUI;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.epsbuiltin.enchants.Durability;

public class Events implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR))
		{
			Player p = e.getPlayer();
			ItemStack item = p.getInventory().getItemInMainHand();
			
			if (item.getItemMeta() != null)
			if (item.getItemMeta().hasLore())
			{
				String str = item.getItemMeta().getLore().get(0);
				if (str.startsWith(ChatColor.BLACK+"T:"))
				{
					int tokens = Integer.parseInt(str.split(":")[1]);
					EPS.getEconomy().changeBalance(p, tokens);
					p.getInventory().remove(item);
					p.sendMessage(Language.getLangMessage("claimed-token-pouch").replaceAll("%tokens%", Integer.toString(tokens)));
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
			if (str.startsWith(ChatColor.BLACK+"T:"))
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(PrepareAnvilEvent e)
	{
		if (!ConfigSettings.isAnvilCombiningEnabled())
			return;
		AnvilInventory anvil = e.getInventory();
		
		if (anvil.getItem(0) == null) return;
		
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
		
		Map<Enchantment, Integer> enchantments = slot2 == null ? CustomEnchantedBook.getEnchants(slot1) : CustomEnchantedBook.combineEnchants(slot1, slot2, false);

		int cost = 1;
		for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			int maxlevel = EPSConfiguration.getConfiguration(entry.getKey()).getInt("maxlevel");
			if (maxlevel != 0 && entry.getValue() > maxlevel)
			{
				item.addUnsafeEnchantment(entry.getKey(), maxlevel);
				cost = cost + maxlevel;
			}
			else
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
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		EnchantMetaWriter.refreshItem(e.getPlayer().getInventory().getItemInMainHand());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) throws IOException
	{
		Player p = e.getPlayer();
		File dataFile = new File(EPS.dataFolder, p.getUniqueId().toString()+".yml");
		EPS.uuidDataStoreData.set(p.getName(), p.getUniqueId().toString());
		try {
			EPS.uuidDataStoreData.save(EPS.uuidDataStore);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (!dataFile.exists())
			EPS.createNewFile(dataFile);
		
		FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
		dataConfig.set("tokens", dataConfig.get("tokens", 0));
		dataConfig.save(dataFile);
		EnchantGUI.setupGUI(e.getPlayer());
	}
}
