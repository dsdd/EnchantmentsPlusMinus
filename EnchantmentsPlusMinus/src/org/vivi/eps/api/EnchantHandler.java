package org.vivi.eps.api;

import org.bukkit.enchantments.Enchantment;

public abstract class EnchantHandler implements Comparable<EnchantHandler>
{
	public abstract EnchantAction.Priority getPriority();
	
	@Override
	public int compareTo(EnchantHandler enchantHandler)
	{
		return enchantHandler.getPriority().getWeight() - getPriority().getWeight();
	}
	
	/**
	 * Enchant being handled
	 * 
	 * @return Handled {@link Enchantment}
	 */
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

	public void entityDamage(EnchantAction.EntityDamage event)
	{
		
	}
	
	public void armorEffect(EnchantAction.ArmorEffect event)
	{
		
	}
}
