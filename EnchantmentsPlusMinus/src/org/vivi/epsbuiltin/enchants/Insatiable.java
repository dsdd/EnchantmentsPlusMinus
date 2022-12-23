package org.vivi.epsbuiltin.enchants;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.Priority;
import org.vivi.sekai.Sekai;
import org.vivi.eps.api.EnchantHandler;

public class Insatiable extends EnchantHandler
{
	@Override
	public Enchantment getEnchant()
	{
		return CustomEnchants.INSATIABLE;
	}

	@Override
	public Priority getPriority()
	{
		return Priority.NORMAL;
	}

	@Override
	public void armorEffect(EnchantAction.ArmorEffect event)
	{
		EnchantAction action = event.getMainAction();
		if (action instanceof EnchantAction.EntityDamage)
		{
			EnchantAction.EntityDamage entityDamage = (EnchantAction.EntityDamage) action;
			double damage = CustomEnchants.insatiableConfig.getAutofilledDouble(event.getEnchantLevel(), "extradamage");
			entityDamage.setDamage(
					entityDamage.getDamage() + (damage - (damage * (entityDamage.getPlayer().getHealth() * 0.05))));
			if (Sekai.getMCVersion() > 12)
				entityDamage.getEntity().getWorld().spawnParticle(Particle.REDSTONE,
						entityDamage.getEntity().getLocation(), 1, new org.bukkit.Particle.DustOptions(Color.RED, 5));
			else
				entityDamage.getEntity().getWorld().spawnParticle(Particle.REDSTONE,
						entityDamage.getEntity().getLocation(), 1);
		}

	}

}
