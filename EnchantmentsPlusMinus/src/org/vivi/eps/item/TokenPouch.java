package org.vivi.eps.item;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.util.Language;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.md_5.bungee.api.ChatColor;

public class TokenPouch extends ItemStack {

	private static String pouchHeadID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI4NWY5OWFmN2M1MDczMWRiMGY5MzcwOGMyNDg5OTI0NzdjOTBiNGQyY2Q0ZDE1Zjc3NDU3YzdmNDk5NzBlOSJ9fX0=";
	private static SkullMeta pouchSkullMeta = getCustomSkullMeta(pouchHeadID);
	
	public TokenPouch(int tokens)
	{
		super(EPS.onLegacy() ? Material.matchMaterial("SKULL_ITEM") : Material.PLAYER_HEAD, 1);
		SkullMeta meta = pouchSkullMeta;
		meta.setDisplayName(Language.getLangMessage("token-pouch", false));
		meta.setLore(Arrays.asList(new String[] {
				ChatColor.BLACK+"T:"+Integer.toString(tokens),
				Language.getLangMessage("token-pouch-lore-1", false).replaceAll("%tokens%", Integer.toString(tokens)),
				Language.getLangMessage("token-pouch-lore-2", false)
		}));
		this.setItemMeta(meta);
	}
	
	// �\_(^v^)_/�
	private static SkullMeta getCustomSkullMeta(String texture) {
		ItemStack head = EPS.onLegacy() ? new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1) : new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);

		profile.getProperties().put("textures", new Property("textures", texture));

		try {
			Method mtd = skull.getClass().getDeclaredMethod("setProfile", GameProfile.class);
			mtd.setAccessible(true);
			mtd.invoke(skull, profile);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		return skull;
	}
}
