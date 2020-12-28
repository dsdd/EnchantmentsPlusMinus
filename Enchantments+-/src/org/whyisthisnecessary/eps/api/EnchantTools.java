package org.whyisthisnecessary.eps.api;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.util.WrapUtil;

public class EnchantTools {

	/**Wraps a custom enchant with the specified namespace, name and max level and returns it
	 * maxLvl only sets the maximum safe enchantment level for this enchant, and has no other use
	 * 
	 * @param namespace The Java name for this enchant
	 * @param name The display name for this enchant
	 * @param maxLvl The maximum safe enchantment level for this enchant
	 * @return A custom enchant with the specified namespace, name and max level
	 */
	public static Enchantment wrapEnchant(String namespace, String name, Integer maxLvl)
	{
		return (new WrapUtil(namespace, name, maxLvl));
	}
		
	/**Registers an enchant for use.
	 * Without registering an enchant, the enchant becomes unusable.
	 * Note that already registered enchants will print an error if registered again.
	 * 
	 * @param enchant The enchant you want to register.
	 * @return Returns if the registering was successful.
	 */
	public static boolean registerEnchant(Enchantment enchant)
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
