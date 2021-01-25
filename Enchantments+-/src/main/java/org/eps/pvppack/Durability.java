package org.eps.pvppack;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.whyisthisnecessary.eps.legacy.LegacyUtil;

@SuppressWarnings("deprecation")
public class Durability {
	
	private ItemStack item = null;
	private Short maxdurability = 0;
	private Short durability = 0;

	public Durability(ItemStack item)
	{
		this.item = item;
		if (item == null)
			return;
		if (item.getType() == null)
			return;
		if (item.getItemMeta() == null)
			return;
		if (LegacyUtil.isLegacy())
		durability = item.getDurability();
		else
		{
			if (!(item instanceof org.bukkit.inventory.meta.Damageable))
				return;
			int i = item.getType().getMaxDurability()-((org.bukkit.inventory.meta.Damageable)item.getItemMeta()).getDamage();
			durability = (short)i;
		}
		maxdurability = item.getType().getMaxDurability();
	}
	
	@Deprecated
	public Integer getDamage()
	{
		return maxdurability-durability;
	}
	
	public Integer getDurability()
	{
		return Short.toUnsignedInt(durability);
	}
	
	@Deprecated
	public void setDamage(Integer dmg)
	{
		if (item == null)
			return;
		if (LegacyUtil.isLegacy())
			item.setDurability((short) (maxdurability-dmg));
		else
		{
			org.bukkit.inventory.meta.Damageable dmg1 = ((org.bukkit.inventory.meta.Damageable)item.getItemMeta());
			dmg1.setDamage(dmg);
			item.setItemMeta((ItemMeta) dmg1);
		}
	}
	
	public void setDurability(Integer durability)
	{
		if (item == null)
			return;
		if (LegacyUtil.isLegacy())
			item.setDurability(durability.shortValue());
		else
		{
			org.bukkit.inventory.meta.Damageable dmg1 = ((org.bukkit.inventory.meta.Damageable)item.getItemMeta());
			dmg1.setDamage(maxdurability-durability);
			item.setItemMeta((ItemMeta) dmg1);
		}
	}
}
