package org.whyisthisnecessary.eps.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.legacy.Label;
import org.whyisthisnecessary.eps.legacy.LegacyUtil;
import org.whyisthisnecessary.eps.legacy.NameUtil;
import org.whyisthisnecessary.eps.util.WrapUtil;
import org.whyisthisnecessary.legacywrapper.LegacyWrapper;

@SuppressWarnings("deprecation")
public class CustomEnchant {

	public static List<Enchantment> registeredEnchants = new ArrayList<Enchantment>(Arrays.asList());
	
	/**Wraps a custom enchant with the specified namespace, name and max level and returns it
	 * maxLvl only sets the maximum safe enchantment level for this enchant, and has no other use
	 * 
	 * @param namespace The Java name for this enchant
	 * @param name The display name for this enchant
	 * @param maxLvl The maximum safe enchantment level for this enchant
	 * @return A custom enchant with the specified namespace, name and max level
	 */
	@Deprecated
	public static Enchantment wrapEnchant(String namespace, String name, Integer maxLvl)
	{
		if (!LegacyUtil.isLegacy())
			return (new WrapUtil(namespace, name, maxLvl));
		else
			return (LegacyWrapper.newEnchant(namespace, name, maxLvl));
	}
	
	/**Creates a custom enchant with the specified namespace and name and returns it
	 * 
	 * @param namespace The Java name for this enchant
	 * @param name The display name for this enchant
	 * @return A custom enchant with the specified namespace, name and max level
	 */
	public static Enchantment newEnchant(String namespace, String name)
	{
		if (!LegacyUtil.isLegacy())
		{
			Enchantment e = new WrapUtil(namespace, name, 32767);
			//registerEnchant(e);
			return e;
		}
		else
		{
			Enchantment e = LegacyWrapper.newEnchant(namespace, name, 32767);
			//registerEnchant(e);
			return e;
		}
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
		if (LegacyUtil.isLegacy())
			Label.addLabel(enchant.getName(), enchant);
		else
			Label.addLabel(enchant.getKey().getKey(), enchant);
		registeredEnchants.add(enchant);
		if (!Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(enchant))
		{
			try
			{
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
				Enchantment.registerEnchantment(enchant);
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN+"Registered enchant "+NameUtil.getName(enchant).toUpperCase()+"!");
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
