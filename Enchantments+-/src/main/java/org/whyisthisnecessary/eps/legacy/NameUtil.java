package org.whyisthisnecessary.eps.legacy;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

/** Deprecated. Use EPS.getDictionary() instead.
 * 
 * @author TreuGames
 *
 */
@Deprecated
public class NameUtil {
	
	public static Enchantment getByName(String name)
	{
		if (LegacyUtil.isLegacy())
			return getEnchantBukkit(getEnchantNameBukkit(name));
		else
			return Enchantment.getByKey(NamespacedKey.minecraft(name));
	}
	
	public static String getName(Enchantment enchant)
	{
		if (LegacyUtil.isLegacy())
			return getEnchantNameMinecraft(enchant.getName());
		else
			return enchant.getKey().getKey();
	}
	
	public static String getMaterialName(Material m)
	{
		if (LegacyUtil.isLegacy())
			return m.name();
		else
			return m.getKey().getKey();
	}
	
	public static String getEnchantNameMinecraft(String name)
	{
		switch (name.toUpperCase())
		{
		    case ("PROTECTION_ENVIRONMENTAL"):
		    	return "protection";
		    case ("PROTECTION_FIRE"):
		    	return "fire_protection";
		    case ("PROTECTION_FALL"):
		    	return "feather_falling";
		    case ("PROTECTION_EXPLOSIONS"):
		    	return "blast_protection";
		    case ("PROTECTION_PROJECTILE"):
		    	return "projectile_projection";
		    case ("OXYGEN"):
		    	return "respiration";
		    case ("WATER_WORKER"):
		    	return "aqua_affinity";
		    case ("THORNS"):
		    	return "thorns";
		    case ("DEPTH_STRIDER"):
		    	return "depth_strider";
		    case ("FROST_WALKER"):
		    	return "frost_walker";
		    case ("DAMAGE_ALL"):
		    	return "sharpness";
		    case ("DAMAGE_UNDEAD"):
		    	return "smite";
		    case ("DAMAGE_ARTHROPODS"):
		    	return "bane_of_arthropods";
		    case ("KNOCKBACK"):
		    	return "knockback";
		    case ("FIRE_ASPECT"):
		    	return "fire_aspect";
		    case ("LOOT_BONUS_MOBS"):
		    	return "looting";
		    case ("DIG_SPEED"):
		        return "efficiency";
		    case ("SILK_TOUCH"):
		    	return "silk_touch";
		    case ("DURABILITY"):
		    	return "unbreaking";
		    case ("LOOT_BONUS_BLOCKS"):
		    	return "fortune";
		    case ("ARROW_DAMAGE"):
		        return "power";
		    case ("ARROW_KNOCKBACK"):
		    	return "punch";
		    case ("ARROW_FIRE"):
		    	return "flame";
		    case ("ARROW_INFINITE"):
		    	return "infinity";
		    case ("LUCK"):
		    	return "luck_of_the_sea";
		    case ("LURE"):
		    	return "lure";
		    case ("MENDING"):
		    	return "mending";
		    default:
		    	return name.toLowerCase();
		}
	}
	
	public static String getEnchantNameBukkit(String name)
	{
		switch (name.toLowerCase())
		{
			case ("protection"):
			    return ("PROTECTION_ENVIRONMENTAL");
			case ("fire_protection"):
			    return ("PROTECTION_FIRE");
			case ("feather_falling"):
			    return ("PROTECTION_FALL");
			case ("blast_protection"):
			    return ("PROTECTION_EXPLOSIONS");
			case ("projectile_projection"):
			    return ("PROTECTION_PROJECTILE");
			case ("respiration"):
			    return ("OXYGEN");
			case ("aqua_affinity"):
			    return ("WATER_WORKER");
			case ("thorns"):
			    return ("THORNS");
			case ("depth_strider"):
			    return ("DEPTH_STRIDER");
			case ("frost_walker"):
			    return ("FROST_WALKER");
			case ("sharpness"):
			    return ("DAMAGE_ALL");
			case ("smite"):
			    return ("DAMAGE_UNDEAD");
			case ("bane_of_arthropods"):
			    return ("DAMAGE_ARTHROPODS");
			case ("knockback"):
			    return ("KNOCKBACK");
			case ("fire_aspect"):
			    return ("FIRE_ASPECT");
			case ("looting"):
			    return ("LOOT_BONUS_MOBS");
			case ("efficiency"):
			    return ("DIG_SPEED");
			case ("silk_touch"):
			    return ("SILK_TOUCH");
			case ("unbreaking"):
			    return ("DURABILITY");
			case ("fortune"):
			    return ("LOOT_BONUS_BLOCKS");
			case ("power"):
			    return ("ARROW_DAMAGE");
			case ("punch"):
			    return ("ARROW_KNOCKBACK");
			case ("flame"):
			    return ("ARROW_FIRE");
			case ("infinity"):
			    return ("ARROW_INFINITE");
			case ("luck_of_the_sea"):
			    return ("LUCK");
			case ("lure"):
			    return ("LURE");
			case ("mending"):
			    return ("MENDING");
		    default:
		    	return name.toUpperCase();
		}
	}
	
	public static Enchantment getEnchantBukkit(String name)
	{
		switch (name.toUpperCase())
		{
		    case ("PROTECTION_ENVIRONMENTAL"):
		    	return Enchantment.PROTECTION_ENVIRONMENTAL;
		    case ("PROTECTION_FIRE"):
		    	return Enchantment.PROTECTION_FIRE;
		    case ("PROTECTION_FALL"):
		    	return Enchantment.PROTECTION_FALL;
		    case ("PROTECTION_EXPLOSIONS"):
		    	return Enchantment.PROTECTION_EXPLOSIONS;
		    case ("PROTECTION_PROJECTILE"):
		    	return Enchantment.PROTECTION_PROJECTILE;
		    case ("OXYGEN"):
		    	return Enchantment.OXYGEN;
		    case ("WATER_WORKER"):
		    	return Enchantment.WATER_WORKER;
		    case ("THORNS"):
		    	return Enchantment.THORNS;
		    case ("DEPTH_STRIDER"):
		    	return Enchantment.DEPTH_STRIDER;
		    case ("FROST_WALKER"):
		    	return Enchantment.FROST_WALKER;
		    case ("DAMAGE_ALL"):
		    	return Enchantment.DAMAGE_ALL;
		    case ("DAMAGE_UNDEAD"):
		    	return Enchantment.DAMAGE_UNDEAD;
		    case ("DAMAGE_ARTHROPODS"):
		    	return Enchantment.DAMAGE_ARTHROPODS;
		    case ("KNOCKBACK"):
		    	return Enchantment.KNOCKBACK;
		    case ("FIRE_ASPECT"):
		    	return Enchantment.FIRE_ASPECT;
		    case ("LOOT_BONUS_MOBS"):
		    	return Enchantment.LOOT_BONUS_MOBS;
		    case ("DIG_SPEED"):
		        return Enchantment.DIG_SPEED;
		    case ("SILK_TOUCH"):
		    	return Enchantment.SILK_TOUCH;
		    case ("DURABILITY"):
		    	return Enchantment.DURABILITY;
		    case ("LOOT_BONUS_BLOCKS"):
		    	return Enchantment.LOOT_BONUS_BLOCKS;
		    case ("ARROW_DAMAGE"):
		        return Enchantment.ARROW_DAMAGE;
		    case ("ARROW_KNOCKBACK"):
		    	return Enchantment.ARROW_KNOCKBACK;
		    case ("ARROW_FIRE"):
		    	return Enchantment.ARROW_FIRE;
		    case ("ARROW_INFINITE"):
		    	return Enchantment.ARROW_INFINITE;
		    case ("LUCK"):
		    	return Enchantment.LUCK;
		    case ("LURE"):
		    	return Enchantment.LURE;
		    case ("MENDING"):
		    	return Enchantment.MENDING;
		    default:
		    	return Label.getEnchant(name);
		}
	}

	public static String getEnchantNameMinecraft(Enchantment enchant) {
		if (LegacyUtil.isLegacy())
			return getEnchantNameMinecraft(enchant.getName());
		else
			return enchant.getKey().getKey();
	}

	public static Enchantment getByName(NamespacedKey key) {
		if (LegacyUtil.isLegacy())
			return getByName(key.getKey());
		else
			return Enchantment.getByKey(key);
	}
}
