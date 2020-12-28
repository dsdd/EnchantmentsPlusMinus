package org.eps.pickaxepack;

import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.api.EnchantTools;

public class CustomEnchants {

	public static final Enchantment HASTE = EnchantTools.wrapEnchant("haste", "Haste", 32767);
	public static final Enchantment EXPLOSIVE = EnchantTools.wrapEnchant("explosive", "Explosive", 32767);
	public static final Enchantment AUTOSMELT = EnchantTools.wrapEnchant("autosmelt", "AutoSmelt", 32767);
	public static final Enchantment TELEPATHY = EnchantTools.wrapEnchant("telepathy", "Telepathy", 32767);
	public static final Enchantment TOKENBLOCKS = EnchantTools.wrapEnchant("tokenblocks", "TokenBlocks", 32767);
	public static final Enchantment MONEYBLOCKS = EnchantTools.wrapEnchant("moneyblocks", "MoneyBlocks", 32767);
	public static final Enchantment TOKENCHARITY = EnchantTools.wrapEnchant("tokencharity", "TokenCharity", 32767);
	public static final Enchantment CHARITY = EnchantTools.wrapEnchant("charity", "Charity", 32767);
	
	public static void register()
	{
		EnchantTools.registerEnchant(HASTE);
		EnchantTools.registerEnchant(EXPLOSIVE);
		EnchantTools.registerEnchant(AUTOSMELT);
		EnchantTools.registerEnchant(TELEPATHY);
		EnchantTools.registerEnchant(TOKENBLOCKS);
		EnchantTools.registerEnchant(TOKENCHARITY);
		EnchantTools.registerEnchant(MONEYBLOCKS);
		EnchantTools.registerEnchant(CHARITY);
	}
}
