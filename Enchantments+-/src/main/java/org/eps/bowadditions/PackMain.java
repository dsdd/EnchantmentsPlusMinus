package org.eps.bowadditions;

import org.bukkit.Bukkit;
import org.whyisthisnecessary.eps.Main;

public class PackMain {

	public void onEnable()
	{
		CustomEnchants.register();
		Bukkit.getPluginManager().registerEvents(new EnchantProcessor(), Main.plugin);
	}
}
