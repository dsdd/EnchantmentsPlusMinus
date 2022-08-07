package org.vivi.eps.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class VaultHook {
	private static Economy economy;
	
	/**Sets up the Economy class for Vault.
	 * Does not do anything if Vault is not installed.
	 * 
	 * @return Returns the Economy class for Vault.
	 */
	public static Economy setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return null;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return null;
        }
        economy = rsp.getProvider();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Successfully hooked into Vault!");
        return economy;
    }
	
	/**Returns the Economy class for Vault. 
	 * Returns null if Vault is not installed.
	 * Returns null if setupEconomy() is not executed.
	 * 
	 * @return Returns the Economy class for Vault.
	 */
	public static Economy getEconomy()
	{
		return economy;
	}
}
