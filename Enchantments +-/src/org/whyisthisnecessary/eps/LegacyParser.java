package org.whyisthisnecessary.eps;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

@SuppressWarnings("deprecation")
public class LegacyParser {
	
	public static boolean legacy = false;
	
	public LegacyParser(Main plugin)
	{
		Material m = Material.getMaterial("BLACK_STAINED_GLASS_PANE");
		if (m == null)
			LegacyParser.legacy = true;
		else
			LegacyParser.legacy = false;
	}
	
	public static Enchantment getByKey(NamespacedKey key)
	{
		if (LegacyParser.legacy = true)
		return Enchantment.getByName(key.getKey());
		else
		return Enchantment.getByKey(key);
	}

	public static String getKey(Enchantment enchant)
	{
		if (LegacyParser.legacy = true)
		return enchant.getName();
		else
		return enchant.getKey().getKey();
	}
}
