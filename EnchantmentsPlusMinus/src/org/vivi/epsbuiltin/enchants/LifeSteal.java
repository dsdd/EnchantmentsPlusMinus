package org.vivi.epsbuiltin.enchants;

import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.Priority;
import org.vivi.eps.api.EnchantHandler;

public class LifeSteal extends EnchantHandler
{
	@Override
	public Enchantment getEnchant()
	{
		return CustomEnchants.LIFESTEAL;
	}

	@Override
	public Priority getPriority()
	{
		return Priority.NORMAL;
	}

	@Override
	public void entityDamage(EnchantAction.EntityDamage event)
	{
		Player player = event.getPlayer();
		double hp = player.getHealth()
				+ CustomEnchants.lifeStealConfig.getAutofilledDouble(event.getEnchantLevel(), "hearts")
						* (event.getDamage() / 10);
		if (hp > 20)
			hp = 20.0;
		player.setHealth(hp);
		event.getEntity().getWorld().spawnParticle(Particle.HEART, event.getEntity().getLocation(), 1);
	}

}
