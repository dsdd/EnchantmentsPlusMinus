package org.eps.pvppack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
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

	private static final Map<EntityType, UUID> MHF_UUIDs = new HashMap<EntityType, UUID>() {
		{
			put(EntityType.BLAZE, UUID.fromString("4c38ed11-596a-4fd4-ab1d-26f386c1cbac")); // MHF_Blaze
			put(EntityType.CAVE_SPIDER, UUID.fromString("cab28771-f0cd-4fe7-b129-02c69eba79a5"));  // MHF_CaveSpider
			put(EntityType.CHICKEN, UUID.fromString("92deafa9-4307-42d9-b003-88601598d6c0")); // MHF_Chicken
			put(EntityType.COW, UUID.fromString("f159b274-c22e-4340-b7c1-52abde147713")); // MHF_Cow
			put(EntityType.ENDERMAN, UUID.fromString("40ffb372-12f6-4678b-3f22-176bf56dd4b")); // MHF_Enderman
			put(EntityType.GHAST, UUID.fromString("063085a6-797f-4785b-e1a2-1cd7580f752")); // MHF_Ghast
			put(EntityType.IRON_GOLEM, UUID.fromString("757f90b2-2344-4b8d8-dac8-24232e2cece")); // MHF_Golem
			put(EntityType.MAGMA_CUBE, UUID.fromString("0972bdd1-4b86-49fb9-ecca-353f8491a51")); // MHF_LavaSlime
			put(EntityType.MUSHROOM_COW, UUID.fromString("a46817d6-73c5-4f3fb-712af-6b3ff47b96")); // MHF_MushroomCow
			put(EntityType.OCELOT, UUID.fromString("1bee9df5-4f71-42a2b-f52d9-7970d3fea3")); // MHF_Ocelot
			put(EntityType.PIG, UUID.fromString("8b57078b-f1bd-45df8-3c4d8-8d16768fbe")); // MHF_Pig
			//put(EntityType.PIG_ZOMBIE, UUID.fromString("18a2bb50-334a-40849-1842c-380251a24b")); // MHF_PigZombie
			put(EntityType.SHEEP, UUID.fromString("dfaad551-4e7e-45a1a-6f7c6-fc5ec823ac")); // MHF_Sheep
			put(EntityType.SLIME, UUID.fromString("870aba93-40e8-48b38-9c532-ece00d6630")); // MHF_Slime
			put(EntityType.SPIDER, UUID.fromString("5ad55f34-41b6-4bd29-c3218-983c635936")); // MHF_Spider
			put(EntityType.SQUID, UUID.fromString("72e64683-e313-4c36a-408c6-6b64e94af5")); // MHF_Squid
			put(EntityType.VILLAGER, UUID.fromString("bd482739-767c-45dca-1f8c3-3c40530952")); // MHF_Villager
			put(EntityType.WITHER_SKELETON, UUID.fromString("7ed571a5-9fb8-416c8-b9dfb-2f446ab5b2")); // MHF_WSkeleton
		}
	};

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
			if (MHF_UUIDs.containsKey(e.getType())) {
				ItemStack head = LegacyUtil.isLegacy() ? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1) : new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta skull = (SkullMeta) head.getItemMeta();
				skull.setOwningPlayer(Bukkit.getOfflinePlayer(MHF_UUIDs.get(e.getType())));
				head.setItemMeta(skull);
				return head;
			}
			return null;
		}
	}
}
