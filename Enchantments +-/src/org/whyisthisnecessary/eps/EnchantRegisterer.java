package org.whyisthisnecessary.eps;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.internal.EnchantmentWrapper;

public class EnchantRegisterer {
	
	public static Enchantment wrapEnchant(String namespace, String name, Integer maxLvl)
	{
		return (new EnchantmentWrapper(namespace, name, maxLvl));
	}
		
	public static boolean register(Enchantment enchant)
	{
		boolean registered = false;
		if (!Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(enchant))
		{
			try
			{
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
				Enchantment.registerEnchantment(enchant);
			}
			catch (Exception e)
			{
				registered = false;
				e.printStackTrace();
			}
			return registered;
		}
		else
	    return registered;
		
	}

}
