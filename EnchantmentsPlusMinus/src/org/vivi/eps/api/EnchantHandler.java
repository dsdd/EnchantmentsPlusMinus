package org.vivi.eps.api;

import org.bukkit.enchantments.Enchantment;

public abstract class EnchantHandler implements Comparable<EnchantHandler>
{
	public abstract EnchantAction.Priority getPriority();
	
	public int compareTo(EnchantHandler enchantHandler)
	{
		return enchantHandler.getPriority().getWeight() - getPriority().getWeight();
	}
	
	public abstract Enchantment getEnchant();

	public void equipItem(EnchantAction.EquipItem event)
	{

	}
	
	public void unequipItem(EnchantAction.EquipItem event)
	{

	}

	public void rightClick(EnchantAction.RightClick event)
	{

	}

	public void blockBreak(EnchantAction.BlockBreak event)
	{

	}

}
