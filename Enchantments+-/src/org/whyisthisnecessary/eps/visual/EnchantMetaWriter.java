package org.whyisthisnecessary.eps.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.legacy.Label;
import org.whyisthisnecessary.eps.legacy.NameUtil;

public class EnchantMetaWriter implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (e.getCurrentItem() == null) return;
		if (e.getCurrentItem().getItemMeta() == null) return;
		List<String> lore = EnchantMetaWriter.getWrittenEnchantLore(e.getCurrentItem());
		ItemMeta meta = e.getCurrentItem().getItemMeta();
		meta.setLore(lore);
    	e.getCurrentItem().setItemMeta(meta);
	}
	
	private static List<String> getWrittenEnchantLore(ItemStack item)
	{
		ItemMeta meta = item.getItemMeta();
		Map<Enchantment, Integer> map = meta.getEnchants();
		List<String> list = meta.getLore();
		
		if (list == null) list = new ArrayList<String>(Arrays.asList());
		
		Collection<Enchantment> enchants = Label.values();
		for (Map.Entry<Enchantment,Integer> entry : map.entrySet())  
		{
			if (enchants.contains(entry.getKey()))
			{
				String name = WordUtils.capitalizeFully(NameUtil.getEnchantNameMinecraft(entry.getKey()).replaceAll("_", " "));
				String lore = ChatColor.GRAY+name+" "+ entry.getValue().toString();
								
				for (int i=0;i<list.size();i++)
				{
					if (list.get(i).toLowerCase().contains(name.toLowerCase()))
						list.remove(i);
				}
				
				if (!list.contains(lore))
			    list.add(0, lore);
			}
		}
		
		return list;
	}
	
	public static ItemMeta getWrittenMeta(ItemStack item)
	{
		if (Main.Config.getBoolean("get-enchant-lore") == false) return item.getItemMeta();
		List<String> lore = EnchantMetaWriter.getWrittenEnchantLore(item);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		return meta;
	}
}
