package org.eps.globalenchants;

import org.vivi.eps.EPS;

public class PackMain {

	public static boolean VaultEnabled = false;

	public void onEnable() 
	{
		CustomEnchants.register();
		new EnchantProcessor(EPS.plugin);
	}
}
