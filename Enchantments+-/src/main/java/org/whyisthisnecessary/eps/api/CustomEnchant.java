package org.whyisthisnecessary.eps.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.EPS;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.legacy.Label;
import org.whyisthisnecessary.eps.util.Wrapper;
import org.whyisthisnecessary.legacywrapper.LegacyWrapper;

@SuppressWarnings("deprecation")
public class CustomEnchant {

	public static List<Enchantment> registeredEnchants = new ArrayList<Enchantment>(Arrays.asList());
	
	/**Wraps a custom enchant with the specified namespace, name and max level and returns it
	 * maxLvl only sets the maximum safe enchantment level for this enchant, and has no other use
	 * 
	 * @deprecated Deprecated. Use newEnchant() instead.
	 * @param namespace The Java name for this enchant
	 * @param name The display name for this enchant
	 * @param maxLvl The maximum safe enchantment level for this enchant
	 * @return A custom enchant with the specified namespace, name and max level
	 */
	@Deprecated
	public static Enchantment wrapEnchant(String namespace, String name, Integer maxLvl)
	{
		if (!EPS.onLegacy())
			return (new Wrapper(namespace, name, maxLvl));
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
		return EPS.onLegacy() ? LegacyWrapper.newEnchant(namespace, name, 32767) : new Wrapper(namespace, name, 32767);
	}
	
	/**Registers an enchant for use.
	 * Without registering an enchant, the enchant becomes unusable.
	 * 
	 * @param enchant The enchant you want to register.
	 * @return Returns if the registering was successful.
	 */
	public static boolean registerEnchant(Enchantment enchant)
	{
		if (EPS.onLegacy())
			Label.addLabel(enchant.getName(), enchant);
		else
			Label.addLabel(enchant.getKey().getKey(), enchant);
		registeredEnchants.add(enchant);
		File enchantfile = new File(Main.EnchantsFolder, EPS.getDictionary().getName(enchant)+".yml");
		if (enchantfile.exists())
			EPSConfiguration.fgMap.put(enchant, EPSConfiguration.loadConfiguration(enchantfile));
		if (!Arrays.asList(Enchantment.values()).contains(enchant))
		{
			try
			{
				Enchantment.registerEnchantment(enchant);
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN+"Registered enchant "+EPS.getDictionary().getName(enchant).toUpperCase()+"!");
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		}
		else
			return false;
		
	}
}
