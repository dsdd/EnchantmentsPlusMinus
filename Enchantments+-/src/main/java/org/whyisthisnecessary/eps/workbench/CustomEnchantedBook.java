package org.whyisthisnecessary.eps.workbench;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;


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
	
	private static Map<Enchantment, Integer> getEnchants(ItemStack item)
	{
		if (item.getItemMeta() instanceof EnchantmentStorageMeta)
			return ((EnchantmentStorageMeta)item.getItemMeta()).getStoredEnchants();
		else
			return item.getItemMeta().getEnchants();
	}
	
	public Map<Enchantment, Integer> combineEnchants(ItemStack item, boolean safe)
	{
		Map<Enchantment, Integer> combine = getEnchants(this);
		
		for (Map.Entry<Enchantment, Integer> entry : getEnchants(item).entrySet())
		{
			Integer first = combine.get(entry.getKey());
			if (first == null) continue;
			Integer lvl = first+entry.getValue();
			if (safe && lvl > entry.getKey().getMaxLevel())
				lvl = entry.getKey().getMaxLevel();
			if (combine.get(entry.getKey()) != null)
		    combine.put(entry.getKey(), lvl);
		}
		return combine;
	}
	
	public static Map<Enchantment, Integer> combineEnchants(ItemStack item, ItemStack item1, boolean safe)
	{
		Map<Enchantment, Integer> combine = getEnchants(item);
		
		for (Map.Entry<Enchantment, Integer> entry : getEnchants(item1).entrySet())
		{
			Integer first = combine.get(entry.getKey());
			if (first == null)
			{
				combine.put(entry.getKey(), entry.getValue());
				continue;
			}
			Integer lvl = first+entry.getValue();
			if (safe && lvl > entry.getKey().getMaxLevel())
				lvl = entry.getKey().getMaxLevel();
			if (combine.get(entry.getKey()) != null)
		    combine.put(entry.getKey(), lvl);
		}
		return combine;
	}
}
