package org.vivi.eps.legacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;

public class Label {

	private static Map<String, Enchantment> enchants = new HashMap<String, Enchantment>();
	
	/** Adds a new Label with the specified name and enchant.
	 * For internal legacy support, should never be used by plugins!
	 * 
	 * @param name The name of the Label.
	 * @param enchant The corresponding enchant.
	 */
	public static void addLabel(String name, Enchantment enchant)
	{
		enchants.put(name.toUpperCase(), enchant);
	}
	
	/** Gets the corresponding enchant from the specified name.
	 * 
	 * @param name The name of the enchant you want to get
	 * @return The corresponding enchant
	 */
	public static Enchantment getEnchant(String name)
	{
		if (enchants.containsKey(name.toUpperCase()))
			return enchants.get(name);
		else
			return null;
	}
	
	/** Returns all label enchants.
	 * 
	 * @return A collection of enchants
	 */
	public static Collection<Enchantment> values()
	{
		return enchants.values();
	}
	
	/** Returns all label names.
	 * 
	 * @return A collection of names
	 */
	public static Collection<String> keys()
	{
		return (new ArrayList<String>(enchants.keySet()));
	}
}
