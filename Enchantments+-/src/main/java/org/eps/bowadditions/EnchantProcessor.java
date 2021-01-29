package org.eps.bowadditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.util.LangUtil;

public class EnchantProcessor implements Listener {
	
	private Map<Player, Long> enderbowCooldown = new HashMap<Player, Long>();
	private Map<Player, Integer> machineryShots = new HashMap<Player, Integer>();
	private Map<Player, Integer> tdShots = new HashMap<Player, Integer>();
	private Material cb = Material.matchMaterial("CROSSBOW");
	
	public EnchantProcessor()
	{
		LangUtil.setDefaultLangMessage("cooldown-error", "&cYou must wait %secs% more seconds to use this again!");
		LangUtil.setDefaultLangMessage("enderbow-radius-error", "&cYou cannot teleport further than %blocks% blocks!");
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e)
	{
		if (e.getEntity().isDead())
			return;
		if (!(e.getEntity() instanceof Arrow))
			return;
		
		Arrow arrow = (Arrow) e.getEntity();
		if (!(arrow.getShooter() instanceof Player))
			return;
		
		Player player = (Player) arrow.getShooter();
		ItemStack mainitem = player.getInventory().getItemInMainHand();
		ItemMeta mainmeta = mainitem.getItemMeta();
		World world = player.getWorld();
		
		Material material = mainitem.getType();
		if (!(material.equals(Material.BOW) && !(material.equals(cb))))
			return;
		
		if (mainmeta.hasEnchant(CustomEnchants.ENDERBOW))
		{
			if (player.isSneaking())
			{
				double cooldown = ConfigUtil.getAutofilledDouble(CustomEnchants.ENDERBOW, mainmeta.getEnchantLevel(CustomEnchants.ENDERBOW), "cooldown")*1000;
				long fulltime = System.currentTimeMillis();
				if (enderbowCooldown.get(player) == null)
					enderbowCooldown.put(player, (long) (System.currentTimeMillis()-cooldown));
				long time = enderbowCooldown.get(player);
				
				// Check if cooldown is met
				if (fulltime-time < cooldown)
					player.sendMessage(LangUtil.getLangMessage("cooldown-error").replaceAll("%secs%", Double.toString(Math.floor(((cooldown-(fulltime-time))/1000)*10)/10)));
				else
				{
					double radius = ConfigUtil.getAutofilledDouble(CustomEnchants.ENDERBOW, mainmeta.getEnchantLevel(CustomEnchants.ENDERBOW), "radius");
					double distance = player.getLocation().distance(arrow.getLocation());
					
					// Check if distance from player exceeds limits
					if (distance > radius)
						player.sendMessage(LangUtil.getLangMessage("enderbow-radius-error").replaceAll("%blocks%", Double.toString(radius)));
					else
					{
						enderbowCooldown.put(player, fulltime);
						player.teleport(arrow.getLocation());
						player.playEffect(EntityEffect.TELEPORT_ENDER);
					}
				}
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.MACHINERY))
		{
			int shotstoactivate = ConfigUtil.getAutofilledInt(CustomEnchants.MACHINERY, mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "shots-to-activate");
			Integer shots = machineryShots.get(player);
			shots = shots == null ? 0 : shots;
			machineryShots.put(player, shots+1);
			if (shots == shotstoactivate)
			{
				machineryShots.put(player, 0);
				int radius = ConfigUtil.getAutofilledInt(CustomEnchants.MACHINERY, mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "radius");
				int arrows = ConfigUtil.getAutofilledInt(CustomEnchants.MACHINERY, mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "arrows");
				Location loc = arrow.getLocation();
				Random random = new Random();
				for (int i=0;i<arrows;i++)
				{
					Location location = new Location(world, random.nextInt(radius*2)-radius+loc.getX(), loc.getY()+48, random.nextInt(radius*2)-radius+loc.getZ());
					world.spawnArrow(location, new Vector(0, -90, 0), 5, 0);
				}
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.THUNDERING_BLOW))
		{
			int shotstoactivate = ConfigUtil.getAutofilledInt(CustomEnchants.THUNDERING_BLOW, mainmeta.getEnchantLevel(CustomEnchants.THUNDERING_BLOW), "shots-to-activate");
			Integer shots = tdShots.get(player);
			shots = shots == null ? 0 : shots;
			tdShots.put(player, shots+1);
			if (shots == shotstoactivate)
			{
				tdShots.put(player, 0);
				Entity entity = e.getHitEntity();
				
				if (entity != null)
				world.strikeLightning(entity.getLocation());
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.ENERGIZED))
		{
			// Just a temporary name for checking
			Entity a = e.getHitEntity();
			
			if (a != null && (a instanceof LivingEntity) && a == arrow.getShooter())
			{
				LivingEntity entity = (LivingEntity) a;
				int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.ENERGIZED);
				
				int speed_amplifier = ConfigUtil.getAutofilledInt(CustomEnchants.ENERGIZED, enchlvl, "speed-amplifier");
				int speed_duration = ConfigUtil.getAutofilledInt(CustomEnchants.ENERGIZED, enchlvl, "speed-duration");
				int regeneration_amplifier = ConfigUtil.getAutofilledInt(CustomEnchants.ENERGIZED, enchlvl, "regeneration-amplifier");
				int regeneration_duration = ConfigUtil.getAutofilledInt(CustomEnchants.ENERGIZED, enchlvl, "regeneration-duration");
			
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speed_duration*20, speed_amplifier-1));
				entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regeneration_duration*20, regeneration_amplifier-1));
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.SHOCKWAVE))
		{
			int r = ConfigUtil.getAutofilledInt(CustomEnchants.SHOCKWAVE, mainmeta.getEnchantLevel(CustomEnchants.SHOCKWAVE), "radius");
			double dmg = ConfigUtil.getAutofilledDouble(CustomEnchants.SHOCKWAVE, mainmeta.getEnchantLevel(CustomEnchants.SHOCKWAVE), "damage");
			Collection<Entity> list = world.getNearbyEntities(arrow.getLocation(), r, r, r);
			for (Entity entity : list)
			{
				if (!(entity instanceof Damageable))
					continue;
				

				EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.PROJECTILE, 0);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					continue;
				
				Damageable le = ((Damageable)entity);
				le.damage(dmg);
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.FIREWORKS))
		{
			double dmg = ConfigUtil.getAutofilledDouble(CustomEnchants.FIREWORKS, mainmeta.getEnchantLevel(CustomEnchants.FIREWORKS), "damage");
			world.createExplosion(arrow.getLocation(), 0F);
			Collection<Entity> list = world.getNearbyEntities(arrow.getLocation(), 2, 2, 2);
			for (Entity entity : list)
			{
				if (!(entity instanceof Damageable))
					continue;
				

				EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.PROJECTILE, 0);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					continue;
				
				Damageable le = ((Damageable)entity);
				le.damage(dmg);
			}
		}
	}
}
