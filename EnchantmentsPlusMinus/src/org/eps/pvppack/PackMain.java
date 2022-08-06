package org.eps.pvppack;

import org.vivi.eps.EPS;

public class PackMain {
	
	public void onEnable() {
		CustomEnchants.register();
		new EnchantProcessor(EPS.plugin);		
	}
}
