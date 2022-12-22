package org.vivi.eps.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.vivi.eps.EPS;

public abstract class EnchantAction
{
	private Enchantment enchant;

	/**
	 * Gets the {@link Event} that has been intercepted by the current
	 * {@link EnchantAction}
	 * 
	 * @return Requested {@code Event}
	 */
	public abstract Event getEvent();

	/**
	 * Gets the {@code Player} holding the tool that fired the current
	 * {@link EnchantAction}.
	 */
	public abstract Player getPlayer();

	/**
	 * Gets the {@link ItemStack} that contains the enchant that is running the
	 * current {@link EnchantAction}
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

	private static void logAction(EnchantAction action)
	{
		EPS.logger.log(Level.FINER, new StringBuilder(action.getPlayer().getName()).append(" fired ")
				.append(action.getClass().getSimpleName()).toString());
	}

	public static class EquipItem extends EnchantAction
	{
		private final PlayerItemHeldEvent event;

		public EquipItem(PlayerItemHeldEvent event)
		{
			this.event = event;
			logAction(this);
		}

		@Override
		public Event getEvent()
		{
			return event;
		}

		@Override
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
			logAction(this);
		}

		@Override
		public Event getEvent()
		{
			return event;
		}

		@Override
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
			logAction(this);
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

		@Override
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

	public static class EntityDamage extends EnchantAction
	{
		private final EntityDamageByEntityEvent event;

		public EntityDamage(EntityDamageByEntityEvent event)
		{
			this.event = event;
			logAction(this);
		}

		@Override
		public Event getEvent()
		{
			return event;
		}

		@Override
		public Player getPlayer()
		{
			return (Player) event.getDamager();
		}

		public Entity getEntity()
		{
			return event.getEntity();
		}

		public void setDamage(double damage)
		{
			event.setDamage(damage);
		}

		public double getDamage()
		{
			return event.getDamage();
		}

		@Override
		public ItemStack getItemStack()
		{
			return getPlayer().getInventory().getItemInMainHand();
		}
	}
	
	public static class ArmorEffect extends EnchantAction
	{
		private final EnchantAction action;
		private ItemStack itemStack = null;
		
		public ArmorEffect(EnchantAction action)
		{
			this.action = action;
			logAction(this);
		}

		@Override
		public Event getEvent()
		{
			return action.getEvent();
		}

		@Override
		public Player getPlayer()
		{
			return action.getPlayer();
		}

		@Override
		public ItemStack getItemStack()
		{
			return itemStack;
		}
		
		public EnchantAction getMainAction()
		{
			return action;
		}
		
		public void setItemStack(ItemStack itemStack)
		{
			this.itemStack = itemStack;
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