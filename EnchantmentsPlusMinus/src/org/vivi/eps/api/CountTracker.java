package org.vivi.eps.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

/** To keep count for each individual player.
 * Used to make enchant abilities.
 *
 */
public class CountTracker {
	
	private Map<Player, Integer> counts = new HashMap<Player, Integer>();
	
	/** Increases the count of the player.
	 * 
	 * @param p The player to increase count of
	 * @return The new count of the player
	 */
	public int increase(Player p)
	{
		Integer t = counts.get(p);
		int current = t == null ? 0 : t;
		counts.put(p, current+1);
		return current + 1;
	}
	
	/** Increases the count of the player.
	 * 
	 * @param p The player to increase count of
	 * @param increment How much to increase by
	 * @return The new count of the player
	 */
	public int increase(Player p, int increment)
	{
		Integer t = counts.get(p);
		int current = t == null ? 0 : t;
		counts.put(p, current+increment);
		return current + increment;
	}
	
	/** Gets the count of the player.
	 * 
	 * @param p The player to get count of
	 * @return The count of the player
	 */
	public int get(Player p)
	{
		Integer t = counts.get(p);
		return t == null ? 0 : t;
	}
	
	/** Resets the count of the player.
	 * 
	 * @param p The player to reset count of
	 */
	public void reset(Player p)
	{
		counts.put(p, 0);
	}
}
