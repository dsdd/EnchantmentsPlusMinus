package org.vivi.epsbuiltin.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.Priority;
import org.vivi.sekai.PlayerAttributes;
import org.vivi.eps.api.EnchantHandler;

public class Fly extends EnchantHandler
{
	@Override
	public Enchantment getEnchant()
	{
		return CustomEnchants.FLY;
	}

	@Override
	public Priority getPriority()
	{
		return Priority.NORMAL;
	}

	@Override
	public void equipItem(EnchantAction.EquipItem event)
	{
		final Player player = event.getPlayer();
		if (CustomEnchants.flyConfig.getBoolean("enabled", false))
		{
			player.setAllowFlight(true);
			player.setFlying(true);
			PlayerAttributes.addAttribute(player, CustomEnchants.FLY);
		}
	}

	@Override
	public void unequipItem(EnchantAction.EquipItem event)
	{
		final Player player = event.getPlayer();
		if (PlayerAttributes.hasAttribute(player, CustomEnchants.FLY) && event.getEnchantLevel() == 0)
		{
			player.setAllowFlight(false);
			player.setFlying(false);
			PlayerAttributes.removeAttribute(player, CustomEnchants.FLY);
		}
	}

}
