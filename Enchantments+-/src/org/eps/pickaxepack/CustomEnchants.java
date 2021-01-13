package org.eps.pickaxepack;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.api.CustomEnchant;

public class CustomEnchants {

	public static final Enchantment HASTE = CustomEnchant.newEnchant("haste", "Haste");
	public static final Enchantment EXPLOSIVE = CustomEnchant.newEnchant("explosive", "Explosive");
	public static final Enchantment AUTOSMELT = CustomEnchant.newEnchant("autosmelt", "AutoSmelt");
	public static final Enchantment TELEPATHY = CustomEnchant.newEnchant("telepathy", "Telepathy");
	public static final Enchantment TOKENBLOCKS = CustomEnchant.newEnchant("tokenblocks", "TokenBlocks");
	public static final Enchantment MONEYBLOCKS = CustomEnchant.newEnchant("moneyblocks", "MoneyBlocks");
	public static final Enchantment TOKENCHARITY = CustomEnchant.newEnchant("tokencharity", "TokenCharity");
	public static final Enchantment CHARITY = CustomEnchant.newEnchant("charity", "Charity");
	public static final Enchantment EXCAVATE = CustomEnchant.newEnchant("excavate", "Excavate");
	public static final Enchantment DIAMOND = CustomEnchant.newEnchant("diamond", "Diamond");
	
	public static void register()
	{
		CustomEnchant.registerEnchant(HASTE);
		CustomEnchant.registerEnchant(EXPLOSIVE);
		CustomEnchant.registerEnchant(AUTOSMELT);
		CustomEnchant.registerEnchant(TELEPATHY);
		CustomEnchant.registerEnchant(TOKENBLOCKS);
		CustomEnchant.registerEnchant(MONEYBLOCKS);
		CustomEnchant.registerEnchant(TOKENCHARITY);
		CustomEnchant.registerEnchant(CHARITY);
		CustomEnchant.registerEnchant(EXCAVATE);
		CustomEnchant.registerEnchant(DIAMOND);
	}
}
