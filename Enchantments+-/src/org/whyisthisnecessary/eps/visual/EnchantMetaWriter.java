package org.whyisthisnecessary.eps.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class EnchantMetaWriter implements Listener {
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e)
	{
		ItemMeta meta = EnchantMetaWriter.getWrittenEnchantLore(e.getPlayer().getInventory().getItemInMainHand());
    	e.getPlayer().getInventory().getItemInMainHand().setItemMeta(meta);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (e.getWhoClicked() == e.getInventory().getHolder())
		{
		    ItemMeta meta = EnchantMetaWriter.getWrittenEnchantLore(e.getWhoClicked().getInventory().getItemInMainHand());
    	    e.getWhoClicked().getInventory().getItemInMainHand().setItemMeta(meta);
		}
	}
	
	public static ItemMeta getWrittenEnchantLore(ItemStack item)
	{
		ItemMeta meta = item.getItemMeta();
		Map<Enchantment, Integer> map = meta.getEnchants();
		List<String> list = meta.getLore();
		meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
		
		if (list == null) list = new ArrayList<String>(Arrays.asList());
		for (Map.Entry<Enchantment,Integer> entry : map.entrySet())  
		{
			String lore = ChatColor.GRAY+WordUtils.capitalizeFully(entry.getKey().getKey().getKey())+" "+ entry.getValue().toString();
			if (!list.contains(lore))
		    list.add(0, lore);
		}
		meta.setLore(list);
		return meta;
	}
}
