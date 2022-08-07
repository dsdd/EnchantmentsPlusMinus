package org.vivi.eps.items;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.google.common.collect.Maps;


public class CustomEnchantedBook extends ItemStack {
	
	public CustomEnchantedBook(Map<Enchantment, Integer> enchants)
	{
		super(Material.ENCHANTED_BOOK, 1);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) getItemMeta();
		for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet())
		{
			meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
		}
		setItemMeta(meta);
		
	}
	
	public ItemStack getItemStack()
	{
		return this;
	}
	
	public static ItemStack getItemStack(CustomEnchantedBook book)
	{
		return book;
	}
	
	public static CustomEnchantedBook getCustomEnchantedBook(ItemStack item)
	{
		if (item instanceof CustomEnchantedBook)
			return (CustomEnchantedBook)item;
		else
			return null;
		
	}
	
	public static Map<Enchantment, Integer> getEnchants(ItemStack item)
	{
		if (item.getItemMeta() instanceof EnchantmentStorageMeta)
			return ((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants();
		else
			return item.getItemMeta().getEnchants();
	}
	
	public static Map<Enchantment, Integer> combineEnchants(ItemStack item, ItemStack item1, boolean safe)
	{
		Map<Enchantment, Integer> combine = Maps.newHashMap(getEnchants(item));
		
		for (Map.Entry<Enchantment, Integer> entry : getEnchants(item1).entrySet())
		{
			Integer first = combine.get(entry.getKey());
			if (first == null)
			{
				combine.put(entry.getKey(), entry.getValue());
				continue;
			}
			Integer lvl = 0;
			if (first == entry.getValue() && first > 0)
				lvl = first+1;
			if (first > entry.getValue())
				lvl = first;
			if (first < entry.getValue())
				lvl = entry.getValue();
			if (safe && lvl > entry.getKey().getMaxLevel())
				lvl = entry.getKey().getMaxLevel();
			if (combine.get(entry.getKey()) != null)
		    combine.put(entry.getKey(), lvl);
		}
		return combine;
	}
}
