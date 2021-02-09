package org.whyisthisnecessary.eps;

import org.whyisthisnecessary.eps.economy.TokenEconomy;
import org.whyisthisnecessary.eps.economy.VaultEconomy;
import org.whyisthisnecessary.eps.util.Dictionary;

import java.io.File;

import org.bukkit.Material;
import org.whyisthisnecessary.eps.api.Reloadable;
import org.whyisthisnecessary.eps.economy.Economy;

public class EPS implements Reloadable {

	private static TokenEconomy te = new TokenEconomy();
	private static VaultEconomy ve = new VaultEconomy();
	private static Dictionary dictionary = new Dictionary.Defaults();
	private static boolean legacy = Material.getMaterial("BLACK_STAINED_GLASS_PANE") == null;
	private static boolean umeiot = Main.Config.getBoolean("use-money-economy-instead-of-tokens");
	
	protected EPS() {}
	
	/** Gets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static Economy getEconomy()
	{
		return umeiot ? ve : te;
	}
	
	/** Gets the plugin folder (/plugins/EnchantmentsPlusMinus)
	 * 
	 * @return The plugin folder
	 */
	public static File getPluginFolder()
	{
		return Main.plugin.getDataFolder();
	}
	
	/** Gets the enchants folder (/plugins/EnchantmentsPlusMinus/enchants)
	 * 
	 * @return The enchants folder
	 */
	public static File getEnchantsFolder()
	{
		return Main.EnchantsFolder;
	}
	
	/** Gets the data folder (/plugins/EnchantmentsPlusMinus/data)
	 * 
	 * @return The data folder
	 */
	public static File getDataFolder()
	{
		return Main.DataFolder;
	}
	
	/** Gets the main dictionary of EPS.
	 * 
	 * @return The main dictionary of EPS.
	 */
	public static Dictionary getDictionary()
	{
		return dictionary;
	}
	
	/** Returns true if the MC server is 1.12 or below.
	 * 
	 * @return Returns true if the MC server is 1.12 or below.
	 */
	public static boolean onLegacy()
	{
		return legacy;
	}
	
	/**
	 *  Registers the specified Reloadable to be ready for listening.
	 */
	public static void registerReloader(Reloadable r)
	{
		Reloadable.addReloader(r);
	}

	@Override
	public void reload() {
		umeiot = Main.Config.getBoolean("use-money-economy-instead-of-tokens");
	}
}
