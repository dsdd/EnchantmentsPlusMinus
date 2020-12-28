package org.eps.pvppack;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.api.EnchantTools;

public class CustomEnchants {

	public static final Enchantment JAGGED = EnchantTools.wrapEnchant("jagged", "Jagged", 32767);
	public static final Enchantment RETALIATE = EnchantTools.wrapEnchant("retaliate", "Retaliate", 32767);
	public static final Enchantment LIFESTEAL = EnchantTools.wrapEnchant("lifesteal", "Lifesteal", 32767);
	public static final Enchantment MOMENTUM = EnchantTools.wrapEnchant("momentum", "Momentum", 32767);
	public static final Enchantment POISONOUS = EnchantTools.wrapEnchant("poisonous", "Poisonous", 32767);
	public static final Enchantment VOLCANIC = EnchantTools.wrapEnchant("volcanic", "Volcanic", 32767);
	public static final Enchantment SATURATED = EnchantTools.wrapEnchant("saturated", "saturated", 32767);
	public static final Enchantment INSATIABLE = EnchantTools.wrapEnchant("insatiable", "Insatiable", 32767);
	
	public static void register()
	{
		EnchantTools.registerEnchant(JAGGED);
		EnchantTools.registerEnchant(RETALIATE);
		EnchantTools.registerEnchant(LIFESTEAL);
		EnchantTools.registerEnchant(MOMENTUM);
		EnchantTools.registerEnchant(POISONOUS);
		EnchantTools.registerEnchant(VOLCANIC);
		EnchantTools.registerEnchant(SATURATED);
		EnchantTools.registerEnchant(INSATIABLE);
	}
}
