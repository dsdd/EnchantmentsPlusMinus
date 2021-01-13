package org.whyisthisnecessary.eps.legacy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.Main;

import net.md_5.bungee.api.ChatColor;

public class LegacyUtil {

	private static Material legacyMaterial;
	private static boolean legacy;
	
	public static void checkLegacy()
	{
		legacyMaterial = Material.getMaterial("BLACK_STAINED_GLASS_PANE");
		if (legacyMaterial == null)
			legacy = true;
		else
			legacy = false;
	}
	
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
	
	public static boolean isLegacy()
	{
		return legacy;
	}
	
	public static Enchantment getByName(String name)
	{
		return (NameUtil.getByName(name));
	}
	
	public static String getName(Enchantment enchant)
	{
		return (NameUtil.getName(enchant));
	}
}
