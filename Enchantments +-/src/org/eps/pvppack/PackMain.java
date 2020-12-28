package org.eps.pvppack;

import org.whyisthisnecessary.eps.Main;

public class PackMain {
	
	public PackMain(Main plugin)
	{
		CustomEnchants.register();
		new EnchantProcessor(plugin);
	}
}
