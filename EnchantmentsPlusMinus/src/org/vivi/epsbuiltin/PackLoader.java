package org.vivi.epsbuiltin;

import org.vivi.eps.EPS;

public class PackLoader {

	public PackLoader(EPS plugin)
	{
		new org.vivi.epsbuiltin.enchants.PackMain().onEnable();
		new org.vivi.epsbuiltin.tokenrewards.EnchantProcessor(plugin);
	}
}
