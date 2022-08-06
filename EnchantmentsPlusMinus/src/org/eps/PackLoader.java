package org.eps;

import org.vivi.eps.EPS;

public class PackLoader {

	public PackLoader(EPS plugin)
	{
		new org.eps.pickaxepack.PackMain().onEnable();
		new org.eps.pvppack.PackMain().onEnable();
		new org.eps.bowadditions.PackMain().onEnable();
		new org.eps.globalenchants.PackMain().onEnable();
		new org.eps.tokenrewards.EnchantProcessor(plugin);
	}
}
