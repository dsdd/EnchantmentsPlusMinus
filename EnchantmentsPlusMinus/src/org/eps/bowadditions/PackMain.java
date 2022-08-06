package org.eps.bowadditions;

import org.bukkit.Bukkit;
import org.vivi.eps.EPS;

public class PackMain {

	public void onEnable()
	{
		CustomEnchants.register();
		Bukkit.getPluginManager().registerEvents(new EnchantProcessor(), EPS.plugin);
	}
}
