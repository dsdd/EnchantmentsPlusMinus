package org.eps.pvppack;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.EnchantRegisterer;

public class CustomEnchants extends EnchantRegisterer {

	public static final Enchantment JAGGED = wrapEnchant("jagged", "Jagged", 32767);
	public static final Enchantment RETALIATE = wrapEnchant("retaliate", "Retaliate", 32767);
	public static final Enchantment LIFESTEAL = wrapEnchant("lifesteal", "Lifesteal", 32767);
	public static final Enchantment MOMENTUM = wrapEnchant("momentum", "Momentum", 32767);
	public static final Enchantment POISONOUS = wrapEnchant("poisonous", "Poisonous", 32767);
	public static final Enchantment VOLCANIC = wrapEnchant("volcanic", "Volcanic", 32767);
	public static final Enchantment SATURATED = wrapEnchant("saturated", "saturated", 32767);
	public static final Enchantment INSATIABLE = wrapEnchant("insatiable", "Insatiable", 32767);
	
	public static void register()
	{
		register(JAGGED);
		register(RETALIATE);
		register(LIFESTEAL);
		register(MOMENTUM);
		register(POISONOUS);
		register(VOLCANIC);
		register(SATURATED);
		register(INSATIABLE);
	}
}
