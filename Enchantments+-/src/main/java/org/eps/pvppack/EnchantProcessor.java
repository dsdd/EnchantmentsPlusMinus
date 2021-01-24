package org.eps.pvppack;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.whyisthisnecessary.eps.visual.EnchantMetaWriter;
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.legacy.LegacyUtil;

public class EnchantProcessor implements Listener {

	public static Random rand = new Random();
	
	public EnchantProcessor(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player)
		{
		
		Player d = (Player) e.getDamager();
		World world = d.getWorld();
		ItemStack item = d.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		Durability dmg = new Durability(item);
		if (item == null || meta == null) { return; }
		ItemMeta meta1 = EnchantMetaWriter.getWrittenMeta(d.getInventory().getItemInMainHand());
    	d.getInventory().getItemInMainHand().setItemMeta(meta1);
		if (meta.hasEnchant(CustomEnchants.JAGGED))
		{
			Double dtp = ConfigUtil.getAutofilledDouble(CustomEnchants.JAGGED, meta.getEnchantLevel(CustomEnchants.JAGGED), "durabilitythresholdpercent");
			Short maxdur = item.getType().getMaxDurability();
			Integer durtaken = dmg.getDamage();
			Double dp =  ((double)maxdur-(double)durtaken) / ((double)maxdur) * 100;
			if (dp < dtp) {
		    e.setDamage(e.getDamage() + meta.getEnchantLevel(CustomEnchants.JAGGED));
		    if (!LegacyUtil.isLegacy())
		    	world.spawnParticle(Particle.REDSTONE, e.getEntity().getLocation(), 1, new org.bukkit.Particle.DustOptions(Color.RED, 5));
		    else
		    	world.spawnParticle(Particle.REDSTONE, e.getEntity().getLocation(), 1);
			}
		}
		
		if (meta.hasEnchant(CustomEnchants.LIFESTEAL))
		{
			double hp = d.getHealth() + (ConfigUtil.getAutofilledDouble(CustomEnchants.LIFESTEAL, meta.getEnchantLevel(CustomEnchants.LIFESTEAL), "hearts") * (e.getDamage() / 10));
			if (hp > 20) hp = 20.0;
			d.setHealth(hp);
			world.spawnParticle(Particle.HEART, e.getEntity().getLocation(), 1);
		}
		
		if (meta.hasEnchant(CustomEnchants.MOMENTUM))
		{
			double duration = ConfigUtil.getAutofilledDouble(CustomEnchants.MOMENTUM, meta.getEnchantLevel(CustomEnchants.MOMENTUM), "duration")*20;
			int durationint = (int) duration;
			d.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationint, meta.getEnchantLevel(CustomEnchants.MOMENTUM)-1));
			world.spawnParticle(Particle.CLOUD, e.getEntity().getLocation(), 1);
		}
		
		if (meta.hasEnchant(CustomEnchants.LASTRESORT))
		{
			double healththreshold = ConfigUtil.getAutofilledDouble(CustomEnchants.LASTRESORT, meta.getEnchantLevel(CustomEnchants.LASTRESORT), "healththreshold");
			if (((LivingEntity)e.getDamager()).getHealth() <= healththreshold)
				e.setDamage(e.getDamage()*3);
		}
		
		ItemStack[] armor = {d.getInventory().getHelmet(), d.getInventory().getChestplate(), d.getInventory().getLeggings(), d.getInventory().getBoots()};
		for (ItemStack piece : armor)
		{
			if (piece != null)
			{
			if (piece.getItemMeta().hasEnchant(CustomEnchants.INSATIABLE))
			{
				double damage = ConfigUtil.getAutofilledDouble(CustomEnchants.INSATIABLE, piece.getEnchantmentLevel(CustomEnchants.INSATIABLE), "extradamage");
				e.setDamage(e.getDamage() + (damage - (damage*(d.getHealth()/20))));
				if (!LegacyUtil.isLegacy())
					world.spawnParticle(Particle.REDSTONE, e.getEntity().getLocation(), 1, new org.bukkit.Particle.DustOptions(Color.RED, 5));
				else
			    	world.spawnParticle(Particle.REDSTONE, e.getEntity().getLocation(), 1);
			}
			}
		}
		
		}
		
		if (e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();
			World world = p.getWorld();
			ItemStack item = p.getInventory().getItemInMainHand();
			ItemMeta meta = item.getItemMeta();
			if (item == null || meta == null) { return; }
			if (meta.hasEnchant(CustomEnchants.RETALIATE))
			{
				double duration = ConfigUtil.getAutofilledDouble(CustomEnchants.RETALIATE, meta.getEnchantLevel(CustomEnchants.RETALIATE), "duration")*20;
				int durationint = (int) duration;
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, durationint, meta.getEnchantLevel(CustomEnchants.RETALIATE)-1));
			}
			
			ItemStack[] armor = {p.getInventory().getHelmet(), p.getInventory().getChestplate(), p.getInventory().getLeggings(), p.getInventory().getBoots()};
			for (ItemStack piece : armor)
			{
				if (piece != null)
				{
				if (piece.getItemMeta().hasEnchant(CustomEnchants.POISONOUS))
				{
					LivingEntity en = (LivingEntity) e.getDamager();
					double duration = ConfigUtil.getAutofilledDouble(CustomEnchants.POISONOUS, piece.getEnchantmentLevel(CustomEnchants.POISONOUS), "ticks");
					PotionEffect pe = (new PotionEffect(PotionEffectType.POISON, (int) duration, piece.getEnchantmentLevel(CustomEnchants.POISONOUS)-1));
					pe.apply(en);
				}
				if (piece.getItemMeta().hasEnchant(CustomEnchants.VOLCANIC))
				{
					int duration = ConfigUtil.getAutofilledDouble(CustomEnchants.VOLCANIC, piece.getEnchantmentLevel(CustomEnchants.VOLCANIC), "ticks").intValue();
                    e.getDamager().setFireTicks(duration);
                    world.spawnParticle(Particle.LAVA, e.getEntity().getLocation(), 1);
				}
				if (piece.getItemMeta().hasEnchant(CustomEnchants.SATURATED))
				{
					double duration = ConfigUtil.getAutofilledDouble(CustomEnchants.SATURATED, piece.getEnchantmentLevel(CustomEnchants.SATURATED), "ticks");
					PotionEffect pe = new PotionEffect(PotionEffectType.SATURATION, (int) duration, piece.getEnchantmentLevel(CustomEnchants.SATURATED)-1);
					pe.apply(p);
				}
				if (piece.getItemMeta().hasEnchant(CustomEnchants.STIFFEN))
				{
					double healththreshold = ConfigUtil.getAutofilledDouble(CustomEnchants.STIFFEN, piece.getEnchantmentLevel(CustomEnchants.STIFFEN), "healththreshold");
					int amplifier = ConfigUtil.getAutofilledInt(CustomEnchants.STIFFEN, piece.getEnchantmentLevel(CustomEnchants.STIFFEN), "amplifier");
					if (((LivingEntity)e.getEntity()).getHealth() <= healththreshold)
                    	((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, amplifier-1));
				}
				}
			}
		}
	}
	
	@EventHandler
	public void onKill(EntityDeathEvent e)
	{
		if (e.getEntity().getKiller() == null) return;
		ItemStack item = e.getEntity().getKiller().getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if (item.containsEnchantment(CustomEnchants.BEHEADING))
		{
			Integer lvl = meta.getEnchantLevel(CustomEnchants.BEHEADING);
			double chance = ConfigUtil.getAutofilledDouble(CustomEnchants.BEHEADING, lvl, "chance");
			if (getNext() <= chance)
			{
				ItemStack head = getHead(e.getEntity());
				if (head != null)
				e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), head);
			}
		}
	}

	@EventHandler
	public void onPlayerKill(PlayerDeathEvent e)
	{
		if (e.getEntity().getKiller() == null) return;
		ItemStack item = e.getEntity().getKiller().getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if (item.containsEnchantment(CustomEnchants.BEHEADING))
		{
			Integer lvl = meta.getEnchantLevel(CustomEnchants.BEHEADING);
			double chance = ConfigUtil.getAutofilledDouble(CustomEnchants.BEHEADING, lvl, "chance");
			if (getNext() <= chance)
			{
				ItemStack head = LegacyUtil.isLegacy() ? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1) : new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta skull = (SkullMeta) head.getItemMeta();
				skull.setOwningPlayer(e.getEntity());
				head.setItemMeta(skull);
				e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), head);
			}
		}
	}
	
	public static double getNext()
	{
		return rand.nextDouble()*100;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getHead(LivingEntity e)
	{
		switch (e.getType())
		{
		case SKELETON:
			if (!LegacyUtil.isLegacy())
				return new ItemStack(Material.SKELETON_SKULL, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 0);
		case WITHER_SKELETON:
			if (!LegacyUtil.isLegacy())
				return new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 1);
		case ZOMBIE:
			if (!LegacyUtil.isLegacy())
				return new ItemStack(Material.ZOMBIE_HEAD, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 2);
		case CREEPER:
			if (!LegacyUtil.isLegacy())
				return new ItemStack(Material.CREEPER_HEAD, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 4);
		case ENDER_DRAGON:
			if (!LegacyUtil.isLegacy())
				return new ItemStack(Material.DRAGON_HEAD, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 5);
		default:
			return null;
		}
	}
}
