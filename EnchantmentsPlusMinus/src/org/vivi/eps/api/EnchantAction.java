package org.vivi.eps.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public abstract class EnchantAction
{
	private Enchantment enchant;

	/**
	 * Gets the {@code Event} that has been intercepted by the current
	 * {@code EnchantAction}
	 * 
	 * @return Requested {@code Event}
	 */
	public abstract Event getEvent();

	/**
	 * Gets the {@code ItemStack} that contains the enchant that is running the
	 * current {@code EnchantAction}
	 * 
	 * @return Requested {@code ItemStack}
	 */
	public abstract ItemStack getItemStack();

	public Enchantment getEnchant()
	{
		return enchant;
	}

	public void setCurrentEnchant(Enchantment enchant)
	{
		this.enchant = enchant;
	}

	public int getEnchantLevel()
	{
		return getItemStack().getItemMeta().getEnchantLevel(enchant);
	}

	public static class EquipItem extends EnchantAction
	{
		private final PlayerItemHeldEvent event;

		public EquipItem(PlayerItemHeldEvent event)
		{
			this.event = event;
		}

		@Override
		public Event getEvent()
		{
			return event;
		}

		public Player getPlayer()
		{
			return event.getPlayer();
		}

		public ItemStack getPreviousItemStack()
		{
			return getPlayer().getInventory().getItem(event.getPreviousSlot());
		}

		public ItemStack getNewItemStack()
		{
			return getPlayer().getInventory().getItem(event.getNewSlot());
		}

		@Override
		public ItemStack getItemStack()
		{
			return getNewItemStack();
		}
	}

	public static class RightClick extends EnchantAction
	{
		private final PlayerInteractEvent event;

		public RightClick(PlayerInteractEvent event)
		{
			this.event = event;
		}

		@Override
		public Event getEvent()
		{
			return event;
		}

		public Player getPlayer()
		{
			return event.getPlayer();
		}

		@Override
		public ItemStack getItemStack()
		{
			return getPlayer().getInventory().getItemInMainHand();
		}
	}

	public static class BlockBreak extends EnchantAction
	{
		private final BlockBreakEvent event;
		private final List<ItemStack> drops = new ArrayList<ItemStack>();

		public BlockBreak(BlockBreakEvent event, Collection<ItemStack> drops)
		{
			this.event = event;
			this.drops.addAll(drops);
		}

		public List<ItemStack> getDrops()
		{
			return drops;
		}

		@Override
		public Event getEvent()
		{
			return event;
		}

		public Player getPlayer()
		{
			return event.getPlayer();
		}

		@Override
		public ItemStack getItemStack()
		{
			return getPlayer().getItemInUse();
		}
	}

	public static enum Priority
	{
		LOWEST(300), LOW(400), NORMAL(800), HIGH(900), HIGHEST(1000), MONITOR(9999);

		private final int weight;

		private Priority(int weight)
		{
			this.weight = weight;
		}

		public int getWeight()
		{
			return weight;
		}
	}
}