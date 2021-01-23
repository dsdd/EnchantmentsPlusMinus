package org.whyisthisnecessary.eps.legacy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.Main;

import net.md_5.bungee.api.ChatColor;

public class LegacyUtil {

	private static Material legacyMaterial;
	private static boolean legacy;
	
	/** Checks if the MC version is 1.12 or lower.
	 */
	public static void checkLegacy()
	{
		legacyMaterial = Material.getMaterial("BLACK_STAINED_GLASS_PANE");
		if (legacyMaterial == null)
			legacy = true;
		else
			legacy = false;
	}
	
	/** Initializes legacy support.
	 * For internal legacy support only, should never be used by plugins!
	 * 
	 * @param plugin The plugin to disable in case LegacyWrapper is not installed.
	 */
	public static void initialize(Main plugin)
	{
		boolean check = Bukkit.getPluginManager().isPluginEnabled("LegacyWrapper");
		if (!check && legacy == true) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Sorry, but it seems that there was an error downloading LegacyWrapper. "
					+ "To prevent data corruption, Enchantments+- will be forcefully disabled."
					+ "If this mistake is unintentional, please report this to TreuGames for further investigation.");
			Bukkit.getPluginManager().disablePlugin(plugin);
		}
	}
	
	/** Returns if the MC server is 1.12 or below.
	 * 
	 * @return Returns if the MC server is 1.12 or below.
	 */
	public static boolean isLegacy()
	{
		return legacy;
	}
	
	/** Gets an enchant by its name.
	 * 
	 * @param name The name of the enchant
	 * @return The enchant
	 */
	public static Enchantment getByName(String name)
	{
		return (NameUtil.getByName(name));
	}
	
	/** Gets the name of an enchant.
	 * 
	 * @param enchant The enchant
	 * @return The name of the enchant
	 */
	public static String getName(Enchantment enchant)
	{
		return (NameUtil.getName(enchant));
	}
}
