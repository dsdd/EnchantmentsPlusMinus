package org.vivi.eps.api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.vivi.eps.EPS;

public class EPSPlayerData
{
	private static Map<UUID, EPSPlayerData> playerDataCache = new HashMap<UUID, EPSPlayerData>();

	private UUID uuid;
	private File dataFile;
	private YamlConfiguration data;
	private double tokens = 0;

	/**
	 * Will retrieve the data file of the UUID.
	 * 
	 * @param uuid The player's UUID
	 */
	private EPSPlayerData(UUID uuid)
	{
		this.uuid = uuid;
		playerDataCache.put(uuid, this);
		dataFile = new File(EPS.dataFolder, uuid.toString() + ".yml");
		if (!dataFile.exists())
			try
			{
				dataFile.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		data = YamlConfiguration.loadConfiguration(dataFile);
		tokens = data.getDouble("tokens", 0);
	}

	/**
	 * Returns amount of tokens listed in the player data.
	 * 
	 * @return Amount of tokens
	 */
	public double getTokens()
	{
		return tokens;
	}

	/**
	 * Sets the amount of tokens in the player data to the specified value.
	 * 
	 * @param value The amount of tokens to set to
	 */
	public void setTokens(double value)
	{
		tokens = value;
		data.set("tokens", value);
		try
		{
			data.save(dataFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Returns the UUID linked to the player data
	 * 
	 * @return The UUID linked to the player data- The player's UUID
	 */
	public UUID getUUID()
	{
		return uuid;
	}

	/**
	 * Gets the UUID linked to the player name (Minecraft user-name). Will return
	 * null if the player has never joined before.
	 * 
	 * @param playerName The player's name
	 * @return The UUID of the player
	 */
	public static UUID getUUID(String playerName)
	{
		String stringUUID = EPS.uuidDataStoreData.getString(playerName);
		if (stringUUID == null)
		{
			// Async... it will return null the first time the operation is run but luckily
			// human behavior is typing commands again if it doesnt work so its fine(?)
			Bukkit.getScheduler().runTaskAsynchronously(EPS.plugin, new Runnable() {

				@Override
				public void run()
				{
					@SuppressWarnings("deprecation")
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
					if (offlinePlayer.hasPlayedBefore())
						EPSPlayerData.getPlayerData(offlinePlayer.getUniqueId());
				}

			});
		}

		return UUID.fromString(stringUUID);
	}

	/**
	 * Gets the player data of the specified UUID.
	 * 
	 * @param uuid UUID to retrieve player data
	 * @return Linked player data
	 */
	public static EPSPlayerData getPlayerData(UUID uuid)
	{
		return playerDataCache.containsKey(uuid) ? playerDataCache.get(uuid) : new EPSPlayerData(uuid);
	}
}
