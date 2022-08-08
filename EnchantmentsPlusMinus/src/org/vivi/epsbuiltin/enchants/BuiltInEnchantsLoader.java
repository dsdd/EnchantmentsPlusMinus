package org.vivi.epsbuiltin.enchants;

import org.bukkit.Bukkit;
import org.vivi.eps.EPS;

public class BuiltInEnchantsLoader {
	
	public static boolean vaultEnabled = false;
	
	public void onEnable() 
	{
		CustomEnchants.register();
		new EnchantProcessor(EPS.plugin);		
		if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
			vaultEnabled = true;
	}
}
