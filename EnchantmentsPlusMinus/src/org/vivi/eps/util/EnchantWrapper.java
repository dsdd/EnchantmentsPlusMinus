package org.vivi.eps.util;

import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.vivi.eps.EPS;
import org.vivi.sekai.enchantment.EnchantmentInfo;

public class EnchantWrapper extends Enchantment {

	public EnchantWrapper(String key)
	{
		super(NamespacedKey.minecraft(key));
	}

	// Defaults to true for now.
	@Override
	public boolean canEnchantItem(ItemStack arg0)
	{
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment arg0)
	{
		return conflicts(this, arg0);
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return null;
	}

	@Override
	public int getMaxLevel()
	{
		return 32767;
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
	
	private static boolean conflicts(Enchantment enchant0, Enchantment enchant1)
	{
		for (Set<Enchantment> enchantSet : EPS.incompatibilities)
			if (enchantSet.contains(enchant0) && enchantSet.contains(enchant1))
				return true;
		return false;
	}

	public static class Legacy extends Enchantment {

		private int maxLvl;

		public Legacy(String namespace)
		{
			super(convertLetters(namespace).intValue());
			this.maxLvl = 32767;
		}

		public String getName()
		{
			return EnchantmentInfo.getName(this);
		}

		public int getMaxLevel()
		{
			return maxLvl;
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

		public boolean conflictsWith(Enchantment paramEnchantment)
		{
			return false;
		}

		public boolean canEnchantItem(ItemStack paramItemStack)
		{
			return false;
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
