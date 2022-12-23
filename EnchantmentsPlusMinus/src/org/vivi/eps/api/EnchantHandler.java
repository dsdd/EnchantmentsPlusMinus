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

	public void equipItem(EnchantAction.EquipItem action)
	{

	}
	
	public void unequipItem(EnchantAction.EquipItem action)
	{

	}

	public void rightClick(EnchantAction.RightClick action)
	{

	}

	public void blockBreak(EnchantAction.BlockBreak action)
	{

	}

	public void entityDamage(EnchantAction.EntityDamage action)
	{
		
	}
	
	public void entityKill(EnchantAction.EntityKill action)
	{
		
	}
	
	public void armorEffect(EnchantAction.ArmorEffect armorEffect)
	{
		
	}
}
