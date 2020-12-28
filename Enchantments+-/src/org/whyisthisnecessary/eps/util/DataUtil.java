package org.whyisthisnecessary.eps.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.Main;

public class DataUtil {

	public static File getUserDataFile(String playername)
	{
		String UUID = Main.UUIDDataStoreConfig.getString(playername);
		File DataFile = new File(Main.DataFolder, UUID+".yml");
		return DataFile;
	}
	
	public static File getUserDataFile(Player player)
	{
		String UUID = Main.UUIDDataStoreConfig.getString(player.getName());
		File DataFile = new File(Main.DataFolder, UUID+".yml");
		return DataFile;
	}
	
	public static String getUUID(String playername)
	{
		return Main.UUIDDataStoreConfig.getString(playername);
	}
	
	public static String getUUID(Player player)
	{
		return Main.UUIDDataStoreConfig.getString(player.getName());
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
	
	public static boolean playerExists(String playername)
	{
		File file = getUserDataFile(playername);
		if (file.exists()) 
		    return true;
		else
			return false;
	}
}
