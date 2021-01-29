package org.eps.bowadditions;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.api.CustomEnchant;

public class CustomEnchants {

	public static final Enchantment ENDERBOW = CustomEnchant.newEnchant("enderbow", "Enderbow");
	public static final Enchantment MACHINERY = CustomEnchant.newEnchant("machinery", "Machinery");
	public static final Enchantment THUNDERING_BLOW = CustomEnchant.newEnchant("thundering_blow", "Thundering_Blow");
	public static final Enchantment ENERGIZED = CustomEnchant.newEnchant("energized", "Energized");
	public static final Enchantment SHOCKWAVE = CustomEnchant.newEnchant("shockwave", "Shockwave");
	public static final Enchantment FIREWORKS = CustomEnchant.newEnchant("fireworks", "Fireworks");
	
	public static void register()
	{
		CustomEnchant.registerEnchant(ENDERBOW);
		CustomEnchant.registerEnchant(MACHINERY);
		CustomEnchant.registerEnchant(THUNDERING_BLOW);
		CustomEnchant.registerEnchant(ENERGIZED);
		CustomEnchant.registerEnchant(SHOCKWAVE);
		CustomEnchant.registerEnchant(FIREWORKS);
	}
}
