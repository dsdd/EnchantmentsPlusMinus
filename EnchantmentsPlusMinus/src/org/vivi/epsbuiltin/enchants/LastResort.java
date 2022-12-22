package org.vivi.epsbuiltin.enchants;

import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.Priority;
import org.vivi.eps.api.EnchantHandler;

public class LastResort extends EnchantHandler
{
	@Override
	public Enchantment getEnchant()
	{
		return CustomEnchants.LAST_RESORT;
	}

	@Override
	public Priority getPriority()
	{
		return Priority.NORMAL;
	}

	@Override
	public void entityDamage(EnchantAction.EntityDamage event)
	{
		if (event.getPlayer().getHealth() <= CustomEnchants.lastResortConfig
				.getAutofilledDouble(event.getEnchantLevel(), "healththreshold"))
			event.setDamage(event.getDamage() * 3);
	}

}
