package org.vivi.eps.api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.vivi.eps.EPS;

public class EPSPlayerData
{
	private static Map<UUID, EPSPlayerData> playerDataCache = new HashMap<UUID, EPSPlayerData>();
	
	private UUID uuid;
	private File dataFile;
	private YamlConfiguration data;
	private double tokens = 0;
	
	/** Will retrieve the data file of the UUID.
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
	
	/** Returns amount of tokens listed in the player data.
	 * 
	 * @return Amount of tokens
	 */
	public double getTokens()
	{
		return tokens;
	}
	
	/** Sets the amount of tokens in the player data to the specified value.
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
	
	/** Returns the UUID linked to the player data
	 * 
	 * @return The UUID linked to the player data- The player's UUID
	 */
	public UUID getUUID()
	{
		return uuid;
	}
	
	public static EPSPlayerData getPlayerData(UUID uuid)
	{
		return playerDataCache.containsKey(uuid) ? playerDataCache.get(uuid) : new EPSPlayerData(uuid);
	}
}
