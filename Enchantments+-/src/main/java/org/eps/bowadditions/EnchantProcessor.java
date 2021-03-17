package org.eps.bowadditions;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.whyisthisnecessary.eps.api.TimeTracker;
import org.whyisthisnecessary.eps.EPS;
import org.whyisthisnecessary.eps.api.CountTracker;
import org.whyisthisnecessary.eps.api.EPSConfiguration;
import org.whyisthisnecessary.eps.api.Reloadable;
import org.whyisthisnecessary.eps.util.LangUtil;

public class EnchantProcessor implements Listener, Reloadable {
	
	private TimeTracker enderbowAbility = new TimeTracker();
	private CountTracker machineryShots = new CountTracker();
	private CountTracker tdShots = new CountTracker();
	private Material cb = Material.matchMaterial("CROSSBOW");
	private Random random = new Random();
	private EPSConfiguration enderbowConfig = EPSConfiguration.getConfiguration(CustomEnchants.ENDERBOW);
	private EPSConfiguration machineryConfig = EPSConfiguration.getConfiguration(CustomEnchants.MACHINERY);
	private EPSConfiguration tbConfig = EPSConfiguration.getConfiguration(CustomEnchants.THUNDERING_BLOW);
	private EPSConfiguration energizedConfig = EPSConfiguration.getConfiguration(CustomEnchants.ENERGIZED);
	private EPSConfiguration shockwaveConfig = EPSConfiguration.getConfiguration(CustomEnchants.SHOCKWAVE);
	private EPSConfiguration fwConfig = EPSConfiguration.getConfiguration(CustomEnchants.FIREWORKS);
	
	public EnchantProcessor()
	{
		LangUtil.setDefaultLangMessage("cooldown-error", "&cYou must wait %secs% more seconds to use this again!");
		LangUtil.setDefaultLangMessage("enderbow-radius-error", "&cYou cannot teleport further than %blocks% blocks!");
		EPS.registerReloader(this);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
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
				double cooldown = enderbowConfig.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.ENDERBOW), "cooldown")*1000;
				
				// Check if cooldown is met
				long time = enderbowAbility.getLastUse(player);
				if (time < cooldown)
					player.sendMessage(LangUtil.getLangMessage("cooldown-error").replaceAll("%secs%", Double.toString(Math.floor(((cooldown-time)/1000)*10)/10)));
				else
				{
					double radius = enderbowConfig.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.ENDERBOW), "radius");
					double distance = player.getLocation().distance(arrow.getLocation());
					
					// Check if distance from player exceeds limits
					if (distance > radius)
						player.sendMessage(LangUtil.getLangMessage("enderbow-radius-error").replaceAll("%blocks%", Double.toString(radius)));
					else
					{
						enderbowAbility.use(player);
						player.teleport(arrow.getLocation());
						player.playEffect(EntityEffect.TELEPORT_ENDER);
					}
				}
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.MACHINERY))
		{
			int shotstoactivate = machineryConfig.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "shots-to-activate");
			int shots = machineryShots.increase(player);
			if (shots >= shotstoactivate)
			{
				machineryShots.reset(player);
				int radius = machineryConfig.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "radius");
				int arrows = machineryConfig.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "arrows");
				Location loc = arrow.getLocation();
				for (int i=0;i<arrows;i++)
				{
					Location location = new Location(world, nextDouble(radius*2)-radius+loc.getX(), loc.getY()+48, nextDouble(radius*2)-radius+loc.getZ());
					world.spawnArrow(location, new Vector(0, -90, 0), 5, 0);
				}
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.THUNDERING_BLOW))
		{
			int shotstoactivate = tbConfig.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.THUNDERING_BLOW), "shots-to-activate");
			int shots = tdShots.increase(player);
			if (shots >= shotstoactivate)
			{
				tdShots.reset(player);
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
				int speed_amplifier = energizedConfig.getAutofilledInt(enchlvl, "speed-amplifier");
				int speed_duration = energizedConfig.getAutofilledInt(enchlvl, "speed-duration");
				int regeneration_amplifier = energizedConfig.getAutofilledInt(enchlvl, "regeneration-amplifier");
				int regeneration_duration = energizedConfig.getAutofilledInt(enchlvl, "regeneration-duration");
			
				entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, speed_duration*20, speed_amplifier-1));
				entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regeneration_duration*20, regeneration_amplifier-1));
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.SHOCKWAVE))
		{
			int r = shockwaveConfig.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.SHOCKWAVE), "radius");
			double dmg = shockwaveConfig.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.SHOCKWAVE), "damage");
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
			double dmg = fwConfig.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.FIREWORKS), "damage");
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
		
		if (mainmeta.hasEnchant(CustomEnchants.FLAMMABLE))
		{
			Block block = e.getEntity().getLocation().getBlock();
			BlockBreakEvent b = new BlockBreakEvent(block, player);
			Bukkit.getPluginManager().callEvent(b);
			if (!b.isCancelled())
				block.setType(Material.FIRE);
		}
		
		
	}
	
	public double nextDouble(int i)
	{
		return random.nextDouble()*i;
	}

	@Override
	public void reload() 
	{
		enderbowConfig = EPSConfiguration.getConfiguration(CustomEnchants.ENDERBOW);
		machineryConfig = EPSConfiguration.getConfiguration(CustomEnchants.MACHINERY);
		tbConfig = EPSConfiguration.getConfiguration(CustomEnchants.THUNDERING_BLOW);
		energizedConfig = EPSConfiguration.getConfiguration(CustomEnchants.ENERGIZED);
		shockwaveConfig = EPSConfiguration.getConfiguration(CustomEnchants.SHOCKWAVE);
		fwConfig = EPSConfiguration.getConfiguration(CustomEnchants.FIREWORKS);
	}
}
