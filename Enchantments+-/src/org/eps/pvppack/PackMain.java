package org.eps.pvppack;

import org.whyisthisnecessary.eps.Main;

public class PackMain {
	
	public void onEnable() {
		CustomEnchants.register();
		new EnchantProcessor(Main.plugin);		
	}
}
