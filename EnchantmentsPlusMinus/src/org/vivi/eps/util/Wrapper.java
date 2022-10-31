package org.vivi.eps.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.vivi.eps.EPS;

public class Wrapper extends Enchantment {

	private static final EnchantDictionary dictionary = EPS.getDictionary();
	private final String name;
	private final List<Enchantment> incompatibilities = new ArrayList<Enchantment>();

	public Wrapper(String namespace, String name)
	{
		super(NamespacedKey.minecraft(namespace));
		this.name = name;
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
		for (Enchantment enchant : incompatibilities)
			if (enchant.equals(arg0))
				return true;

		for (String key : EPS.incompatibilitiesData.getKeys(false))
		{
			List<String> incompatibilities = EPS.incompatibilitiesData.getStringList(key);
			boolean contains = false;
			for (String enchantName : incompatibilities)
				if (enchantName.equalsIgnoreCase(dictionary.getName(this)))
				{
					contains = true;
					break;
				}
			if (contains)
				for (String enchantName : incompatibilities)
					if (enchantName.equalsIgnoreCase(dictionary.getName(arg0)))
					{
						this.incompatibilities.add(arg0);
						return true;
					}

		}
		return false;
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
		return name;
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

	public static class LegacyWrapper extends Enchantment {

		private String name;
		private int maxLvl;

		public LegacyWrapper(String namespace, String name)
		{
			super(convertLetters(namespace).intValue());
			this.name = name;
			this.maxLvl = 32767;
		}

		public String getName()
		{
			return this.name;
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
