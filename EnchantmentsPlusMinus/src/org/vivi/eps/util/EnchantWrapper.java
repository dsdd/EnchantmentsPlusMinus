package org.vivi.eps.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EnchantFile;
import org.vivi.sekai.enchantment.EnchantmentInfo;

public class EnchantWrapper extends Enchantment
{

	public EnchantWrapper(String key)
	{
		super(NamespacedKey.minecraft(key));
	}

	@Override
	public boolean canEnchantItem(ItemStack item)
	{
		return canEnchantItem(this, item);
	}

	@Override
	public boolean conflictsWith(Enchantment other)
	{
		return conflicts(this, other);
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return null;
	}

	@Override
	public int getMaxLevel()
	{
		return getMaxLevel(this);
	}

	@Override
	public String getName()
	{
		return EnchantmentInfo.getName(this);
	}

	@Override
	public int getStartLevel()
	{
		return 0;
	}

	public boolean isCursed()
	{
		return false;
	}

	public boolean isTreasure()
	{
		return false;
	}
	
	private static boolean canEnchantItem(Enchantment enchant, ItemStack itemStack)
	{
		for (Map.Entry<Set<Material>, List<Enchantment>> entry : EPS.guis.entrySet())
			if (entry.getKey().contains(itemStack.getType()))
			{
				for (Enchantment foundEnchant : entry.getValue())
					if (enchant.equals(foundEnchant))
						return true;
			}
		return false;
	}

	private static int getMaxLevel(Enchantment enchant)
	{
		EnchantFile enchantFile = EPS.getEnchantFile(enchant);
		return enchantFile == null ? 32767 : (enchantFile.exists() ? enchantFile.getMaxLevel() : 32767);
	}

	private static boolean conflicts(Enchantment enchant0, Enchantment enchant1)
	{
		for (Set<Enchantment> enchantSet : EPS.incompatibilities)
			if (enchantSet.contains(enchant0) && enchantSet.contains(enchant1))
				return true;
		return false;
	}

	public static class Legacy extends Enchantment
	{
		private String key;

		public Legacy(String key)
		{
			super(convertLetters(key).intValue());
			this.key = key;
		}

		public String getName()
		{
			return key;
		}

		public int getMaxLevel()
		{
			return EnchantWrapper.getMaxLevel(this);
		}

		public int getStartLevel()
		{
			return 0;
		}

		public EnchantmentTarget getItemTarget()
		{
			return null;
		}

		public boolean isTreasure()
		{
			return false;
		}

		public boolean isCursed()
		{
			return false;
		}

		public boolean conflictsWith(Enchantment other)
		{
			return EnchantWrapper.conflicts(this, other);
		}

		public boolean canEnchantItem(ItemStack item)
		{
			return EnchantWrapper.canEnchantItem(this, item);
		}

		private static Integer convertLetters(String s)
		{
			String t = "";
			for (int i = 0; i < s.length(); i++)
			{
				char ch = s.charAt(i);
				if (!t.isEmpty())
					t = String.valueOf(t) + " ";
				int n = ch - 97 + 1;
				t = String.valueOf(t) + String.valueOf(n);
			}
			return Integer.valueOf(Integer.parseInt(t));
		}
	}
}
