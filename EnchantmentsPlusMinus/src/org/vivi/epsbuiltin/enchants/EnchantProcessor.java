package org.vivi.epsbuiltin.enchants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.vivi.eps.EPS;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.gui.EnchantsGUI;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.economy.Economy;
import org.vivi.sekai.PlayerKeeper.PlayerStopwatch;
import org.vivi.sekai.PlayerAttributes;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.dependencies.VaultHook;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * To be superseded by {@link org.vivi.eps.api.EnchantHandler}
 * 
 * @author vivisan
 *
 */
@Deprecated
public class EnchantProcessor implements Listener, Reloadable
{
	private static String cooldownError = Language.getLangMessage("cooldown-error");
	private static String inventoryFull = Language.getLangMessage("inventoryfull");

	private final static Map<Player, List<ItemStack>> itemsToKeep = new HashMap<Player, List<ItemStack>>();

	private static boolean repairDebounce = false;
	private static Material crossbowMaterial = Material.matchMaterial("CROSSBOW");
	private static Random random = new Random();
	private static boolean modifiedByEnchant = false;
	private static boolean looping = false;
	private final static Collection<Location> saves = new ArrayList<Location>();
	private final static Material endframe = Sekai.getMCVersion() < 13 ? Material.matchMaterial("ENDER_PORTAL_FRAME")
			: Material.matchMaterial("END_PORTAL_FRAME");
	private final static Economy economy = EPS.getEconomy();
	private static Map<Material, Material> smeltResults = new HashMap<Material, Material>();
	private final static Sound ENTITY_ENDERMAN_TELEPORT = Sekai.getMCVersion() < 13
			? Sound.valueOf("ENTITY_ENDERMEN_TELEPORT")
			: Sound.valueOf("ENTITY_ENDERMAN_TELEPORT");

	private static final Map<EntityType, String> mobHeads = new HashMap<EntityType, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(EntityType.BLAZE,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyNTMzNTAzOCwKICAicHJvZmlsZUlkIiA6ICI0YzM4ZWQxMTU5NmE0ZmQ0YWIxZDI2ZjM4NmMxY2JhYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQmxhemUiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA2ZTM0MmY5MGVjNTM4YWFhMTU1MmIyMjRmMjY0YTA0MDg0MDkwMmUxMjZkOTFlY2U2MTM5YWE1YjNjN2NjMyIKICAgIH0KICB9Cn0="); // MHF_Blaze
			put(EntityType.CAVE_SPIDER,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyNTM2MzQ4NywKICAicHJvZmlsZUlkIiA6ICJjYWIyODc3MWYwY2Q0ZmU3YjEyOTAyYzY5ZWJhNzlhNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQ2F2ZVNwaWRlciIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83N2IwNzA2M2E2ODc0ZmEzZTIyNTQ4ZTAyMDYyYmQ3MzNjMjU4ODU5Mjk4MDk2MjQxODBhZWJiODUxNTU3ZjZhIgogICAgfQogIH0KfQ=="); // MHF_CaveSpider
			put(EntityType.CHICKEN,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyNTM4NzcxOCwKICAicHJvZmlsZUlkIiA6ICI5MmRlYWZhOTQzMDc0MmQ5YjAwMzg4NjAxNTk4ZDZjMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQ2hpY2tlbiIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85MTZiOGU5ODM4OWM1NDFiYjM2NDUzODUwYmNiZDFmN2JjNWE1N2RhNjJkY2M1MDUwNjA0MDk3MzdlYzViNzJhIgogICAgfQogIH0KfQ=="); // MHF_Chicken
			put(EntityType.COW,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyNTQwNTg2MCwKICAicHJvZmlsZUlkIiA6ICJmMTU5YjI3NGMyMmU0MzQwYjdjMTUyYWJkZTE0NzcxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfQ293IiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QwZTRlNmZiZjVmM2RjZjk0NDIyYTFmMzE5NDQ4ZjE1MjM2OWQxNzlkYmZiY2RmMDBlNWJmZTg0OTVmYTk3NyIKICAgIH0KICB9Cn0"); // MHF_Cow
			put(EntityType.ENDERMAN,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODI5Mjk3OCwKICAicHJvZmlsZUlkIiA6ICI0MGZmYjM3MjEyZjY0Njc4YjNmMjIxNzZiZjU2ZGQ0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfRW5kZXJtYW4iLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWIwOWEzNzUyNTEwZTkxNGIwYmRjOTA5NmIzOTJiYjM1OWY3YThlOGE5NTY2YTAyZTdmNjZmYWZmOGQ2Zjg5ZSIKICAgIH0KICB9Cn0="); // MHF_Enderman
			put(EntityType.GHAST,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODQyNTk0NiwKICAicHJvZmlsZUlkIiA6ICIwNjMwODVhNjc5N2Y0Nzg1YmUxYTIxY2Q3NTgwZjc1MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfR2hhc3QiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGE0ZTQyZWIxNWEwODgxM2E2YTZmNjFmMTBhYTI4ODAxOWZhMGZhZTEwNmEyOTUzZGRiNDZmNzdlZTJkNzdmIgogICAgfQogIH0KfQ=="); // MHF_Ghast
			put(EntityType.IRON_GOLEM,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODYxODQxMywKICAicHJvZmlsZUlkIiA6ICI3NTdmOTBiMjIzNDQ0YjhkOGRhYzgyNDIzMmUyY2VjZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfR29sZW0iLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWM2Y2Q3MjAyYzM0ZTc4ZjMwNzMwOTAzNDlmN2Q5NzNiMjg4YWY1ZTViNzMzNGRkMjQ5MDEwYjNmMjcwNzhmOSIKICAgIH0KICB9Cn0="); // MHF_Golem
			put(EntityType.MAGMA_CUBE,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODcxOTQ1MywKICAicHJvZmlsZUlkIiA6ICIwOTcyYmRkMTRiODY0OWZiOWVjY2EzNTNmODQ5MWE1MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfTGF2YVNsaW1lIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q5MGQ2MWU4Y2U5NTExYTBhMmI1ZWEyNzQyY2IxZWYzNjEzMTM4MGVkNDEyOWUxYjE2M2NlOGZmMDAwZGU4ZWEiCiAgICB9CiAgfQp9"); // MHF_LavaSlime
			put(EntityType.MUSHROOM_COW,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODc0NzAzNiwKICAicHJvZmlsZUlkIiA6ICJhNDY4MTdkNjczYzU0ZjNmYjcxMmFmNmIzZmY0N2I5NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfTXVzaHJvb21Db3ciLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTIzY2ZjNTU4MjQ1NGZjZjk5MDZmODQxZmRhMmNjNmFlODk2Y2Y0NTU4MjFjNGFkYTE5OThkZTcwODc3Y2M4NiIKICAgIH0KICB9Cn0="); // MHF_MushroomCow
			put(EntityType.OCELOT,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODc3OTUwMywKICAicHJvZmlsZUlkIiA6ICIxYmVlOWRmNTRmNzE0MmEyYmY1MmQ5Nzk3MGQzZmVhMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfT2NlbG90IiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzExOGI2Yjc5NzgzMzY4ZGZlMDA0Mjk4NTExMGRhMzY2ZjljNzg4YjQ1MDk3YTNlYTZkMGQ5YTc1M2U5ZjQyYzYiCiAgICB9CiAgfQp9"); // MHF_Ocelot
			put(EntityType.PIG,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODgyMDE4NywKICAicHJvZmlsZUlkIiA6ICI4YjU3MDc4YmYxYmQ0NWRmODNjNGQ4OGQxNjc2OGZiZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfUGlnIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E1NjJhMzdiODcxZjk2NGJmYzNlMTMxMWVhNjcyYWFhMDM5ODRhNWRjNDcyMTU0YTM0ZGMyNWFmMTU3ZTM4MmIiCiAgICB9CiAgfQp9"); // MHF_Pig
			// put(EntityType.ZOMBIFIED_PIGLIN,
			// "ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODg1NDMzMiwKICAicHJvZmlsZUlkIiA6ICIxOGEyYmI1MDMzNGE0MDg0OTE4NDJjMzgwMjUxYTI0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfUGlnWm9tYmllIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkxNmQxNjdjNTc0NGVkMTRlYmMwMmY0NDdmMzI2MTQwNTkzNjJiN2QyZWNiODA4ZmYwNjE2NWQyYzM0M2JlZjIiCiAgICB9CiAgfQp9");
			// // MHF_PigZombie
			put(EntityType.SHEEP,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyODk2MzMzMSwKICAicHJvZmlsZUlkIiA6ICJkZmFhZDU1MTRlN2U0NWExYTZmN2M2ZmM1ZWM4MjNhYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU2hlZXAiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NhMzhjY2Y0MTdlOTljYTlkNDdlZWIxNWE4YTMwZWRiMTUwN2FhNTJiNjc4YzIyMGM3MTdjNDc0YWE2ZmUzZSIKICAgIH0KICB9Cn0="); // MHF_Sheep
			put(EntityType.SLIME,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTA0NDM1MiwKICAicHJvZmlsZUlkIiA6ICI4NzBhYmE5MzQwZTg0OGIzODljNTMyZWNlMDBkNjYzMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU2xpbWUiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZjMjdiMDEzZjFiZjMzNDQ4NjllODFlNWM2MTAwMjdiYzQ1ZWM1Yjc5NTE0ZmRjOTZlMDFkZjFiN2UzYTM4NyIKICAgIH0KICB9Cn0="); // MHF_Slime
			put(EntityType.SPIDER,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTA5MTU4MCwKICAicHJvZmlsZUlkIiA6ICI1YWQ1NWYzNDQxYjY0YmQyOWMzMjE4OTgzYzYzNTkzNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3BpZGVyIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y2MWE0OTU0MWE4MzZhYThmNGY3NmUwZDRjYjJmZjA0ODg4YzYyZjk0MTFlYTEwY2JhY2YxZjJhNTQ0MjQyNDAiCiAgICB9CiAgfQp9"); // MHF_Spider
			put(EntityType.SQUID,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTEzOTQwNiwKICAicHJvZmlsZUlkIiA6ICI3MmU2NDY4M2UzMTM0YzM2YTQwOGM2NmI2NGU5NGFmNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfU3F1aWQiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU4OTEwMWQ1Y2M3NGFhNDU4MDIxYTA2MGY2Mjg5YTUxYTM1YTdkMzRkOGNhZGRmYzNjZGYzYjJjOWEwNzFhIgogICAgfQogIH0KfQ=="); // MHF_Squid
			put(EntityType.VILLAGER,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTE3MzU5NSwKICAicHJvZmlsZUlkIiA6ICJiZDQ4MjczOTc2N2M0NWRjYTFmOGMzM2M0MDUzMDk1MiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfVmlsbGFnZXIiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjRiZDgzMjgxM2FjMzhlNjg2NDg5MzhkN2EzMmY2YmEyOTgwMWFhZjMxNzQwNDM2N2YyMTRiNzhiNGQ0NzU0YyIKICAgIH0KICB9Cn0="); // MHF_Villager
			put(EntityType.WITHER_SKELETON,
					"ewogICJ0aW1lc3RhbXAiIDogMTYxMTUyOTE5OTczMywKICAicHJvZmlsZUlkIiA6ICI3ZWQ1NzFhNTlmYjg0MTZjOGI5ZGZiMmY0NDZhYjViMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNSEZfV1NrZWxldG9uIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JhOTZlOWQ3NmJlZDMwMDkwY2U2ZTJkODQyNTk5NjU5NGVlYzZkNjhhYzg4Y2YwNzM1NmU5ODE0ODM0MjQzZWMiCiAgICB9CiAgfQp9"); // MHF_WSkeleton
		}
	};
	private final static Map<EntityType, ItemStack> mobSkulls = new HashMap<EntityType, ItemStack>();

	public EnchantProcessor(final Plugin plugin)
	{
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Language.setDefaultLangMessage("cooldown-error", "&cYou must wait %secs% more seconds to use this again!");
		Language.setDefaultLangMessage("enderbow-radius-error", "&cYou cannot teleport further than %blocks% blocks!");
		EPS.registerReloadable(this);

		if (CustomEnchants.beheadingConfig.isSet("chance"))
		{
			final String s = CustomEnchants.beheadingConfig.getString("chance");
			CustomEnchants.beheadingConfig.set("mob-chance", s);
			CustomEnchants.beheadingConfig.set("player-chance", s);
			CustomEnchants.beheadingConfig.set("chance", null);
			CustomEnchants.beheadingConfig.saveYaml();
		}

		if (Sekai.getMCVersion() > 12)
			for (final Map.Entry<EntityType, String> entry : mobHeads.entrySet())
				mobSkulls.put(entry.getKey(), getCustomSkull(mobHeads.get(entry.getKey()),
						WordUtils.capitalizeFully(entry.getKey().name()) + " Head"));

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(EPS.plugin, new Runnable() {
			public void run()
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					ItemStack itemStack = player.getInventory().getItemInMainHand();
					if (itemStack == null)
						return;

					ItemMeta itemMeta = itemStack.getItemMeta();

					if (itemMeta == null)
						return;

					if (itemMeta.hasEnchant(CustomEnchants.NIGHT_VISION))
					{
						PlayerAttributes.addAttribute(player, CustomEnchants.NIGHT_VISION);
						player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 500, 0));
					} else if (PlayerAttributes.hasAttribute(player, CustomEnchants.NIGHT_VISION))
						player.removePotionEffect(PotionEffectType.NIGHT_VISION);

					if (itemMeta.hasEnchant(CustomEnchants.SPEED))
					{
						PlayerAttributes.addAttribute(player, CustomEnchants.SPEED);
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 500,
								itemMeta.getEnchantLevel(CustomEnchants.SPEED) - 1));
					} else if (PlayerAttributes.hasAttribute(player, CustomEnchants.SPEED))
						player.removePotionEffect(PotionEffectType.SPEED);

					if (itemMeta.hasEnchant(CustomEnchants.JUMP_BOOST))
					{
						PlayerAttributes.addAttribute(player, CustomEnchants.JUMP_BOOST);
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 500,
								itemMeta.getEnchantLevel(CustomEnchants.JUMP_BOOST) - 1));
					} else if (PlayerAttributes.hasAttribute(player, CustomEnchants.JUMP_BOOST))
						player.removePotionEffect(PotionEffectType.JUMP);
				}
			}
		}, 0, 20);

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(final EntityDamageByEntityEvent e)
	{
		Entity entityDamager = e.getDamager();

		if (e.getEntity() instanceof Player)
		{
			final Player p = (Player) e.getEntity();
			final Entity d = entityDamager;
			final World world = p.getWorld();
			final ItemStack item = p.getInventory().getItemInMainHand();
			final ItemMeta meta = item.getItemMeta();

			if (item == null || meta == null)
				return;
			if (meta.hasEnchant(CustomEnchants.RETALIATE))
			{
				final int retaliateLevel = meta.getEnchantLevel(CustomEnchants.RETALIATE);
				final int duration = CustomEnchants.retaliateConfig.getAutofilledInt(retaliateLevel, "duration-seconds")
						* 20;
				p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, retaliateLevel - 1));
			}

			final ItemStack[] armor = { p.getInventory().getHelmet(), p.getInventory().getChestplate(),
					p.getInventory().getLeggings(), p.getInventory().getBoots() };
			if (entityDamager instanceof LivingEntity)
				for (final ItemStack piece : armor)
				{
					if (piece != null)
					{
						final ItemMeta pmeta = piece.getItemMeta();
						if (pmeta.hasEnchant(CustomEnchants.POISONOUS))
						{

							final LivingEntity en = (LivingEntity) entityDamager;
							final int duration = CustomEnchants.poisonousConfig
									.getAutofilledInt(pmeta.getEnchantLevel(CustomEnchants.POISONOUS), "ticks");
							PotionCombiner.lengthenEffect(en, new PotionEffect(PotionEffectType.POISON, duration,
									pmeta.getEnchantLevel(CustomEnchants.POISONOUS) - 1));
						}

						if (pmeta.hasEnchant(CustomEnchants.VOLCANIC))
						{
							final int ticks = CustomEnchants.volcanicConfig
									.getAutofilledInt(pmeta.getEnchantLevel(CustomEnchants.VOLCANIC), "ticks");
							d.setFireTicks(d.getFireTicks() + ticks);
							world.spawnParticle(Particle.LAVA, e.getEntity().getLocation(), 1);
						}

						if (pmeta.hasEnchant(CustomEnchants.SATURATED))
						{
							final int duration = CustomEnchants.saturatedConfig
									.getAutofilledInt(pmeta.getEnchantLevel(CustomEnchants.SATURATED), "ticks");
							PotionCombiner.lengthenEffect(p, new PotionEffect(PotionEffectType.SATURATION, duration,
									pmeta.getEnchantLevel(CustomEnchants.SATURATED) - 1));
						}

						if (pmeta.hasEnchant(CustomEnchants.STIFFEN))
						{
							final double healththreshold = CustomEnchants.stiffenConfig.getAutofilledDouble(
									pmeta.getEnchantLevel(CustomEnchants.STIFFEN), "healththreshold");
							final int amplifier = CustomEnchants.stiffenConfig
									.getAutofilledInt(pmeta.getEnchantLevel(CustomEnchants.STIFFEN), "amplifier") - 1;
							final LivingEntity l = ((LivingEntity) e.getEntity());
							if (l.getHealth() <= healththreshold)
								PotionCombiner.lengthenEffect(l,
										new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, amplifier - 1));
						}
						if (pmeta.hasEnchant(CustomEnchants.EVADE))
						{
							final int evadeLevel = pmeta.getEnchantLevel(CustomEnchants.EVADE);
							if (CustomEnchants.evadeConfig.getBoolean("luckbased"))
								if (getNext() < CustomEnchants.evadeConfig.getAutofilledInt(evadeLevel, "chance"))
								{
									e.setCancelled(true);
								} else if (PlayerStopwatch.incrementValue((Player) e.getEntity(), CustomEnchants.EVADE,
										1) > CustomEnchants.evadeConfig.getAutofilledInt(evadeLevel,
												"hits-to-activate"))
								{
									PlayerStopwatch.log((Player) e.getEntity(), CustomEnchants.EVADE, 0);
									e.setCancelled(true);
								}
						}
					}
				}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(final PlayerMoveEvent e)
	{
		if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY())
			return;
		if (e.isCancelled())
			return;

		final Player p = e.getPlayer();
		final ItemStack[] armor = { p.getInventory().getHelmet(), p.getInventory().getChestplate(),
				p.getInventory().getLeggings(), p.getInventory().getBoots() };
		final ItemStack mainHandItem = e.getPlayer().getInventory().getItemInMainHand();
		int overhealLvl = 0;
		boolean heal = false;
		final boolean hpBoosted = p.hasPotionEffect(PotionEffectType.HEALTH_BOOST);
		for (final ItemStack piece : armor)
		{
			if (piece == null)
				continue;

			final ItemMeta pieceMeta = piece.getItemMeta();
			if (pieceMeta.hasEnchant(CustomEnchants.OVERHEALED))
			{
				if (!hpBoosted)
					overhealLvl++;
				heal = true;
			}
		}

		if (heal)
			p.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, overhealLvl - 1));
		else
			p.removePotionEffect(PotionEffectType.HEALTH_BOOST);

		if (mainHandItem != null && mainHandItem.getItemMeta() != null)
			if (repairDebounce && mainHandItem.getItemMeta().hasEnchant(CustomEnchants.REPAIR))
				new Durability(mainHandItem).incrementDurability(1);

		repairDebounce = !repairDebounce;
	}

	@EventHandler
	public void onKill(final EntityDeathEvent e)
	{
		if (e.getEntity().getKiller() == null)
			return;
		ItemStack itemStack = e.getEntity().getKiller().getInventory().getItemInMainHand();
		if (itemStack == null)
			return;
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null)
			return;
		if (itemMeta.hasEnchant(CustomEnchants.BEHEADING))
		{
			final int enchlvl = itemMeta.getEnchantLevel(CustomEnchants.BEHEADING);
			final double chance = CustomEnchants.beheadingConfig.getAutofilledInt(enchlvl, "mob-chance");
			if (getNext() <= chance)
			{
				final ItemStack head = getHead(e.getEntity());
				if (head != null)
					e.getDrops().add(head);
			}
		}
	}

	@EventHandler
	public void onPlayerKill(final PlayerDeathEvent e)
	{
		if (e.getEntity().getKiller() == null)
			return;
		final ItemStack item = e.getEntity().getKiller().getInventory().getItemInMainHand();
		final ItemMeta meta = item.getItemMeta();
		if (meta != null)
			if (meta.hasEnchant(CustomEnchants.BEHEADING))
			{
				final int enchlvl = meta.getEnchantLevel(CustomEnchants.BEHEADING);
				final double chance = CustomEnchants.beheadingConfig.getAutofilledInt(enchlvl, "player-chance");
				if (getNext() <= chance)
				{
					final ItemStack head = Sekai.getMCVersion() < 13
							? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1)
							: new ItemStack(Material.matchMaterial("PLAYER_HEAD"), 1);
					final SkullMeta skull = (SkullMeta) head.getItemMeta();
					if (Sekai.getMCVersion() < 12)
						skull.setOwner(e.getEntity().getName());
					else
						skull.setOwningPlayer(e.getEntity());
					head.setItemMeta(skull);
					e.getDrops().add(head);
				}
			}
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e)
	{
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			Player player = e.getPlayer();
			final ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
			if (meta == null)
				return;
			if (!player.isSneaking())
				return;
			if (meta.hasEnchant(CustomEnchants.POWERHOUSE))
			{
				final int enchlvl = meta.getEnchantLevel(CustomEnchants.POWERHOUSE);
				final double cooldown = CustomEnchants.powerhouseConfig.getAutofilledDouble(enchlvl, "cooldown-seconds")
						* 1000;

				final long lastuse = PlayerStopwatch.getLastLog(player, CustomEnchants.POWERHOUSE);
				if (lastuse - System.currentTimeMillis() < cooldown)
					player.sendMessage(cooldownError.replaceAll("%secs%",
							Double.toString(Math.floor(((cooldown - lastuse) / 1000) * 10) / 10)));
				else
				{
					PlayerStopwatch.log(player, CustomEnchants.POWERHOUSE, System.currentTimeMillis());
					final int duration = CustomEnchants.powerhouseConfig.getAutofilledInt(enchlvl, "duration-seconds");
					final int amplifier = CustomEnchants.powerhouseConfig.getAutofilledInt(enchlvl, "amplifier") - 1;
					player.addPotionEffect(
							new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration * 20, amplifier));
				}
			}
			if (meta.hasEnchant(CustomEnchants.BACKUP_SPELLS))
			{
				final int enchlvl = meta.getEnchantLevel(CustomEnchants.BACKUP_SPELLS);
				final double cooldown = CustomEnchants.backupSpellsConfig.getAutofilledDouble(enchlvl,
						"cooldown-seconds") * 1000;
				final long lastuse = PlayerStopwatch.getLastLog(player, CustomEnchants.BACKUP_SPELLS);
				if (lastuse - System.currentTimeMillis() < cooldown)
					player.sendMessage(cooldownError.replaceAll("%secs%",
							Double.toString(Math.floor(((cooldown - lastuse) / 1000) * 10) / 10)));
				else
				{
					PlayerStopwatch.log(player, CustomEnchants.BACKUP_SPELLS, System.currentTimeMillis());
					final ItemStack i = new ItemStack(Material.SPLASH_POTION);
					final PotionMeta pmeta = (PotionMeta) i.getItemMeta();
					pmeta.addCustomEffect(new PotionEffect(randomEffect(), 20, 0), true);

					i.setItemMeta(pmeta);
					final ThrownPotion tPotion = player.launchProjectile(ThrownPotion.class);
					tPotion.setItem(i);
				}
			}
			if (meta.hasEnchant(CustomEnchants.BOOSTED))
			{
				final int enchlvl = meta.getEnchantLevel(CustomEnchants.BOOSTED);
				final double cooldown = CustomEnchants.boostedConfig.getAutofilledDouble(enchlvl, "cooldown-seconds")
						* 1000;
				final long lastuse = PlayerStopwatch.getLastLog(player, CustomEnchants.BOOSTED);
				if (lastuse - System.currentTimeMillis() < cooldown)
					player.sendMessage(Language.getLangMessage("cooldown-error").replaceAll("%secs%",
							Double.toString(Math.floor(((cooldown - lastuse) * 0.001) * 10) * 0.1)));
				else
				{
					PlayerStopwatch.log(player, CustomEnchants.BOOSTED, System.currentTimeMillis());
					PlayerAttributes.addAttribute(player, CustomEnchants.BOOSTED);
					Language.sendMessage(player, "boosted-activate");
					EnchantsGUI.get(player).setOpenable(false);
					final int duration = CustomEnchants.boostedConfig.getAutofilledInt(enchlvl, "duration-seconds");
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(EPS.plugin, new Runnable() {
						public void run()
						{
							PlayerAttributes.removeAttribute(player, CustomEnchants.BOOSTED);
						}
					}, (long) (duration * 20));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerItemHeld(final PlayerItemHeldEvent e)
	{
		if (e.isCancelled())
			return;

		final Player player = e.getPlayer();
		final ItemStack itemStack = player.getInventory().getItem(e.getNewSlot());

		if (PlayerAttributes.hasAttribute(player, CustomEnchants.FLY))
		{
			player.setAllowFlight(false);
			player.setFlying(false);
			PlayerAttributes.removeAttribute(player, CustomEnchants.FLY);
		}

		if (itemStack == null)
			return;

		final ItemMeta itemMeta = itemStack.getItemMeta();
		if (CustomEnchants.flyConfig.getBoolean("enabled", false) && itemMeta.hasEnchant(CustomEnchants.FLY))
		{
			player.setAllowFlight(true);
			player.setFlying(true);
			PlayerAttributes.addAttribute(player, CustomEnchants.FLY);
		}
	}

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent e)
	{
		final Player k = e.getEntity().getKiller();
		final ItemStack imh = k == null ? null : k.getInventory().getItemInMainHand();
		final int lvl = imh == null ? 0 : imh.getItemMeta().getEnchantLevel(CustomEnchants.SOUL_DESTRUCTION);
		final List<ItemStack> soulbound = new ArrayList<ItemStack>();
		for (final ItemStack i : e.getDrops())
			if (i.getItemMeta().hasEnchant(CustomEnchants.SOULBOUND))
				if (lvl > 0)
					if (getNext() < CustomEnchants.soulDestructionConfig.getAutofilledDouble(lvl, "chance"))
						continue;
					else
						soulbound.add(i);
				else
					soulbound.add(i);
		e.getDrops().removeAll(soulbound);
		itemsToKeep.put(e.getEntity(), soulbound);
	}

	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent e)
	{
		List<ItemStack> drops = itemsToKeep.get(e.getPlayer());
		if (drops == null)
			return;
		for (ItemStack i : drops)
			e.getPlayer().getInventory().addItem(i);
		itemsToKeep.put(e.getPlayer(), new ArrayList<ItemStack>());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onProjectileHit(final ProjectileHitEvent e)
	{
		if (e.getEntity().isDead())
			return;
		if (!(e.getEntity() instanceof Arrow))
			return;

		final Arrow arrow = (Arrow) e.getEntity();
		if (!(arrow.getShooter() instanceof Player))
			return;

		final Player player = (Player) arrow.getShooter();
		final ItemStack mainitem = player.getInventory().getItemInMainHand();
		final ItemMeta mainmeta = mainitem.getItemMeta();
		final World world = player.getWorld();

		final Material material = mainitem.getType();
		if (!(material.equals(Material.BOW) && !(material.equals(crossbowMaterial))))
			return;

		if (mainmeta.hasEnchant(CustomEnchants.ENDERBOW))
		{
			if (player.isSneaking())
			{
				final double cooldown = CustomEnchants.enderbowConfig.getAutofilledDouble(
						mainmeta.getEnchantLevel(CustomEnchants.ENDERBOW), "cooldown-seconds") * 1000;

				final long lastuse = PlayerStopwatch.getLastLog(player, CustomEnchants.ENDERBOW);
				if (lastuse - System.currentTimeMillis() < cooldown)
					player.sendMessage(Language.getLangMessage("cooldown-error").replaceAll("%secs%",
							Double.toString(Math.floor(((cooldown - lastuse) / 1000) * 10) / 10)));
				else
				{
					final double radius = CustomEnchants.enderbowConfig
							.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.ENDERBOW), "radius");
					final double distance = player.getLocation().distanceSquared(arrow.getLocation());

					// Check if distance from player exceeds limits
					if (distance > radius * radius)
						player.sendMessage(Language.getLangMessage("enderbow-radius-error").replaceAll("%blocks%",
								Double.toString(radius)));
					else
					{
						PlayerStopwatch.log(player, CustomEnchants.ENDERBOW, System.currentTimeMillis());
						player.teleport(arrow.getLocation());
						player.playSound(player.getLocation(), ENTITY_ENDERMAN_TELEPORT, 1, 1);
					}
				}
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.MACHINERY))
		{
			final int shotstoactivate = CustomEnchants.machineryConfig
					.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "shots-to-activate");
			final long shots = PlayerStopwatch.incrementValue(player, CustomEnchants.MACHINERY, 1);
			if (shots >= shotstoactivate)
			{
				PlayerStopwatch.log(player, CustomEnchants.MACHINERY, 0);
				final int radius = CustomEnchants.machineryConfig
						.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "radius");
				final int arrows = CustomEnchants.machineryConfig
						.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.MACHINERY), "arrows");
				final Location loc = arrow.getLocation();
				for (int i = 0; i < arrows; i++)
				{
					final Location location = new Location(world,
							(random.nextDouble() * radius * 2) - radius + loc.getX(), loc.getY() + 48,
							(random.nextDouble() * radius * 2) - radius + loc.getZ());
					world.spawnArrow(location, new Vector(0, -90, 0), 5, 0);
				}
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.THUNDERING_BLOW))
		{
			final int shotstoactivate = CustomEnchants.thunderingBlowConfig
					.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.THUNDERING_BLOW), "shots-to-activate");
			final long shots = PlayerStopwatch.incrementValue(player, CustomEnchants.THUNDERING_BLOW, 1);
			if (shots >= shotstoactivate)
			{
				PlayerStopwatch.log(player, CustomEnchants.THUNDERING_BLOW, 0);
				final Entity entity = e.getHitEntity();

				if (entity != null)
					world.strikeLightning(entity.getLocation());
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.ENERGIZED))
		{
			// Just a temporary name for checking
			final Entity a = e.getHitEntity();

			if (a != null && (a instanceof LivingEntity) && a == arrow.getShooter())
			{
				final LivingEntity entity = (LivingEntity) a;
				final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.ENERGIZED);
				final int speed_amplifier = CustomEnchants.energizedConfig.getAutofilledInt(enchlvl, "speed-amplifier");
				final int speed_duration = CustomEnchants.energizedConfig.getAutofilledInt(enchlvl, "speed-duration");
				final int regeneration_amplifier = CustomEnchants.energizedConfig.getAutofilledInt(enchlvl,
						"regeneration-amplifier");
				final int regeneration_duration = CustomEnchants.energizedConfig.getAutofilledInt(enchlvl,
						"regeneration-duration");

				entity.addPotionEffect(
						new PotionEffect(PotionEffectType.SPEED, speed_duration * 20, speed_amplifier - 1));
				entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, regeneration_duration * 20,
						regeneration_amplifier - 1));
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.SHOCKWAVE))
		{
			final int r = CustomEnchants.shockwaveConfig
					.getAutofilledInt(mainmeta.getEnchantLevel(CustomEnchants.SHOCKWAVE), "radius");
			final double dmg = CustomEnchants.shockwaveConfig
					.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.SHOCKWAVE), "damage");
			final Collection<Entity> list = world.getNearbyEntities(arrow.getLocation(), r, r, r);
			for (final Entity entity : list)
			{
				if (!(entity instanceof Damageable))
					continue;

				final EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.PROJECTILE, 0);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					continue;

				final Damageable le = ((Damageable) entity);
				le.damage(dmg);
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.FIREWORKS))
		{
			final double dmg = CustomEnchants.fireworksConfig
					.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.FIREWORKS), "damage");
			world.createExplosion(arrow.getLocation(), 0F);
			final Collection<Entity> list = world.getNearbyEntities(arrow.getLocation(), 2, 2, 2);
			for (final Entity entity : list)
			{
				if (!(entity instanceof Damageable))
					continue;

				final EntityDamageEvent event = new EntityDamageEvent(entity, DamageCause.PROJECTILE, 0);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					continue;

				final Damageable le = ((Damageable) entity);
				le.damage(dmg);
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.FLAMMABLE))
		{
			final Block block = e.getEntity().getLocation().getBlock();
			final BlockBreakEvent b = new BlockBreakEvent(block, player);
			Bukkit.getPluginManager().callEvent(b);
			if (!b.isCancelled())
				block.setType(Material.FIRE);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent e)
	{
		if (looping)
			return;
		final Player player = e.getPlayer();
		final ItemStack mainhand = player.getInventory().getItemInMainHand();
		if (mainhand == null)
			return;
		if (!mainhand.hasItemMeta())
			return;
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;
		if (player.getInventory().firstEmpty() == -1)
		{
			if (ConfigSettings.isUseActionBar())
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(inventoryFull));
			else
				Language.sendMessage(player, "inventoryfull");
			return;
		}
		if (e.getBlock().getState() instanceof Container)
			return;
		modifiedByEnchant = false;
		final Collection<ItemStack> drops = getDrops(e.getBlock(), mainhand, player);
		final ItemMeta mainmeta = mainhand.getItemMeta();

		if (mainmeta.hasEnchant(CustomEnchants.EXPERIENCE))
		{
			final int exp = e.getExpToDrop();
			if (exp > 0)
			{
				final double multi = CustomEnchants.experienceConfig
						.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.EXPERIENCE), "exp-multi");
				e.setExpToDrop((int) (e.getExpToDrop() * multi));
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.HASTE))
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.HASTE);
			if (getNext() < CustomEnchants.hasteConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				Language.sendMessage(player, "hasteactivate");
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, enchlvl - 1));
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.TOKENBLOCKS))
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.TOKENBLOCKS);
			if (getNext() < CustomEnchants.tokenBlocksConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				final int randomrange = CustomEnchants.tokenBlocksConfig.getAutofilledInt(enchlvl, "random-range");
				final int tokens = CustomEnchants.tokenBlocksConfig.getAutofilledInt(enchlvl, "tokens")
						+ random.nextInt(randomrange * 2) - randomrange;
				final String m = Language.getLangMessage("tokenblocksactivate").replaceAll("%tokens%",
						Integer.toString(tokens));
				if (m != "")
					player.sendMessage(m);
				economy.changeBalance(player, tokens);
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.MONEYBLOCKS))
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.MONEYBLOCKS);
			if (getNext() < CustomEnchants.moneyBlocksConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				if (BuiltInEnchantsLoader.vaultEnabled)
				{
					final int randomrange = CustomEnchants.moneyBlocksConfig.getAutofilledInt(enchlvl, "random-range");
					final int money = CustomEnchants.moneyBlocksConfig.getAutofilledInt(enchlvl, "money")
							+ random.nextInt(randomrange * 2) - randomrange;
					final String m = Language.getLangMessage("moneyblocksactivate").replaceAll("%money%",
							Integer.toString(money));
					if (m != "")
						player.sendMessage(m);
					VaultHook.getEconomy().depositPlayer(player, money);
				} else
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You must have Vault to use money enchants!");
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.CHARITY))
		{
			if (BuiltInEnchantsLoader.vaultEnabled)
			{
				final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.CHARITY);
				if (getNext() < CustomEnchants.charityConfig.getAutofilledDouble(enchlvl, "chance"))
				{
					final int randomrange = CustomEnchants.charityConfig.getAutofilledInt(enchlvl, "random-range");
					final int money = CustomEnchants.charityConfig.getAutofilledInt(enchlvl, "money")
							+ random.nextInt(randomrange * 2) - randomrange;
					final String m = Language.getLangMessage("charityactivate")
							.replaceAll("%money%", Integer.toString(money))
							.replaceAll("%player%", player.getDisplayName());
					if (m != "")
						Bukkit.broadcastMessage(m);
					for (final Player p : Bukkit.getOnlinePlayers())
						VaultHook.getEconomy().depositPlayer(p, money);
				}
			} else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "You must have Vault to use money enchants!");
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.TOKENCHARITY))
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.TOKENCHARITY);
			if (getNext() < CustomEnchants.tokenCharityConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				final int randomrange = CustomEnchants.tokenCharityConfig.getAutofilledInt(enchlvl, "random-range");
				final int tokens = CustomEnchants.tokenCharityConfig.getAutofilledInt(enchlvl, "tokens")
						+ random.nextInt(randomrange * 2) - randomrange;
				final String m = Language.getLangMessage("tokencharityactivate")
						.replaceAll("%tokens%", Integer.toString(tokens))
						.replaceAll("%player%", player.getDisplayName());
				if (m != "")
					Bukkit.broadcastMessage(m);
				for (final Player p : Bukkit.getOnlinePlayers())
					economy.changeBalance(p, tokens);
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.EXPLOSIVE))
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.EXPLOSIVE);

			if (getNext() < CustomEnchants.explosiveConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				final Location loc = e.getBlock().getLocation();
				final int radius = (int) (Math.floor(enchlvl / 2) + 1);
				final List<Block> area = sphere(loc, radius);
				loc.getWorld().createExplosion(loc, 0F);

				for (final Block block : area)
				{
					if (block == e.getBlock() || block.getType() == Material.BEDROCK || block.getType() == Material.AIR
							|| block.getType() == endframe)
						continue;

					looping = true;
					final BlockBreakEvent newevent = new BlockBreakEvent(block, player);
					Bukkit.getPluginManager().callEvent(newevent);
					if (newevent.isCancelled())
						continue;

					player.giveExp(getExp(block));
					drops.addAll(getDrops(block, mainhand, player));
					block.setType(Material.AIR);
				}

				modifiedByEnchant = true;
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.EXCAVATE))
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.EXCAVATE);

			if (getNext() < CustomEnchants.excavateConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				final Location loc = e.getBlock().getLocation();
				final int radius = enchlvl / 2;
				final List<Block> area = new ArrayList<Block>();
				final World world = e.getBlock().getWorld();
				for (int x = -radius; x < radius; x++)
					for (int y = -radius; y < radius; y++)
						for (int z = -radius; z < radius; z++)
							area.add(world.getBlockAt(new Location(world, x + loc.getBlockX(), y + loc.getBlockY(),
									z + loc.getBlockZ())));
				for (final Block block : area)
				{
					if (block == e.getBlock() || block.getType() == Material.BEDROCK || block.getType() == endframe
							|| block.getType() == Material.AIR)
						continue;

					looping = true;
					final BlockBreakEvent newevent = new BlockBreakEvent(block, player);
					Bukkit.getServer().getPluginManager().callEvent(newevent);
					if (newevent.isCancelled())
						continue;

					player.giveExp(getExp(block));
					drops.addAll(getDrops(block, mainhand, player));
					block.setType(Material.AIR);
				}

				modifiedByEnchant = true;
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.DIAMOND))
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.DIAMOND);

			if (getNext() < CustomEnchants.diamondConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				final Location loc = e.getBlock().getLocation();
				final int radius = enchlvl / 2;
				final Block[] area = diamond(loc, radius);
				for (final Block block : area)
				{
					if (block == e.getBlock() || block.getType() == Material.BEDROCK || block.getType() == Material.AIR
							|| block.getType() == endframe)
						continue;

					looping = true;
					final BlockBreakEvent newevent = new BlockBreakEvent(block, player);
					Bukkit.getServer().getPluginManager().callEvent(newevent);
					if (newevent.isCancelled())
						continue;

					player.giveExp(getExp(block));
					drops.addAll(getDrops(block, mainhand, player));
					block.setType(Material.AIR);
				}

				modifiedByEnchant = true;
			}
		}

		if (mainmeta.hasEnchant(CustomEnchants.VEIN_MINER))
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.VEIN_MINER);

			if (getNext() < CustomEnchants.veinMinerConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				final Location loc = e.getBlock().getLocation();
				final int radius = enchlvl / 2;
				final List<Block> area = new ArrayList<Block>();
				final World world = e.getBlock().getWorld();
				for (int x = -radius; x < radius; x++)
					for (int y = -radius; y < radius; y++)
						for (int z = -radius; z < radius; z++)
						{
							final Block block = world.getBlockAt(
									new Location(world, x + loc.getBlockX(), y + loc.getBlockY(), z + loc.getBlockZ()));
							if (block.getType().equals(e.getBlock().getType()))
								area.add(block);
						}
				for (final Block block : area)
				{
					if (block == e.getBlock() || block.getType() == Material.BEDROCK || block.getType() == Material.AIR
							|| block.getType() == endframe)
						continue;

					looping = true;
					final BlockBreakEvent newevent = new BlockBreakEvent(block, player);
					Bukkit.getServer().getPluginManager().callEvent(newevent);
					if (newevent.isCancelled())
						continue;

					player.giveExp(getExp(block));
					drops.addAll(getDrops(block, mainhand, player));
					block.setType(Material.AIR);
				}

				modifiedByEnchant = true;
			}
		}

		looping = false;

		if (mainmeta.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) || mainmeta.hasEnchant(CustomEnchants.AUTOSMELT))
			modifiedByEnchant = true;

		if (modifiedByEnchant)
		{
			e.setDropItems(false);
			final World world = e.getBlock().getWorld();
			final Location loc = e.getBlock().getLocation();

			boolean telepathyEnabled = mainmeta.hasEnchant(CustomEnchants.TELEPATHY)
					&& getNext() < CustomEnchants.telepathyConfig
							.getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.TELEPATHY), "chance");
			for (final ItemStack drop : drops)
				if (!drop.getType().equals(Material.AIR) && !drop.getType().equals(Material.matchMaterial("CAVE_AIR"))
						&& !drop.getType().equals(Material.matchMaterial("VOID_AIR")))
					if (telepathyEnabled && player.getInventory().firstEmpty() != -1)
						if (Sekai.getMCVersion() < 14)
							return;
						else
							e.getPlayer().getInventory().addItem(drop);
					else
						world.dropItemNaturally(loc, drop);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockDropItem(final BlockDropItemEvent e)
	{
		final Player player = e.getPlayer();
		final ItemStack mainhand = player.getInventory().getItemInMainHand();
		if (mainhand == null)
			return;
		if (!mainhand.hasItemMeta())
			return;
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;
		if (e.getBlock().getState() instanceof Container)
			return;
		final ItemMeta mainmeta = mainhand.getItemMeta();

		if (mainmeta.hasEnchant(CustomEnchants.TELEPATHY) && player.getInventory().firstEmpty() != -1)
		{
			final int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.TELEPATHY);
			if (getNext() < CustomEnchants.telepathyConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				for (final Item item : e.getItems())
					e.getPlayer().getInventory().addItem(item.getItemStack());
				e.setCancelled(true);
			}
		}
	}

	public static double getNext()
	{
		return random.nextDouble() * 100;
	}

	private static PotionEffectType randomEffect()
	{
		final int i = random.nextInt(5);
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

	private static ItemStack getHead(final LivingEntity e)
	{
		if (Sekai.getMCVersion() < 12 && e instanceof Skeleton)
			return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1,
					(short) (((Skeleton) e).getSkeletonType() == org.bukkit.entity.Skeleton.SkeletonType.NORMAL ? 0
							: 1));

		switch (e.getType())
		{
		case SKELETON:
			if (Sekai.getMCVersion() > 12)
				return new ItemStack(Material.matchMaterial("SKELETON_SKULL"), 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 0);
		case WITHER_SKELETON:
			if (Sekai.getMCVersion() > 12)
				return new ItemStack(Material.matchMaterial("WITHER_SKELETON_SKULL"), 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 1);
		case ZOMBIE:
			if (Sekai.getMCVersion() > 12)
				return new ItemStack(Material.matchMaterial("ZOMBIE_HEAD"), 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 2);
		case CREEPER:
			if (Sekai.getMCVersion() > 12)
				return new ItemStack(Material.matchMaterial("CREEPER_HEAD"), 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 4);
		case ENDER_DRAGON:
			if (Sekai.getMCVersion() > 12)
				return new ItemStack(Material.matchMaterial("DRAGON_HEAD"), 1);
			else
				return new ItemStack(Material.matchMaterial("SKULL_HEAD"), 1, (short) 5);
		default:
			if (Sekai.getMCVersion() > 12)
				if (mobHeads.containsKey(e.getType()))
					return mobSkulls.get(e.getType());
			return null;
		}
	}

	private static ItemStack getCustomSkull(final String texture, final String name)
	{
		final ItemStack head = Sekai.getMCVersion() < 13 ? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1)
				: new ItemStack(Material.matchMaterial("PLAYER_HEAD"), 1);

		final SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setDisplayName(name);
		final GameProfile profile = new GameProfile(UUID.randomUUID(), null);

		profile.getProperties().put("textures", new Property("textures", texture));

		try
		{
			final Method mtd = skull.getClass().getDeclaredMethod("setProfile", GameProfile.class);
			mtd.setAccessible(true);
			mtd.invoke(skull, profile);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
		{
			ex.printStackTrace();
		}

		head.setItemMeta(skull);
		return head;
	}

	private Collection<ItemStack> getDrops(final Block block, final ItemStack tool, final Player player)
	{
		final ItemMeta itemMeta = tool.getItemMeta();
		final Collection<ItemStack> drops = block.getDrops(tool);
		final int fortuneLevel = itemMeta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
		final int autoSmeltLevel = itemMeta.getEnchantLevel(CustomEnchants.AUTOSMELT);

		if (drops.isEmpty() || drops == null)
			return (new ArrayList<ItemStack>());

		for (final ItemStack drop : drops)
		{
			if (fortuneLevel > 0)
			{
				if (ConfigSettings.getApplyFortuneOn().contains(drop.getType())
						&& !(saves.contains(block.getLocation())))
					drop.setAmount(getDropCount(fortuneLevel, random)
							* (PlayerAttributes.hasAttribute(player, CustomEnchants.BOOSTED) ? 5 : 1));
			}
			if (autoSmeltLevel > 0
					&& getNext() < CustomEnchants.autosmeltConfig.getAutofilledDouble(autoSmeltLevel, "chance"))
			{
				final Material smelted = getSmelted(drop.getType());
				if (smelted != null)
					drop.setType(smelted);
			}

		}
		return drops;
	}

	private List<Block> sphere(final Location center, final int radius)
	{
		final ArrayList<Block> sphere = new ArrayList<Block>();
		for (int Y = -radius; Y < radius; Y++)
		{
			for (int X = -radius; X < radius; X++)
			{
				for (int Z = -radius; Z < radius; Z++)
				{
					if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius)
					{
						final Block block = center.getWorld().getBlockAt(X + center.getBlockX(), Y + center.getBlockY(),
								Z + center.getBlockZ());
						sphere.add(block);
					}
				}
			}
		}
		return sphere;
	}

	public Block[] diamond(final Location center, final int radius)
	{
		final ArrayList<Block> diamond = new ArrayList<Block>();
		for (int Y = -radius; Y < radius + 1; Y++)
		{
			final int r = radius - Math.abs(Y);
			for (int X = -radius; X < radius + 1; X++)
			{
				for (int Z = -radius; Z < radius + 1; Z++)
				{
					if (X > -r && X < r && Z > -r && Z < r)
					{
						if (Math.abs(Y) + 1 != radius)
							if (Math.abs(X) == r - 1 && Math.abs(Z) == r - 1)
								continue;

						final Block block = center.getWorld().getBlockAt(X + center.getBlockX(), Y + center.getBlockY(),
								Z + center.getBlockZ());
						diamond.add(block);
					}
				}
			}
		}
		return diamond.toArray(new Block[diamond.size()]);
	}

	private int getDropCount(final int i, final Random random)
	{
		final int j = random.nextInt(i + 2) - 1;
		return j < 0 ? 0 : j + 1;
	}

	@EventHandler
	private void onBlockPlace(final BlockPlaceEvent e)
	{
		saves.add(e.getBlock().getLocation());
	}

	private int getExp(final Block block)
	{
		switch (block.getType())
		{
		case COAL_ORE:
			return random.nextInt(3);
		case REDSTONE_ORE:
			return random.nextInt(4) + 1;
		case LAPIS_ORE:
			return random.nextInt(4) + 2;
		case DIAMOND_ORE:
			return random.nextInt(5) + 3;
		case EMERALD_ORE:
			return random.nextInt(5) + 3;
		default:
			return 0;
		}
	}

	private Material getSmelted(final Material material)
	{
		if (smeltResults.containsKey(material))
			return smeltResults.get(material);

		Material result = null;
		final Iterator<Recipe> iter = Bukkit.recipeIterator();
		while (iter.hasNext())
		{
			final Recipe recipe = iter.next();
			if (!(recipe instanceof FurnaceRecipe))
				continue;
			if (((FurnaceRecipe) recipe).getInput().getType() != material)
				continue;
			result = recipe.getResult().getType();
			break;
		}
		smeltResults.put(material, result);
		return result;
	}

	@Override
	public void reload()
	{
		CustomEnchants.flyConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.repairConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.experienceConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.soulDestructionConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.jaggedConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.lifeStealConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.momentumConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.poisonousConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.volcanicConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.saturatedConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.insatiableConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.lastResortConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.retaliateConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.beheadingConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.powerhouseConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.meltingConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.backupSpellsConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.overhealedConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.evadeConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.enderbowConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.machineryConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.thunderingBlowConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.energizedConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.shockwaveConfig.loadYaml(new YamlConfiguration());
		CustomEnchants.fireworksConfig.loadYaml(new YamlConfiguration());
		inventoryFull = Language.getLangMessage("inventoryfull");
		cooldownError = Language.getLangMessage("cooldown-error");
		itemsToKeep.clear();
	}
}

class PotionCombiner
{

	public static PotionEffect getEffect(final LivingEntity entity, final PotionEffectType potionEffectType)
	{
		for (PotionEffect potionEffect : entity.getActivePotionEffects())
			if (potionEffect.getType().equals(potionEffectType))
				return potionEffect;
		return null;
	}

	public static void lengthenEffect(final LivingEntity entity, final PotionEffect potionEffect)
	{
		final PotionEffect a = getEffect(entity, potionEffect.getType());
		if (a == null)
		{
			potionEffect.apply(entity);
			return;
		}

		entity.addPotionEffect(new PotionEffect(potionEffect.getType(), potionEffect.getDuration() + a.getDuration(),
				potionEffect.getAmplifier()));
	}

	public static void amplifyEffect(final LivingEntity entity, final PotionEffect potionEffect)
	{
		final PotionEffect a = getEffect(entity, potionEffect.getType());
		if (a == null)
		{
			potionEffect.apply(entity);
			return;
		}

		entity.addPotionEffect(new PotionEffect(potionEffect.getType(), potionEffect.getDuration(),
				potionEffect.getAmplifier() + a.getAmplifier()));
	}
}