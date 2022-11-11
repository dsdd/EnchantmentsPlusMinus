package org.vivi.sekai.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultHook
{
	private static Economy economy;

	/**
	 * Hooks into the Vault API. If Vault is not loaded into the server, this will
	 * return false.
	 * 
	 * @return Returns if Vault had successfully been hooked into.
	 */
	public static boolean hook()
	{
		if (Bukkit.getPluginManager().getPlugin("Vault") == null)
			return false;

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
			return false;

		economy = rsp.getProvider();
		return true;
	}

	/**
	 * Returns the Economy instance for Vault. Returns null if Vault had not been
	 * hooked into prior to this operation.
	 * 
	 * @return Returns the Economy instance for Vault.
	 */
	public static Economy getEconomy()
	{
		return economy;
	}
}
