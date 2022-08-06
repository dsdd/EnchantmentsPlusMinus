package org.vivi.eps;

import java.io.File;

import org.bukkit.Material;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.economy.Economy;
import org.vivi.eps.economy.TokenEconomy;
import org.vivi.eps.economy.VaultEconomy;
import org.vivi.eps.util.Dictionary;

public class EPS implements Reloadable {

	private static Dictionary dictionary = new Dictionary.Defaults();
	private static boolean legacy = Material.getMaterial("BLACK_STAINED_GLASS_PANE") == null;
	private static boolean umeiot = Main.Config.getBoolean("use-money-economy-instead-of-tokens");
	private static Economy economy = umeiot ? new VaultEconomy() : new TokenEconomy();
	
	protected EPS() {}
	
	/** Gets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static Economy getEconomy()
	{
		return economy;
	}
	
	/** Sets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static void setEconomy(Economy economy)
	{
		EPS.economy = economy;
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
	
	/** Sets the main dictionary of EPS.
	 * 
	 * @return The main dictionary of EPS.
	 */
	public static void setDictionary(Dictionary dictionary)
	{
		EPS.dictionary = dictionary;
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

	
	/**
	 * Fires reload() in all registered Reloadable classes.
	 */
	public static void reloadConfigs()
	{
		for (Reloadable r : Reloadable.CLASSES)
			r.reload();
	}
	
	@Override
	public void reload() {
		umeiot = Main.Config.getBoolean("use-money-economy-instead-of-tokens");
	}
}
