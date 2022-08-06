package org.eps.pvppack;

import org.vivi.eps.Main;

public class PackMain {
	
	public void onEnable() {
		CustomEnchants.register();
		new EnchantProcessor(Main.plugin);		
	}
}
