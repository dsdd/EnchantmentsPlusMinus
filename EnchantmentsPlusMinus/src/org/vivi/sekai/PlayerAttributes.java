package org.vivi.sekai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

public class PlayerAttributes
{
	public static final Map<Player, Set<Object>> attributesMap = new HashMap<Player, Set<Object>>();
	
	public static void addAttribute(Player player, Object object)
	{
		Set<Object> attributes = attributesMap.get(player);
		if (attributes == null)
		{
			attributes = new HashSet<Object>();
			attributesMap.put(player, attributes);
		}
		attributes.add(object);
	}
	
	public static void removeAttribute(Player player, Object object)
	{
		Set<Object> attributes = attributesMap.get(player);
		if (attributes != null)
			attributes.remove(object);
	}
	
	public static boolean hasAttribute(Player player, Object object)
	{
		Set<Object> attributes = attributesMap.get(player);
		if (attributes == null)
			return false;
		return attributes.contains(object);
	}
	
	public static Set<Object> getAttributes(Player player)
	{
		Set<Object> attributes = attributesMap.get(player);
		if (attributes == null)
		{
			attributes = new HashSet<Object>();
			attributesMap.put(player, attributes);
		}
		return attributes;
	}
}
