package org.vivi.epsbuiltin.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.Priority;
import org.vivi.sekai.PlayerKeeper.PlayerStopwatch;
import org.vivi.eps.api.EnchantHandler;

public class Melting extends EnchantHandler
{
	@Override
	public Enchantment getEnchant()
	{
		return CustomEnchants.MELTING;
	}

	@Override
	public Priority getPriority()
	{
		return Priority.NORMAL;
	}

	@Override
	public void entityDamage(EnchantAction.EntityDamage event)
	{

		if (event.getEntity() instanceof LivingEntity)
		{
			int enchlvl = event.getEnchantLevel();
			LivingEntity entity = (LivingEntity) event.getEntity();
			entity.setFireTicks(
					entity.getFireTicks() + CustomEnchants.meltingConfig.getAutofilledInt(enchlvl, "fire-ticks"));
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
					CustomEnchants.meltingConfig.getAutofilledInt(enchlvl, "slowness-ticks"),
					CustomEnchants.meltingConfig.getAutofilledInt(enchlvl, "slowness-level") - 1));
			if (PlayerStopwatch.incrementValue(event.getPlayer(), CustomEnchants.MELTING,
					1) >= CustomEnchants.meltingConfig.getAutofilledInt(enchlvl, "freeze-hits"))
			{
				PlayerStopwatch.log(event.getPlayer(), CustomEnchants.MELTING, 0);
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
						CustomEnchants.meltingConfig.getAutofilledInt(enchlvl, "freeze-ticks"), 20));
			}
		}
	}

}
