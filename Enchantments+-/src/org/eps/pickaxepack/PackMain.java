package org.eps.pickaxepack;

import org.bukkit.Bukkit;
import org.whyisthisnecessary.eps.Main;

public class PackMain {

	public static boolean VaultEnabled = false;

	public void onEnable() 
	{
		CustomEnchants.register();
		new EnchantProcessor(Main.plugin);
		if (Bukkit.getPluginManager().isPluginEnabled("Vault"))
		{
			VaultEnabled = true;
		}
	}
}
