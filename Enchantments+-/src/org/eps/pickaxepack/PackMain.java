package org.eps.pickaxepack;

import org.bukkit.Bukkit;
import org.whyisthisnecessary.eps.Main;

public class PackMain {

	public static boolean VaultEnabled = false;
	
	public PackMain(Main plugin) 
	{
		CustomEnchants.register();
		new EnchantProcessor(plugin);
		if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
		{
			VaultEnabled = true;
			Vault.setupEconomy();
		}
	}
}
