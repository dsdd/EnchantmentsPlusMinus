package org.whyisthisnecessary.eps.legacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public class Label {

	private static Map<String, Enchantment> enchants = new HashMap<String, Enchantment>();
	
	public static void addLabel(String name, Enchantment enchant)
	{
		enchants.put(name.toUpperCase(), enchant);
	}
	
	public static Enchantment getEnchant(String name)
	{
		if (enchants.containsKey(name.toUpperCase()))
			return enchants.get(name);
		else
			return null;
	}
	
	public static Collection<Enchantment> values()
	{
		return enchants.values();
	}
	
	public static Collection<String> keys()
	{
		return (new ArrayList<String>(enchants.keySet()));
	}
}
