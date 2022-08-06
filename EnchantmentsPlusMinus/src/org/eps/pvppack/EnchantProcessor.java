package org.eps.pvppack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.vivi.eps.EPS;
import org.vivi.eps.api.CountTracker;
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.api.TimeTracker;
import org.vivi.eps.util.Language;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class EnchantProcessor implements Listener, Reloadable {

	public static Random rand = new Random();
	private TimeTracker phCooldown = new TimeTracker();
	private TimeTracker bsCooldown = new TimeTracker();
	private CountTracker evadeCount = new CountTracker();
	private CountTracker mCount = new CountTracker();
	private EPSConfiguration jaggedConfig = EPSConfiguration.getConfiguration(CustomEnchants.JAGGED);
	private EPSConfiguration lsConfig = EPSConfiguration.getConfiguration(CustomEnchants.LIFESTEAL);
	private EPSConfiguration momentumConfig = EPSConfiguration.getConfiguration(CustomEnchants.MOMENTUM);
	private EPSConfiguration lrConfig = EPSConfiguration.getConfiguration(CustomEnchants.LAST_RESORT);
	private EPSConfiguration retaliateConfig = EPSConfiguration.getConfiguration(CustomEnchants.RETALIATE);
	private EPSConfiguration beheadingConfig = EPSConfiguration.getConfiguration(CustomEnchants.BEHEADING);
	private EPSConfiguration stiffenConfig = EPSConfiguration.getConfiguration(CustomEnchants.STIFFEN);
	private EPSConfiguration phConfig = EPSConfiguration.getConfiguration(CustomEnchants.POWERHOUSE);
	private EPSConfiguration meltingConfig = EPSConfiguration.getConfiguration(CustomEnchants.MELTING);
	private EPSConfiguration bsConfig = EPSConfiguration.getConfiguration(CustomEnchants.BACKUP_SPELLS);
	private EPSConfiguration evadeConfig = EPSConfiguration.getConfiguration(CustomEnchants.EVADE);
	private String cooldownError = Language.getLangMessage("cooldown-error");
	
	private static final Map<EntityType, String> mobHeads = new HashMap<EntityType, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(EntityType.BLAZE, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyNTMzNTAzOCwKICAicHJvZmlsZUlkIiA6ICI0YzM4ZWQxMTU5NmE0ZmQ0YWIxZDI2ZjM4NmMxY2JhYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQmxhemUiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA2ZTM0MmY5MGVjNTM4YWFhMTU1MmIyMjRmMjY0YTA0MDg0MDkwMmUxMjZkOTFlY2U2MTM5YWE1YjNjN2NjMyIKICAgIH0KICB9Cn0="); // MHF_Blaze
			put(EntityType.CAVE_SPIDER, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyNTM2MzQ4NywKICAicHJvZmlsZUlkIiA6ICJjYWIyODc3MWYwY2Q0ZmU3YjEyOTAyYzY5ZWJhNzlhNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQ2F2ZVNwaWRlciIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83N2IwNzA2M2E2ODc0ZmEzZTIyNTQ4ZTAyMDYyYmQ3MzNjMjU4ODU5Mjk4MDk2MjQxODBhZWJiODUxNTU3ZjZhIgogICAgfQogIH0KfQ==");  // MHF_CaveSpider
			put(EntityType.CHICKEN, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyNTM4NzcxOCwKICAicHJvZmlsZUlkIiA6ICI5MmRlYWZhOTQzMDc0MmQ5YjAwMzg4NjAxNTk4ZDZjMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQ2hpY2tlbiIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85MTZiOGU5ODM4OWM1NDFiYjM2NDUzODUwYmNiZDFmN2JjNWE1N2RhNjJkY2M1MDUwNjA0MDk3MzdlYzViNzJhIgogICAgfQogIH0KfQ=="); // MHF_Chicken
			put(EntityType.COW, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyNTQwNTg2MCwKICAicHJvZmlsZUlkIiA6ICJmMTU5YjI3NGMyMmU0MzQwYjdjMTUyYWJkZTE0NzcxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQ293IiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QwZTRlNmZiZjVmM2RjZjk0NDIyYTFmMzE5NDQ4ZjE1MjM2OWQxNzlkYmZiY2RmMDBlNWJmZTg0OTVmYTk3NyIKICAgIH0KICB9Cn0"); // MHF_Cow
			put(EntityType.ENDERMAN, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODI5Mjk3OCwKICAicHJvZmlsZUlkIiA6ICI0MGZmYjM3MjEyZjY0Njc4YjNmMjIxNzZiZjU2ZGQ0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfRW5kZXJtYW4iLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIwOWEzNzUyNTEwZTkxNGIwYmRjOTA5NmIzOTJiYjM1OWY3YThlOGE5NTY2YTAyZTdmNjZmYWZmOGQ2Zjg5ZSIKICAgIH0KICB9Cn0="); // MHF_Enderman
			put(EntityType.GHAST, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODQyNTk0NiwKICAicHJvZmlsZUlkIiA6ICIwNjMwODVhNjc5N2Y0Nzg1YmUxYTIxY2Q3NTgwZjc1MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfR2hhc3QiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE0ZTQyZWIxNWEwODgxM2E2YTZmNjFmMTBhYTI4ODAxOWZhMGZhZTEwNmEyOTUzZGRiNDZmNzdlZTJkNzdmIgogICAgfQogIH0KfQ=="); // MHF_Ghast
			put(EntityType.IRON_GOLEM, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODYxODQxMywKICAicHJvZmlsZUlkIiA6ICI3NTdmOTBiMjIzNDQ0YjhkOGRhYzgyNDIzMmUyY2VjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfR29sZW0iLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM2Y2Q3MjAyYzM0ZTc4ZjMwNzMwOTAzNDlmN2Q5NzNiMjg4YWY1ZTViNzMzNGRkMjQ5MDEwYjNmMjcwNzhmOSIKICAgIH0KICB9Cn0="); // MHF_Golem
			put(EntityType.MAGMA_CUBE, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODcxOTQ1MywKICAicHJvZmlsZUlkIiA6ICIwOTcyYmRkMTRiODY0OWZiOWVjY2EzNTNmODQ5MWE1MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfTGF2YVNsaW1lIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q5MGQ2MWU4Y2U5NTExYTBhMmI1ZWEyNzQyY2IxZWYzNjEzMTM4MGVkNDEyOWUxYjE2M2NlOGZmMDAwZGU4ZWEiCiAgICB9CiAgfQp9"); // MHF_LavaSlime
			put(EntityType.MUSHROOM_COW, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODc0NzAzNiwKICAicHJvZmlsZUlkIiA6ICJhNDY4MTdkNjczYzU0ZjNmYjcxMmFmNmIzZmY0N2I5NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfTXVzaHJvb21Db3ciLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTIzY2ZjNTU4MjQ1NGZjZjk5MDZmODQxZmRhMmNjNmFlODk2Y2Y0NTU4MjFjNGFkYTE5OThkZTcwODc3Y2M4NiIKICAgIH0KICB9Cn0="); // MHF_MushroomCow
			put(EntityType.OCELOT, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODc3OTUwMywKICAicHJvZmlsZUlkIiA6ICIxYmVlOWRmNTRmNzE0MmEyYmY1MmQ5Nzk3MGQzZmVhMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfT2NlbG90IiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzExOGI2Yjc5NzgzMzY4ZGZlMDA0Mjk4NTExMGRhMzY2ZjljNzg4YjQ1MDk3YTNlYTZkMGQ5YTc1M2U5ZjQyYzYiCiAgICB9CiAgfQp9"); // MHF_Ocelot
			put(EntityType.PIG, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODgyMDE4NywKICAicHJvZmlsZUlkIiA6ICI4YjU3MDc4YmYxYmQ0NWRmODNjNGQ4OGQxNjc2OGZiZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfUGlnIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E1NjJhMzdiODcxZjk2NGJmYzNlMTMxMWVhNjcyYWFhMDM5ODRhNWRjNDcyMTU0YTM0ZGMyNWFmMTU3ZTM4MmIiCiAgICB9CiAgfQp9"); // MHF_Pig
			//put(EntityType.ZOMBIFIED_PIGLIN, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODg1NDMzMiwKICAicHJvZmlsZUlkIiA6ICIxOGEyYmI1MDMzNGE0MDg0OTE4NDJjMzgwMjUxYTI0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfUGlnWm9tYmllIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkxNmQxNjdjNTc0NGVkMTRlYmMwMmY0NDdmMzI2MTQwNTkzNjJiN2QyZWNiODA4ZmYwNjE2NWQyYzM0M2JlZjIiCiAgICB9CiAgfQp9"); // MHF_PigZombie
			put(EntityType.SHEEP, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODk2MzMzMSwKICAicHJvZmlsZUlkIiA6ICJkZmFhZDU1MTRlN2U0NWExYTZmN2M2ZmM1ZWM4MjNhYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU2hlZXAiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NhMzhjY2Y0MTdlOTljYTlkNDdlZWIxNWE4YTMwZWRiMTUwN2FhNTJiNjc4YzIyMGM3MTdjNDc0YWE2ZmUzZSIKICAgIH0KICB9Cn0="); // MHF_Sheep
			put(EntityType.SLIME, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTA0NDM1MiwKICAicHJvZmlsZUlkIiA6ICI4NzBhYmE5MzQwZTg0OGIzODljNTMyZWNlMDBkNjYzMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU2xpbWUiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZjMjdiMDEzZjFiZjMzNDQ4NjllODFlNWM2MTAwMjdiYzQ1ZWM1Yjc5NTE0ZmRjOTZlMDFkZjFiN2UzYTM4NyIKICAgIH0KICB9Cn0="); // MHF_Slime
			put(EntityType.SPIDER, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTA5MTU4MCwKICAicHJvZmlsZUlkIiA6ICI1YWQ1NWYzNDQxYjY0YmQyOWMzMjE4OTgzYzYzNTkzNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3BpZGVyIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y2MWE0OTU0MWE4MzZhYThmNGY3NmUwZDRjYjJmZjA0ODg4YzYyZjk0MTFlYTEwY2JhY2YxZjJhNTQ0MjQyNDAiCiAgICB9CiAgfQp9"); // MHF_Spider
			put(EntityType.SQUID, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTEzOTQwNiwKICAicHJvZmlsZUlkIiA6ICI3MmU2NDY4M2UzMTM0YzM2YTQwOGM2NmI2NGU5NGFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3F1aWQiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU4OTEwMWQ1Y2M3NGFhNDU4MDIxYTA2MGY2Mjg5YTUxYTM1YTdkMzRkOGNhZGRmYzNjZGYzYjJjOWEwNzFhIgogICAgfQogIH0KfQ=="); // MHF_Squid
			put(EntityType.VILLAGER, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTE3MzU5NSwKICAicHJvZmlsZUlkIiA6ICJiZDQ4MjczOTc2N2M0NWRjYTFmOGMzM2M0MDUzMDk1MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfVmlsbGFnZXIiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRiZDgzMjgxM2FjMzhlNjg2NDg5MzhkN2EzMmY2YmEyOTgwMWFhZjMxNzQwNDM2N2YyMTRiNzhiNGQ0NzU0YyIKICAgIH0KICB9Cn0="); // MHF_Villager
			put(EntityType.WITHER_SKELETON, "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTE5OTczMywKICAicHJvZmlsZUlkIiA6ICI3ZWQ1NzFhNTlmYjg0MTZjOGI5ZGZiMmY0NDZhYjViMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfV1NrZWxldG9uIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JhOTZlOWQ3NmJlZDMwMDkwY2U2ZTJkODQyNTk5NjU5NGVlYzZkNjhhYzg4Y2YwNzM1NmU5ODE0ODM0MjQzZWMiCiAgICB9CiAgfQp9"); // MHF_WSkeleton
		}
	};
	private static Map<EntityType, ItemStack> mobSkulls = new HashMap<EntityType, ItemStack>();
	
	public EnchantProcessor(Plugin plugin) 
	{
		Bukkit.getPluginManager().registerEvents(this, plugin);
		EPS.registerReloader(this);
		
		if (beheadingConfig.isSet("chance"))
		{
			String s = beheadingConfig.getString("chance");
			beheadingConfig.set("mob-chance", s);
			beheadingConfig.set("player-chance", s);
			beheadingConfig.set("chance", null);
			beheadingConfig.save();
		}
		
		if (!EPS.onLegacy())
		for (Map.Entry<EntityType, String> entry : mobHeads.entrySet())
			mobSkulls.put(entry.getKey(), getCustomSkull(mobHeads.get(entry.getKey()), WordUtils.capitalizeFully(entry.getKey().name()) + " Head"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		if (e.getDamager() instanceof Player)
		{
		
			Player d = (Player) e.getDamager();
			World world = d.getWorld();
			ItemStack item = d.getInventory().getItemInMainHand();
			ItemMeta meta = item.getItemMeta();
			if (item == null || meta == null)
				return;
			if (meta.hasEnchant(CustomEnchants.JAGGED))
			{
				Durability dmg = new Durability(item);
				int enchlvl =  meta.getEnchantLevel(CustomEnchants.JAGGED);
				double dtp = jaggedConfig.getAutofilledDouble(enchlvl, "durabilitythresholdpercent");
				double dp = (double) (dmg.getMaxDurability()/dmg.getDurability()*100);
				if (dp < dtp) 
				{
				    e.setDamage(e.getDamage() + enchlvl);
				    if (!EPS.onLegacy())
				    	world.spawnParticle(Particle.REDSTONE, e.getEntity().getLocation(), 1, new Particle.DustOptions(Color.RED, 5));
				    else
				    	world.spawnParticle(Particle.REDSTONE, e.getEntity().getLocation(), 1);
				}
			}
			
			if (meta.hasEnchant(CustomEnchants.LIFESTEAL))
			{
				double hp = d.getHealth() + lsConfig.getAutofilledDouble(meta.getEnchantLevel(CustomEnchants.LIFESTEAL), "hearts") * (e.getDamage() / 10);
				if (hp > 20) 
					hp = 20.0;
				d.setHealth(hp);
				world.spawnParticle(Particle.HEART, e.getEntity().getLocation(), 1);
			}
			
			if (meta.hasEnchant(CustomEnchants.MOMENTUM))
			{
				int enchlvl = meta.getEnchantLevel(CustomEnchants.MOMENTUM);
				int duration = momentumConfig.getAutofilledInt(enchlvl, "duration")*20;
				d.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, enchlvl-1));
				world.spawnParticle(Particle.CLOUD, e.getEntity().getLocation(), 1);
			}
			
			if (meta.hasEnchant(CustomEnchants.LAST_RESORT))
			{
				double healththreshold = lrConfig.getAutofilledDouble(meta.getEnchantLevel(CustomEnchants.LAST_RESORT), "healththreshold");
				if (d.getHealth() <= healththreshold)
					e.setDamage(e.getDamage()*3);
			}
			
			if (meta.hasEnchant(CustomEnchants.MELTING))
			{
				if (e.getEntity() instanceof LivingEntity)
				{
					int enchlvl = meta.getEnchantLevel(CustomEnchants.MELTING);
					LivingEntity hit = (LivingEntity) e.getEntity();
					hit.setFireTicks(hit.getFireTicks() + meltingConfig.getAutofilledInt(enchlvl, "fire-ticks"));
					hit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, meltingConfig.getAutofilledInt(enchlvl, "slowness-ticks"), meltingConfig.getAutofilledInt(enchlvl, "slowness-level")-1));
					mCount.increase(d);
					if (mCount.get(d) >= meltingConfig.getAutofilledInt(enchlvl, "freeze-hits"))
					{
						mCount.reset(d);
						hit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, meltingConfig.getAutofilledInt(enchlvl, "freeze-ticks"), 20));
					}
				}
			}
			
			ItemStack[] armor = {d.getInventory().getHelmet(), d.getInventory().getChestplate(), d.getInventory().getLeggings(), d.getInventory().getBoots()};
			for (ItemStack piece : armor)
			{
				if (piece != null)
				{
					if (piece.getItemMeta().hasEnchant(CustomEnchants.INSATIABLE))
					{
						double damage = EPSConfiguration.getConfiguration(CustomEnchants.INSATIABLE).getAutofilledDouble(piece.getItemMeta().getEnchantLevel(CustomEnchants.INSATIABLE), "extradamage");
						e.setDamage(e.getDamage() + (damage - (damage*(d.getHealth()/20))));
						if (!EPS.onLegacy())
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
			Entity d = e.getDamager();
			World world = p.getWorld();
			ItemStack item = p.getInventory().getItemInMainHand();
			ItemMeta meta = item.getItemMeta();
			
			if (item == null || meta == null) 
				return;
			if (meta.hasEnchant(CustomEnchants.RETALIATE))
			{
				int enchlvl = meta.getEnchantLevel(CustomEnchants.RETALIATE);
				int duration = retaliateConfig.getAutofilledInt(enchlvl, "duration")*20;
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, enchlvl-1));
			}
			
			ItemStack[] armor = {p.getInventory().getHelmet(), p.getInventory().getChestplate(), p.getInventory().getLeggings(), p.getInventory().getBoots()};
			if (e.getDamager() instanceof LivingEntity)
				for (ItemStack piece : armor)
				{
					if (piece != null)
					{
						ItemMeta pmeta = piece.getItemMeta();
						if (pmeta.hasEnchant(CustomEnchants.POISONOUS))
						{
							
							LivingEntity en = (LivingEntity) e.getDamager();
							int duration = EPSConfiguration.getConfiguration(CustomEnchants.POISONOUS).getAutofilledInt(pmeta.getEnchantLevel(CustomEnchants.POISONOUS), "ticks");
							PotionCombiner.addEffect(en, new PotionEffect(PotionEffectType.POISON, duration, pmeta.getEnchantLevel(CustomEnchants.POISONOUS)-1));
						}
						
						if (pmeta.hasEnchant(CustomEnchants.VOLCANIC))
						{
							int ticks = EPSConfiguration.getConfiguration(CustomEnchants.VOLCANIC).getAutofilledInt(pmeta.getEnchantLevel(CustomEnchants.VOLCANIC), "ticks");
		                    d.setFireTicks(d.getFireTicks()+ticks);
		                    world.spawnParticle(Particle.LAVA, e.getEntity().getLocation(), 1);
						}
						
						if (pmeta.hasEnchant(CustomEnchants.SATURATED))
						{
							int duration = EPSConfiguration.getConfiguration(CustomEnchants.SATURATED).getAutofilledInt(pmeta.getEnchantLevel(CustomEnchants.SATURATED), "ticks");
							PotionCombiner.addEffect(p, new PotionEffect(PotionEffectType.SATURATION, duration, pmeta.getEnchantLevel(CustomEnchants.SATURATED)-1));
						}
						
						if (pmeta.hasEnchant(CustomEnchants.STIFFEN))
						{
							double healththreshold = stiffenConfig.getAutofilledDouble(pmeta.getEnchantLevel(CustomEnchants.STIFFEN), "healththreshold");
							int amplifier = stiffenConfig.getAutofilledInt(pmeta.getEnchantLevel(CustomEnchants.STIFFEN), "amplifier")-1;
							LivingEntity l = ((LivingEntity)e.getEntity());
							if (l.getHealth() <= healththreshold)
		                    	PotionCombiner.addEffect(l, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, amplifier-1));
						}
						if (pmeta.hasEnchant(CustomEnchants.EVADE))
						{
							evadeCount.increase(p);
							int enchlvl = pmeta.getEnchantLevel(CustomEnchants.EVADE);
							if (evadeConfig.getBoolean("luckbased"))
								if (getNext() < evadeConfig.getAutofilledInt(enchlvl, "chance"))
								{
									e.setCancelled(true);
								}
							else
								if (evadeCount.get(p) > evadeConfig.getAutofilledInt(enchlvl, "hits-to-activate"))
								{
									e.setCancelled(true);
								}
						}
					}
				}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY())
			return;
		Player p = e.getPlayer();
		ItemStack[] armor = {p.getInventory().getHelmet(), p.getInventory().getChestplate(), p.getInventory().getLeggings(), p.getInventory().getBoots()};
		int overhealLvl = 0;
		boolean heal = false;
		boolean hpBoosted = p.hasPotionEffect(PotionEffectType.HEALTH_BOOST);
		for (ItemStack piece : armor)
		{
			if (piece == null)
				continue;
			
			ItemMeta pmeta = piece.getItemMeta();
				if (pmeta.hasEnchant(CustomEnchants.OVERHEALED))
				{
					if (!hpBoosted)
						overhealLvl++;
					heal = true;
				}
		}
		
		if (heal)
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, overhealLvl-1));
		else
			p.removePotionEffect(PotionEffectType.HEALTH_BOOST);
	}
	
	@EventHandler
	public void onKill(EntityDeathEvent e)
	{
		if (e.getEntity().getKiller() == null) 
			return;
		ItemStack item = e.getEntity().getKiller().getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if (meta.hasEnchant(CustomEnchants.BEHEADING))
		{
			int enchlvl = meta.getEnchantLevel(CustomEnchants.BEHEADING);
			double chance = beheadingConfig.getAutofilledInt(enchlvl, "mob-chance");
			if (getNext() <= chance)
			{
				ItemStack head = getHead(e.getEntity());
				if (head != null)
					e.getDrops().add(head);
			}
		}
	}

	@EventHandler
	public void onPlayerKill(PlayerDeathEvent e)
	{
		if (e.getEntity().getKiller() == null) 
			return;
		ItemStack item = e.getEntity().getKiller().getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		if (meta != null)
			if (meta.hasEnchant(CustomEnchants.BEHEADING))
			{
				int enchlvl = meta.getEnchantLevel(CustomEnchants.BEHEADING);
				double chance = beheadingConfig.getAutofilledInt(enchlvl, "player-chance");
				if (getNext() <= chance)
				{
					ItemStack head = EPS.onLegacy() ? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1) : new ItemStack(Material.PLAYER_HEAD, 1);
					SkullMeta skull = (SkullMeta) head.getItemMeta();
					skull.setOwningPlayer(e.getEntity());
					head.setItemMeta(skull);
					e.getDrops().add(head);
				}
			}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			ItemMeta meta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
			if (meta == null)
				return;
			if (!e.getPlayer().isSneaking())
				return;
			if (meta.hasEnchant(CustomEnchants.POWERHOUSE))
			{
				int enchlvl = meta.getEnchantLevel(CustomEnchants.POWERHOUSE);
				double cooldown = phConfig.getAutofilledDouble(enchlvl, "cooldown")*1000;
				
				long lastuse = phCooldown.getLastUse(e.getPlayer());
				if (lastuse < cooldown)
					e.getPlayer().sendMessage(cooldownError.replaceAll("%secs%", Double.toString(Math.floor(((cooldown-lastuse)/1000)*10)/10)));
				else
				{
					phCooldown.use(e.getPlayer());
					int duration = phConfig.getAutofilledInt(enchlvl, "duration");
					int amplifier = phConfig.getAutofilledInt(enchlvl, "amplifier")-1;
		            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, amplifier));
				}
			}
			if (meta.hasEnchant(CustomEnchants.BACKUP_SPELLS))
			{
				int enchlvl = meta.getEnchantLevel(CustomEnchants.BACKUP_SPELLS);
				double cooldown = bsConfig.getAutofilledDouble(enchlvl, "cooldown")*1000;
				long lastuse = bsCooldown.getLastUse(e.getPlayer());
				if (lastuse < cooldown)
					e.getPlayer().sendMessage(cooldownError.replaceAll("%secs%", Double.toString(Math.floor(((cooldown-lastuse)/1000)*10)/10)));
				else
				{
					bsCooldown.use(e.getPlayer());
					ItemStack i = new ItemStack(Material.SPLASH_POTION);
					PotionMeta pmeta = (PotionMeta) i.getItemMeta();
					pmeta.addCustomEffect(new PotionEffect(randomEffect(), 20, 0), true);

					i.setItemMeta(pmeta);
					ThrownPotion tPotion = e.getPlayer().launchProjectile(ThrownPotion.class);
					tPotion.setItem(i);
				}
			}
		}
	}
	
	public static double getNext()
	{
		return rand.nextDouble()*100;
	}
	
	private static PotionEffectType randomEffect()
	{
		int i = rand.nextInt(5);
		switch (i)
		{
			case 0:
				return PotionEffectType.SLOW;
			case 1:
				return PotionEffectType.WEAKNESS;
			case 2:
				return PotionEffectType.BLINDNESS;
			case 3:
				return PotionEffectType.POISON;
			case 4:
				return PotionEffectType.WITHER;
			case 5:
				return PotionEffectType.SLOW_DIGGING;
			default:
				return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	private static ItemStack getHead(LivingEntity e)
	{
		switch (e.getType())
		{
		case SKELETON:
			if (!EPS.onLegacy())
				return new ItemStack(Material.SKELETON_SKULL, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 0);
		case WITHER_SKELETON:
			if (!EPS.onLegacy())
				return new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 1);
		case ZOMBIE:
			if (!EPS.onLegacy())
				return new ItemStack(Material.ZOMBIE_HEAD, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 2);
		case CREEPER:
			if (!EPS.onLegacy())
				return new ItemStack(Material.CREEPER_HEAD, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 4);
		case ENDER_DRAGON:
			if (!EPS.onLegacy())
				return new ItemStack(Material.DRAGON_HEAD, 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 5);
		default:
			if (!EPS.onLegacy())
				if (mobHeads.containsKey(e.getType()))
					return mobSkulls.get(e.getType());
			return null;
		}
	}
	
	private static ItemStack getCustomSkull(String texture, String name) {
		ItemStack head = EPS.onLegacy() ? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1) : new ItemStack(Material.PLAYER_HEAD, 1);

		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setDisplayName(name);
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);

		profile.getProperties().put("textures", new Property("textures", texture));

		try {
			Method mtd = skull.getClass().getDeclaredMethod("setProfile", GameProfile.class);
			mtd.setAccessible(true);
			mtd.invoke(skull, profile);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}

		head.setItemMeta(skull);
		return head;
	}

	@Override
	public void reload() {
		jaggedConfig = EPSConfiguration.getConfiguration(CustomEnchants.JAGGED);
		lsConfig = EPSConfiguration.getConfiguration(CustomEnchants.LIFESTEAL);
		momentumConfig = EPSConfiguration.getConfiguration(CustomEnchants.MOMENTUM);
		lrConfig = EPSConfiguration.getConfiguration(CustomEnchants.LAST_RESORT);
		retaliateConfig = EPSConfiguration.getConfiguration(CustomEnchants.RETALIATE);
		beheadingConfig = EPSConfiguration.getConfiguration(CustomEnchants.BEHEADING);
		phConfig = EPSConfiguration.getConfiguration(CustomEnchants.POWERHOUSE);
		meltingConfig = EPSConfiguration.getConfiguration(CustomEnchants.MELTING);
		bsConfig = EPSConfiguration.getConfiguration(CustomEnchants.BACKUP_SPELLS);
		evadeConfig = EPSConfiguration.getConfiguration(CustomEnchants.EVADE);
		cooldownError = Language.getLangMessage("cooldown-error");
	}
}

class PotionCombiner {
	
	public static void addEffect(LivingEntity e, PotionEffect pe)
	{
		PotionEffect a = e.getPotionEffect(pe.getType());
		if (a == null)
		{
			pe.apply(e);
			return;
		}
		
		e.addPotionEffect(new PotionEffect(pe.getType(), pe.getDuration()+a.getDuration(), pe.getAmplifier()));
	}
	
	public static void amplifyEffect(LivingEntity e, PotionEffect pe)
	{
		PotionEffect a = e.getPotionEffect(pe.getType());
		if (a == null)
		{
			pe.apply(e);
			return;
		}
		
		e.addPotionEffect(new PotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier()+a.getAmplifier()));
	}
}