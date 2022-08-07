package org.vivi.eps.util.economy;

import org.bukkit.entity.Player;

public interface Economy {

	/**Changes the specified player's balance.
	 * 
	 * @param playername The player affected
	 * @param amount The amount changed. Can be negative.
	 * @return Returns the amount the player has after.
	 */
	public Integer changeBalance(String playername, Integer amount);
	
	/**Changes the specified player's balance.
	 * 
	 * @param player The player affected
	 * @param amount The amount changed. Can be negative.
	 * @return Returns the amount the player has after.
	 */
	public Integer changeBalance(Player player, Integer amount);
	
	/**Sets the specified player's balance.
	 * 
	 * @param playername The player affected
	 * @param value The amount to be set to.
	 * @return Returns the balance the player has after.
	 */
	public Integer setBalance(String playername, Integer value);
	
	/**Sets the specified player's balance.
	 * 
	 * @param player The player affected
	 * @param value The amount to be set to.
	 * @return Returns the balance the player has after.
	 */
	public Integer setBalance(Player player, Integer value);
	
	/**Returns the balance of the specified player.
	 * 
	 * @param player The player to get from
	 * @return Returns the balance of the specified player.
	 */
	public Integer getBalance(Player player);
	
	/**Returns the balance of the specified player.
	 * 
	 * @param playername The player to get from
	 * @return Returns the balance of the specified player.
	 */
	public Integer getBalance(String playername);
}
