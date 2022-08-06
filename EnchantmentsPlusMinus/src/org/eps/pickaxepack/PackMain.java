package org.eps.pickaxepack;

import org.bukkit.Bukkit;
import org.vivi.eps.EPS;

public class PackMain {

	public static boolean VaultEnabled = false;

	public void onEnable() 
	{
		CustomEnchants.register();
		new EnchantProcessor(EPS.plugin);
		if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
		{
			VaultEnabled = true;
		}
	}
}
