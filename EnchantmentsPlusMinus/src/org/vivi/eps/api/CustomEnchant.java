package org.vivi.eps.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.EPS;
import org.vivi.eps.Main;
import org.vivi.eps.legacy.Label;
import org.vivi.eps.util.Wrapper;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.whyisthisnecessary.legacywrapper.LegacyWrapper;

@SuppressWarnings("deprecation")
public class CustomEnchant {

	public static List<Enchantment> registeredEnchants = new ArrayList<Enchantment>(Arrays.asList());
	private static List<String> disabledEnchants = Main.Config.getStringList("disabled-enchants");
	
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
	 * @return A custom enchant with the specified namespace and name
	 */
	public static Enchantment newEnchant(String namespace, String name)
	{
		if (disabledEnchants.contains(name))
			return SpecialEnchants.NULL;
		if (disabledEnchants.contains(namespace))
			return SpecialEnchants.NULL;
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
		if (enchant == SpecialEnchants.NULL)
			return false;
		String name = EPS.onLegacy() ? enchant.getName() : enchant.getKey().getKey();
		Label.addLabel(name, enchant);
		registeredEnchants.add(enchant);
		File enchantfile = new File(Main.EnchantsFolder, EPS.getDictionary().getName(enchant)+".yml");
		if (enchantfile.exists())
			EPSConfiguration.fgMap.put(enchant, EPSConfiguration.loadConfiguration(enchantfile));
		if (!Arrays.asList(Enchantment.values()).contains(enchant))
		{
			try
			{
				Enchantment.registerEnchantment(enchant);
				EnchantMetaWriter.init(enchant);
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
		{
			EnchantMetaWriter.init(enchant);
			return false;
		}
		
	}
}

class SpecialEnchants {
	
	public static final Enchantment NULL = CustomEnchant.newEnchant("null", "null");
}
