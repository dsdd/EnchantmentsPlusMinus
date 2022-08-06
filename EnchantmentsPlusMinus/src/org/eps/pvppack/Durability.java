package org.eps.pvppack;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.legacy.LegacyUtil;

@SuppressWarnings("deprecation")
public class Durability {
	
	private ItemStack item = null;
	private short maxdurability = 0;
	private short durability = 0;

	public Durability(ItemStack item)
	{
		this.item = item;
		if (item == null)
			return;
		if (item.getType() == null)
			return;
		if (item.getItemMeta() == null)
			return;
		maxdurability = item.getType().getMaxDurability();
		if (LegacyUtil.isLegacy())
			durability = item.getDurability();
		else
		{
			if (!(item.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable))
			{
				durability = maxdurability;
				return;
			}
			int i = maxdurability-((org.bukkit.inventory.meta.Damageable)item.getItemMeta()).getDamage();
			durability = (short)i;
		}
	}
	
	@Deprecated
	public Integer getDamage()
	{
		return maxdurability-durability;
	}
	
	public int getDurability()
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
	
	public void setDurability(int durability)
	{
		if (item == null)
			return;
		if (LegacyUtil.isLegacy())
			item.setDurability((short)durability);
		else
		{
			org.bukkit.inventory.meta.Damageable dmg1 = ((org.bukkit.inventory.meta.Damageable)item.getItemMeta());
			dmg1.setDamage(maxdurability-durability);
			item.setItemMeta((ItemMeta) dmg1);
		}
	}
	
	public void incrementDurability(int durability)
	{
		setDurability(getDurability()+durability);
	}
	
	public int getMaxDurability()
	{
		return maxdurability;
	}
}
