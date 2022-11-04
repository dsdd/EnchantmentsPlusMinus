package org.vivi.sekai;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class PlayerKeeper
{
	private static Map<Player, Map<Object, Long>> keepingMap = new HashMap<Player, Map<Object, Long>>();

	private PlayerKeeper()
	{
	}

	/**
	 * Keeps a value into a key specific to the specified {@code Player}.
	 * 
	 * @param player The {@code Player} to log specifically
	 * @param key    The key to store the value in
	 * @param value  The value to set the key to
	 */
	public static void log(Player player, Object key, long value)
	{
		Map<Object, Long> valueMap = keepingMap.get(player);
		if (valueMap == null)
		{
			valueMap = new HashMap<Object, Long>();
			keepingMap.put(player, valueMap);
		}
		valueMap.put(key, value);
	}

	/**
	 * Increments a value in a key specific to the specified {@code Player}.
	 * 
	 * @param player The {@code Player} to change specifically
	 * @param key    The key to change
	 * @param delta  The amount to change the value by
	 */
	public static long incrementValue(Player player, Object key, long delta)
	{
		Map<Object, Long> valueMap = keepingMap.get(player);
		if (valueMap == null)
		{
			valueMap = new HashMap<Object, Long>();
			keepingMap.put(player, valueMap);
		}
		
		Long value = valueMap.get(key);
		long newValue = (value == null ? 0L : value) + delta;
		valueMap.put(key, newValue);
		return newValue;
	}
	

	/**
	 * Returns the last value that has been logged into the key specific to the
	 * {@code Player}. If no keys had been logged prior to this operation, this will
	 * return 0.
	 * 
	 * @param player The {@code Player} to check
	 * @param key    The key to check
	 * @return The last value logged into the key
	 */
	public static long getLastLog(Player player, Object key)
	{
		Map<Object, Long> valueMap = keepingMap.get(player);
		if (valueMap == null)
			return 0;

		Long value = valueMap.get(key);
		return value == null ? 0 : value;
	}

}
