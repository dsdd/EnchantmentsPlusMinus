package org.eps.globalenchants;

import org.vivi.eps.Main;

public class PackMain {

	public static boolean VaultEnabled = false;

	public void onEnable() 
	{
		CustomEnchants.register();
		new EnchantProcessor(Main.plugin);
	}
}
