package org.vivi.epsbuiltin.enchants;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.Priority;
import org.vivi.sekai.Sekai;
import org.vivi.eps.api.EnchantHandler;

public class Jagged extends EnchantHandler
{
	@Override
	public Enchantment getEnchant()
	{
		return CustomEnchants.JAGGED;
	}

	@Override
	public Priority getPriority()
	{
		return Priority.NORMAL;
	}

	@Override
	public void entityDamage(EnchantAction.EntityDamage event)
	{
		Durability durability = new Durability(event.getItemStack());
		int enchlvl = event.getEnchantLevel();
		double damageThresholdP = CustomEnchants.jaggedConfig.getAutofilledDouble(enchlvl,
				"durabilitythresholdpercent");
		double damageP = (double) ((durability.getDurability() * 100) / durability.getMaxDurability());
		if (damageP < damageThresholdP)
		{
			event.setDamage(event.getDamage() + enchlvl);
			if (Sekai.getMCVersion() > 12)
				event.getEntity().getWorld().spawnParticle(Particle.REDSTONE, event.getEntity().getLocation(), 1,
						new Particle.DustOptions(Color.RED, 5));
			else
				event.getEntity().getWorld().spawnParticle(Particle.REDSTONE, event.getEntity().getLocation(), 1);
		}
	}

}
