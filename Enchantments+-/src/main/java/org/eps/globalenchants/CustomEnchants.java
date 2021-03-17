package org.eps.globalenchants;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.api.CustomEnchant;

public class CustomEnchants {

	public static final Enchantment FLY = CustomEnchant.newEnchant("fly", "Fly");
	public static final Enchantment REPAIR = CustomEnchant.newEnchant("repair", "Repair");
	public static final Enchantment SOULBOUND = CustomEnchant.newEnchant("soulbound", "Soulbound");
	public static final Enchantment EXPERIENCE = CustomEnchant.newEnchant("experience", "Experience");
	public static final Enchantment SOUL_DESTRUCTION = CustomEnchant.newEnchant("soul_destruction", "Soul_Destruction");
	
	public static void register()
	{
		CustomEnchant.registerEnchant(FLY);
		CustomEnchant.registerEnchant(REPAIR);
		CustomEnchant.registerEnchant(SOULBOUND);
		CustomEnchant.registerEnchant(EXPERIENCE);
		CustomEnchant.registerEnchant(SOUL_DESTRUCTION);
	}
}
