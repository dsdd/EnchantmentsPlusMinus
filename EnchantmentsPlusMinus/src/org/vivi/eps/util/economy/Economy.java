package org.vivi.eps.util.economy;

import java.util.UUID;

import org.bukkit.entity.Player;

public interface Economy {
	
	/** Gets the balance linked to the specified UUID.
	 * 
	 * @param playerUUID The player UUID to get from
	 * @return Returns the balance of the linked player.
	 */
	public double getBalance(UUID playerUUID);
	
	/** Changes the balance linked to the specified UUID by the specified amount.
	 * 
	 * @param playerUUID The player UUID to get from
	 * @param amount The amount changed.
	 * @return Returns the amount the player has in the end.
	 */
	public double changeBalance(UUID playerUUID, double amount);
	
	/** Sets the balance linked to the specified UUID to the specified amount.
	 * 
	 * @param playerUUID The player UUID to get from
	 * @param value The amount to be set to.
	 * @return Returns the amount the player has in the end.
	 */
	public double setBalance(UUID playerUUID, double value);
	
	/** Gets the balance of the specified player.
	 * 
	 * @param player The player to get from
	 * @return Returns the balance of the player.
	 */
	public default double getBalance(Player player)
	{
		return getBalance(player.getUniqueId());
	}
	
	/** Changes the balance of the specified player by the specified amount.
	 * 
	 * @param player The player to get from
	 * @param amount The amount changed.
	 * @return Returns the amount the player has in the end.
	 */
	public default double changeBalance(Player player, double amount)
	{
		return changeBalance(player.getUniqueId(), amount);
	}
	
	/** Sets the balance of the specified player to the specified amount.
	 * 
	 * @param player The player to get from
	 * @param value The amount to be set to.
	 * @return Returns the amount the player has in the end.
	 */
	public default double setBalance(Player player, double value)
	{
		return setBalance(player.getUniqueId(), value);
	}
}
