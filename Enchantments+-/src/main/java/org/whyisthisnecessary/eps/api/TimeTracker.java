package org.whyisthisnecessary.eps.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

/** To track time for each individual player.
 * Used to make enchant abilities.
 *
 */
public class TimeTracker {

	private Map<Player, Long> cooldowns = new HashMap<Player, Long>();
	
	/** Gets the last time, in milliseconds, since usage.
	 * 
	 * @param p The player to get
	 * @return The last time, in milliseconds, since usage.
	 */
	public long getLastUse(Player p)
	{
		long currentTime = System.currentTimeMillis();
		Long previousUse = cooldowns.get(p);
		if (previousUse == null)
		{
			cooldowns.put(p, 0L);
			previousUse = 0L;
		}
		return currentTime-previousUse;
	}
	
	/**Sets the tracker to the current time.
	 * 
	 * @param p To set
	 */
	public void use(Player p)
	{
		cooldowns.put(p, System.currentTimeMillis());
	}
}
