package org.eps.pickaxepack;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.EnchantRegisterer;

public class CustomEnchants extends EnchantRegisterer {

	public static final Enchantment HASTE = wrapEnchant("haste", "Haste", 32767);
	public static final Enchantment EXPLOSIVE = wrapEnchant("explosive", "Explosive", 32767);
	public static final Enchantment AUTOSMELT = wrapEnchant("autosmelt", "AutoSmelt", 32767);
	public static final Enchantment TELEPATHY = wrapEnchant("telepathy", "Telepathy", 32767);
	public static final Enchantment TOKENBLOCKS = wrapEnchant("tokenblocks", "TokenBlocks", 32767);
	public static final Enchantment MONEYBLOCKS = wrapEnchant("moneyblocks", "MoneyBlocks", 32767);
	public static final Enchantment TOKENCHARITY = wrapEnchant("tokencharity", "TokenCharity", 32767);
	public static final Enchantment CHARITY = wrapEnchant("charity", "Charity", 32767);
	
	public static void register()
	{
		register(HASTE);
		register(EXPLOSIVE);
		register(AUTOSMELT);
		register(TELEPATHY);
		register(TOKENBLOCKS);
		register(TOKENCHARITY);
		register(MONEYBLOCKS);
		register(CHARITY);
	}
}
