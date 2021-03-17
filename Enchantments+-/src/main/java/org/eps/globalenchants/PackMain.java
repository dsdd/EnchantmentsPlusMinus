package org.eps.globalenchants;

import org.whyisthisnecessary.eps.Main;

public class PackMain {

	public static boolean VaultEnabled = false;

	public void onEnable() 
	{
		CustomEnchants.register();
		new EnchantProcessor(Main.plugin);
	}
}
