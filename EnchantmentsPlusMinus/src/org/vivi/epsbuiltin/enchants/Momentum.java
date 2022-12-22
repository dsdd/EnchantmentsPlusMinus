package org.vivi.epsbuiltin.enchants;

import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.Priority;
import org.vivi.eps.api.EnchantHandler;

public class Momentum extends EnchantHandler
{
	@Override
	public Enchantment getEnchant()
	{
		return CustomEnchants.MOMENTUM;
	}

	@Override
	public Priority getPriority()
	{
		return Priority.NORMAL;
	}

	@Override
	public void entityDamage(EnchantAction.EntityDamage event)
	{
		event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
				CustomEnchants.momentumConfig.getAutofilledInt(event.getEnchantLevel(), "duration-seconds") * 20,
				event.getEnchantLevel() - 1));
		event.getEntity().getWorld().spawnParticle(Particle.CLOUD, event.getEntity().getLocation(), 1);
	}

}
