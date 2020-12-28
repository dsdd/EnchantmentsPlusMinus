package org.whyisthisnecessary.eps.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TokenUtil {

	/**Changes the amount of tokens a specified player has.
	 * 
	 * @param player The player affected
	 * @param amount The amount of tokens changed. Can be negative.
	 * @return Returns the amount of tokens the player has after.
	 */
	public static Integer changeTokens(String playername, Integer amount)
	{
		File file = DataUtil.getUserDataFile(playername);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		Integer redefine = config.getInt("tokens") + amount;
		config.set("tokens", redefine);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return redefine;
	}
	
	/**Changes the amount of tokens a specified player has.
	 * 
	 * @param player The player affected
	 * @param amount The amount of tokens changed. Can be negative.
	 * @return Returns the amount of tokens the player has after.
	 */
	public static Integer changeTokens(Player player, Integer amount)
	{
		return changeTokens(player.getName(), amount);
	}
	
	/**Sets the amount of tokens a specified player has.
	 * 
	 * @param playername The player affected
	 * @param value The amount of tokens to be set to. Can be negative, but why?
	 * @return Returns the amount of tokens the player has after.
	 */
	public static Integer setTokens(String playername, Integer value)
	{
		File file = DataUtil.getUserDataFile(playername);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		Integer redefine = value;
		config.set("tokens", redefine);
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return redefine;
	}
	
	/**Sets the amount of tokens a specified player has.
	 * 
	 * @param player The player affected
	 * @param value The amount of tokens to be set to. Can be negative, but why?
	 * @return Returns the amount of tokens the player has after.
	 */
	public static Integer setTokens(Player player, Integer value)
	{
		return setTokens(player.getName(), value);
	}
	
	/**Returns the amount of tokens a specified player has.
	 * 
	 * @param player The player to get from
	 * @return Returns the amount of tokens a specified player has.
	 */
	public static Integer getTokens(Player player)
	{
		return getTokens(player.getName());
	}
	
	/**Returns the amount of tokens a specified player has.
	 * 
	 * @param playername The player to get from
	 * @return Returns the amount of tokens a specified player has.
	 */
	public static Integer getTokens(String playername)
	{
		File file = DataUtil.getUserDataFile(playername);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config.getInt("tokens");
	}
}
