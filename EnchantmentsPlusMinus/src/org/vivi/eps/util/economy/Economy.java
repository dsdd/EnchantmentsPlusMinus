package org.vivi.eps.util.economy;

import org.bukkit.entity.Player;

public interface Economy {

	/**Changes the specified player's balance.
	 * 
	 * @param playername The player affected
	 * @param amount The amount changed. Can be negative.
	 * @return Returns the amount the player has after.
	 */
	public double changeBalance(String playername, double amount);
	
	/**Changes the specified player's balance.
	 * 
	 * @param player The player affected
	 * @param amount The amount changed. Can be negative.
	 * @return Returns the amount the player has after.
	 */
	public double changeBalance(Player player, double amount);
	
	/**Sets the specified player's balance.
	 * 
	 * @param playername The player affected
	 * @param value The amount to be set to.
	 * @return Returns the balance the player has after.
	 */
	public double setBalance(String playername, double value);
	
	/**Sets the specified player's balance.
	 * 
	 * @param player The player affected
	 * @param value The amount to be set to.
	 * @return Returns the balance the player has after.
	 */
	public double setBalance(Player player, double value);
	
	/**Returns the balance of the specified player.
	 * 
	 * @param player The player to get from
	 * @return Returns the balance of the specified player.
	 */
	public double getBalance(Player player);
	
	/**Returns the balance of the specified player.
	 * 
	 * @param playername The player to get from
	 * @return Returns the balance of the specified player.
	 */
	public double getBalance(String playername);
}
