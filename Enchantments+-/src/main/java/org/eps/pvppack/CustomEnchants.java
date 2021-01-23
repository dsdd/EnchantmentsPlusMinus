package org.eps.pvppack;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.api.CustomEnchant;

public class CustomEnchants {

	public static final Enchantment JAGGED = CustomEnchant.newEnchant("jagged", "Jagged");
	public static final Enchantment RETALIATE = CustomEnchant.newEnchant("retaliate", "Retaliate");
	public static final Enchantment LIFESTEAL = CustomEnchant.newEnchant("lifesteal", "Lifesteal");
	public static final Enchantment MOMENTUM = CustomEnchant.newEnchant("momentum", "Momentum");
	public static final Enchantment POISONOUS = CustomEnchant.newEnchant("poisonous", "Poisonous");
	public static final Enchantment VOLCANIC = CustomEnchant.newEnchant("volcanic", "Volcanic");
	public static final Enchantment SATURATED = CustomEnchant.newEnchant("saturated", "Saturated");
	public static final Enchantment INSATIABLE = CustomEnchant.newEnchant("insatiable", "Insatiable");
	public static final Enchantment BEHEADING = CustomEnchant.newEnchant("beheading", "Beheading");
	public static final Enchantment STIFFEN = CustomEnchant.newEnchant("stiffen", "Stiffen");
	public static final Enchantment LASTRESORT = CustomEnchant.newEnchant("last_resort", "Last_Resort");
	
	
	public static void register()
	{
		CustomEnchant.registerEnchant(JAGGED);
		CustomEnchant.registerEnchant(RETALIATE);
		CustomEnchant.registerEnchant(LIFESTEAL);
		CustomEnchant.registerEnchant(MOMENTUM);
		CustomEnchant.registerEnchant(POISONOUS);
		CustomEnchant.registerEnchant(VOLCANIC);
		CustomEnchant.registerEnchant(SATURATED);
		CustomEnchant.registerEnchant(INSATIABLE);
		CustomEnchant.registerEnchant(BEHEADING);
		CustomEnchant.registerEnchant(STIFFEN);
		CustomEnchant.registerEnchant(LASTRESORT);
	}
}
