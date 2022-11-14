package org.vivi.sekai;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerKeeper<T>
{
	private Map<Player, Map<Object, T>> keepingMap = new HashMap<Player, Map<Object, T>>();

	public PlayerKeeper()
	{
		for (Player player : Bukkit.getOnlinePlayers())
			keepingMap.put(player, new HashMap<Object, T>());
	}

	/**
	 * Keeps a value into a key specific to the specified {@code Player}.
	 * 
	 * @param player The {@code Player} to log specifically
	 * @param key    The key to store the value in
	 * @param value  The value to set the key to
	 */
	public void log(Player player, Object key, T value)
	{
		Map<Object, T> valueMap = keepingMap.get(player);
		if (valueMap == null)
		{
			valueMap = new HashMap<Object, T>();
			keepingMap.put(player, valueMap);
		}
		valueMap.put(key, value);
	}

	/**
	 * Returns the last value that has been logged into the key specific to the
	 * {@code Player}. If no keys had been logged prior to this operation, this will
	 * return null.
	 * 
	 * @param player The {@code Player} to check
	 * @param key    The key to check
	 * @return The last value logged into the key
	 */
	public T getLastLog(Player player, Object key)
	{
		Map<Object, T> valueMap = keepingMap.get(player);
		if (valueMap == null)
			return null;

		return valueMap.get(key);
	}

	public static class PlayerStopwatch
	{
		public static final PlayerKeeper<Long> playerKeeper = new PlayerKeeper<Long>();

		/**
		 * Keeps a value into a key specific to the specified {@code Player}.
		 * 
		 * @param player The {@code Player} to log specifically
		 * @param key    The key to store the value in
		 * @param value  The value to set the key to
		 */
		public static void log(Player player, Object object, long value)
		{
			playerKeeper.log(player, object, value);
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
			Long lastLog = playerKeeper.getLastLog(player, key);
			return lastLog == null ? 0L : lastLog;
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
			Map<Object, Long> valueMap = playerKeeper.keepingMap.get(player);
			if (valueMap == null)
			{
				valueMap = new HashMap<Object, Long>();
				playerKeeper.keepingMap.put(player, valueMap);
			}

			Long value = valueMap.get(key);
			long newValue = (value == null ? 0L : value) + delta;
			valueMap.put(key, newValue);
			return newValue;
		}
	}
}
