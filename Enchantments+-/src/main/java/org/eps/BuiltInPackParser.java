package org.eps;

import org.whyisthisnecessary.eps.Main;

public class BuiltInPackParser {

	public BuiltInPackParser(Main plugin)
	{
		new org.eps.pickaxepack.PackMain().onEnable();
		new org.eps.pvppack.PackMain().onEnable();
		new org.eps.bowadditions.PackMain().onEnable();
		new org.eps.tokenrewards.EnchantProcessor(plugin);
	}
}
