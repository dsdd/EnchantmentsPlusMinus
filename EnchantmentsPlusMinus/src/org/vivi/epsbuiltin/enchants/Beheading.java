package org.vivi.epsbuiltin.enchants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.vivi.eps.api.EnchantAction;
import org.vivi.eps.api.EnchantAction.Priority;
import org.vivi.sekai.Sekai;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.vivi.eps.api.EnchantHandler;

public class Beheading extends EnchantHandler
{
	private static final Random RANDOM = new Random();
	private static final Map<EntityType, String> mobHeadAddresses = new HashMap<EntityType, String>() {
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
	private final static Map<EntityType, ItemStack> mobHeads = new HashMap<EntityType, ItemStack>();

	public Beheading()
	{
		if (Sekai.getMCVersion() > 12)
			for (final Map.Entry<EntityType, String> entry : mobHeadAddresses.entrySet())
				mobHeads.put(entry.getKey(), getCustomSkull(mobHeadAddresses.get(entry.getKey()),
						WordUtils.capitalizeFully(entry.getKey().name()) + " Head"));
	}

	@Override
	public Enchantment getEnchant()
	{
		return CustomEnchants.BEHEADING;
	}

	@Override
	public Priority getPriority()
	{
		return Priority.NORMAL;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void entityKill(EnchantAction.EntityKill action)
	{
		double chance = CustomEnchants.beheadingConfig.getAutofilledInt(action.getEnchantLevel(), "mob-chance");
		if (RANDOM.nextDouble() * 100 <= chance)
		{
			LivingEntity entity = action.getEntity();
			ItemStack headItemStack = entity instanceof Player ? (Sekai.getMCVersion() < 13
						? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1)
						: new ItemStack(Material.matchMaterial("PLAYER_HEAD"), 1)) : getHead(entity);
			
			if (entity instanceof Player)
			{
				SkullMeta skullMeta = (SkullMeta) headItemStack.getItemMeta();
				if (Sekai.getMCVersion() < 12)
					skullMeta.setOwner(entity.getName());
				else
					skullMeta.setOwningPlayer((Player) entity);
				headItemStack.setItemMeta(skullMeta);
			}
			
			if (headItemStack != null)
				action.getDrops().add(headItemStack);
		}
			
	}

	@SuppressWarnings("deprecation")
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
				if (mobHeadAddresses.containsKey(e.getType()))
					return mobHeads.get(e.getType());
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
}
