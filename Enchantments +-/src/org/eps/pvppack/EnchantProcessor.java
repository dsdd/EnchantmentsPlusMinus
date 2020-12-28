package org.eps.pvppack;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.whyisthisnecessary.eps.EnchantHandler;
import org.whyisthisnecessary.eps.EnchantMetaWriter;
import org.whyisthisnecessary.eps.Main;

public class EnchantProcessor extends EnchantHandler implements Listener {

	public EnchantProcessor(Main plugin) {
		super(plugin);
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
		Damageable dmg = (Damageable) meta;
		ItemStack a = d.getInventory().getItemInMainHand();
		DustOptions dust = new DustOptions(Color.RED, 5);
		if (item == null || meta == null) { return; }
		ItemMeta meta1 = EnchantMetaWriter.getWrittenEnchantLore(d.getInventory().getItemInMainHand());
    	d.getInventory().getItemInMainHand().setItemMeta(meta1);
		if (meta.hasEnchant(CustomEnchants.JAGGED))
		{
			Double dtp = getValue(CustomEnchants.JAGGED, a.getEnchantmentLevel(CustomEnchants.JAGGED), "durabilitythresholdpercent");
			Short maxdur = item.getType().getMaxDurability();
			Integer durtaken = dmg.getDamage();
			Double dp =  ((double)maxdur-(double)durtaken) / ((double)maxdur) * 100;
			if (dp < dtp) {
		    e.setDamage(e.getDamage() + meta.getEnchantLevel(CustomEnchants.JAGGED));
		    world.spawnParticle(Particle.REDSTONE, e.getEntity().getLocation(), 1, dust);
			}
		}
		
		if (meta.hasEnchant(CustomEnchants.LIFESTEAL))
		{
			double hp = d.getHealth() + (getValue(CustomEnchants.LIFESTEAL, a.getEnchantmentLevel(CustomEnchants.LIFESTEAL), "hearts") * (e.getDamage() / 10));
			if (hp > 20) hp = 20.0;
			d.setHealth(hp);
			world.spawnParticle(Particle.HEART, e.getEntity().getLocation(), 1);
		}
		
		if (meta.hasEnchant(CustomEnchants.MOMENTUM))
		{
			double duration = getValue(CustomEnchants.MOMENTUM, a.getEnchantmentLevel(CustomEnchants.MOMENTUM), "duration")*20;
			int durationint = (int) duration;
			d.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationint, meta.getEnchantLevel(CustomEnchants.MOMENTUM)-1));
			world.spawnParticle(Particle.FLASH, e.getEntity().getLocation(), 1);
		}
		
		ItemStack[] armor = {d.getInventory().getHelmet(), d.getInventory().getChestplate(), d.getInventory().getLeggings(), d.getInventory().getBoots()};
		for (ItemStack piece : armor)
		{
			if (piece != null)
			{
			if (piece.getItemMeta().hasEnchant(CustomEnchants.INSATIABLE))
			{
				double damage = getValue(CustomEnchants.INSATIABLE, piece.getEnchantmentLevel(CustomEnchants.INSATIABLE), "extradamage");
				e.setDamage(e.getDamage() + (damage - (damage*(d.getHealth()/20))));
				world.spawnParticle(Particle.REDSTONE, e.getEntity().getLocation(), 1, dust);
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
				double duration = getValue(CustomEnchants.RETALIATE, item.getEnchantmentLevel(CustomEnchants.RETALIATE), "duration")*20;
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
					double duration = getValue(CustomEnchants.POISONOUS, piece.getEnchantmentLevel(CustomEnchants.POISONOUS), "ticks");
					PotionEffect pe = (new PotionEffect(PotionEffectType.POISON, (int) duration, piece.getEnchantmentLevel(CustomEnchants.POISONOUS)-1));
					pe.apply(en);
				}
				if (piece.getItemMeta().hasEnchant(CustomEnchants.VOLCANIC))
				{
					double rgefsa = getValue(CustomEnchants.VOLCANIC, piece.getEnchantmentLevel(CustomEnchants.VOLCANIC), "ticks");
					Integer sjdsa = (int)rgefsa;
                    e.getDamager().setFireTicks(sjdsa);
                    world.spawnParticle(Particle.LAVA, e.getEntity().getLocation(), 1);
				}
				if (piece.getItemMeta().hasEnchant(CustomEnchants.SATURATED))
				{
					double duration = getValue(CustomEnchants.SATURATED, piece.getEnchantmentLevel(CustomEnchants.SATURATED), "ticks");
					PotionEffect pe = new PotionEffect(PotionEffectType.SATURATION, (int) duration, piece.getEnchantmentLevel(CustomEnchants.SATURATED)-1);
					pe.apply(p);
				}
				}
			}
		}
	}
}
