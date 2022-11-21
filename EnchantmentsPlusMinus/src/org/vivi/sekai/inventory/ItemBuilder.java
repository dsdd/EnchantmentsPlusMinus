package org.vivi.sekai.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**	Additional methods for ItemStack
 * 
 * @author oicb
 *
 */
public class ItemBuilder extends org.bukkit.inventory.ItemStack {

	public ItemBuilder()
	{
		super();
	}
	
	public ItemBuilder(org.bukkit.inventory.ItemStack stack)
	{
		super(stack);
	}
	
	public ItemBuilder(Material type)
	{
		super(type);
	}
	
	public ItemBuilder(Material type, int amount)
	{
		super(type, amount);
	}
	
	@SuppressWarnings("deprecation")
	public ItemBuilder(Material type, int amount, short damage)
	{
		super(type, amount, damage);
	}
	
	@SuppressWarnings("deprecation")
	public ItemBuilder(Material type, int amount, short damage, Byte data)
	{
		super(type, amount, damage, data);
	}
	
	public ItemBuilder(Material type, int amount, ItemMeta meta)
	{
		super(type, amount);
		this.setItemMeta(meta);
	}
	
	
	public ItemBuilder(Material type, int amount, Object... data)
	{
		super(type, amount);
		meta(data);
	}
	
	@Override
	public boolean isSimilar(ItemStack stack)
	{
		if (stack == null)
			return false;
		return (stack.getType() == this.getType()) &&
				stack.getEnchantments().equals(this.getEnchantments()) &&
				stack.getItemMeta().getDisplayName().equals(this.getItemMeta().getDisplayName()) &&
			((stack.getItemMeta().getLore() == null && this.getItemMeta().getLore() == null) ? true : stack.getItemMeta().getLore().equals(this.getItemMeta().getLore()));
	}
	
	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof ItemStack))
			return false;
		
		return isSimilar((ItemStack)object) && ((ItemStack)object).getAmount() == getAmount();
	}
	
	public ItemBuilder amount(int amount)
	{
		setAmount(amount);
		return this;
	}
	
	public ItemBuilder lore(Iterable<String> iterable)
	{
		ItemMeta meta = getItemMeta();
		List<String> lore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
		for (String s : iterable)
			lore.add(s);
		meta.setLore(lore);
		setItemMeta(meta);
		return this;
	}
	
	public ItemBuilder displayName(String name)
	{
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(name);
		setItemMeta(meta);
		return this;
	}
	
	public ItemBuilder meta(Object... data)
	{
		if (data == null)
			return this;
		ItemMeta meta = getItemMeta();
		for (int i=0;i<data.length;i++)
		{
			if (data[i] instanceof String)
			{
				if (i < 1)
					meta.setDisplayName((String) data[i]);
				else
				{
					List<String> lore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
					lore.add((String) data[i]);
					meta.setLore(lore);
				}
			}
			else if (data[i] instanceof List)
			{
				for (Object o : (List<?>)data[i])
					if (o instanceof String)
					{
						List<String> lore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
						lore.add((String) o);
						meta.setLore(lore);
					}
			}
			else if (data[i] instanceof Enchantment)
			{
				int a = 1;
				if (data.length > i+1)
					if (data[i+1] instanceof Integer)
						a = (int) data[i+1];
				meta.addEnchant((Enchantment)data[i], a, true);
			}
					
			else if (data[i] instanceof ItemFlag)
				meta.addItemFlags((ItemFlag)data[i]);
		}
		setItemMeta(meta);
		return this;
	}
}
