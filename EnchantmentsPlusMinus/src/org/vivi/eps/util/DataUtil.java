package org.vivi.eps.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.vivi.eps.Main;

public class DataUtil {

	private DataUtil() {}
	
	/** Gets the data file of the specified user.
	 * 
	 * @param playername The user in question
	 * @return The data file of the user
	 */
	public static File getUserDataFile(String playername)
	{
		String UUID = Main.UUIDDataStoreConfig.getString(playername);
		File DataFile = new File(Main.DataFolder, UUID+".yml");
		return DataFile;
	}
	
	/** Gets the data file of the specified player.
	 * 
	 * @param player The player in question
	 * @return The data file of the player
	 */
	public static File getUserDataFile(Player player)
	{
		String UUID = Main.UUIDDataStoreConfig.getString(player.getName());
		File DataFile = new File(Main.DataFolder, UUID+".yml");
		return DataFile;
	}
	
	/** Gets the UUID of the specified player.
	 * Will return null if the player has never joined.
	 * 
	 * @param playername The player
	 * @return The UUID of the player
	 */
	public static String getUUID(String playername)
	{
		return Main.UUIDDataStoreConfig.getString(playername);
	}
	
	/** Gets the UUID of the specified player.
	 * Will return null if the player has never joined.
	 * 
	 * @param player The player
	 * @return The UUID of the player
	 */
	public static String getUUID(Player player)
	{
		return getUUID(player.getName());
	}
	
	/** Saves a configuration file without having to add try/catch statement to reduce lines.
	 * 
	 * @param config The configuration file
	 * @param file The file of the configuration
	 */
	public static void saveConfig(FileConfiguration config, File file)
	{
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Checks if the specified player has ever joined before.
	 * 
	 * @param playername The player
	 * @return The player's existence on the server.
	 */
	public static boolean playerExists(String playername)
	{
		File file = getUserDataFile(playername);
		return file != null;
	}
}
