package org.vivi.sekai.enchantment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.enchantments.Enchantment;
import org.vivi.sekai.Sekai;

@SuppressWarnings("deprecation")
public class EnchantmentInfo
{
	private static final List<EnchantmentInfo> enchantmentInfos = new ArrayList<EnchantmentInfo>();
	public final Enchantment enchantment;
	public String key;
	public String defaultName;
	public String name;
	public String defaultDescription;
	public String description;

	static
	{
		getEnchantmentInfo(Enchantment.PROTECTION_ENVIRONMENTAL).key("protection").defaultName("Protection")
				.defaultDescription("Reduces most types of damage by 4% for each level.");

		getEnchantmentInfo(Enchantment.PROTECTION_FIRE).key("fire_protection").defaultName("Fire Protection")
				.defaultDescription("Reduces fire damage and burn time. Mutually exclusive with other protections.");

		getEnchantmentInfo(Enchantment.PROTECTION_FALL).key("feather_falling").defaultName("Feather Falling")
				.defaultDescription("Reduces fall damage.");

		getEnchantmentInfo(Enchantment.PROTECTION_EXPLOSIONS).key("blast_protection").defaultName("Blast Protection")
				.defaultDescription("Reduces explosion damage and knockback.");

		getEnchantmentInfo(Enchantment.PROTECTION_PROJECTILE).key("projectile_projection")
				.defaultName("Projectile Protection").defaultDescription(
						"Reduces projectile damage such as damage from arrows, thrown tridents, ghast and blaze fireballs, etc.");

		getEnchantmentInfo(Enchantment.OXYGEN).key("respiration").defaultName("Respiration")
				.defaultDescription("Extends underwater breathing time. Stacks with a Turtle Shell.");

		getEnchantmentInfo(Enchantment.WATER_WORKER).key("aqua_affinity").defaultName("Aqua Affinity")
				.defaultDescription("Increases underwater mining speed.");

		getEnchantmentInfo(Enchantment.THORNS).key("thorns").defaultName("Thorns").defaultDescription(
				"Reflects some of the damage taken when hit, at the cost of reducing durability with each proc.");

		getEnchantmentInfo(Enchantment.DEPTH_STRIDER).key("depth_strider").defaultName("Depth Strider")
				.defaultDescription("Increases underwater movement speed.");

		getEnchantmentInfo(Enchantment.DAMAGE_ALL).key("sharpness").defaultName("Sharpness")
				.defaultDescription("Increases damage for melee weapons.");

		getEnchantmentInfo(Enchantment.DAMAGE_UNDEAD).key("smite").defaultName("Smite")
				.defaultDescription("Increases damage to undead mobs.");

		getEnchantmentInfo(Enchantment.DAMAGE_ARTHROPODS).key("bane_of_arthropods").defaultName("Bane of Arthropods")
				.defaultDescription("Increases damage and applies Slowness IV to arthropod mobs.");

		getEnchantmentInfo(Enchantment.KNOCKBACK).key("knockback").defaultName("Knockback")
				.defaultDescription("Increases knockback.");

		getEnchantmentInfo(Enchantment.FIRE_ASPECT).key("fire_aspect").defaultName("Fire Aspect")
				.defaultDescription("Sets target on fire.");

		getEnchantmentInfo(Enchantment.LOOT_BONUS_MOBS).key("looting").defaultName("Looting")
				.defaultDescription("Increases mob loot. Higher levels increase loot dropped.");

		getEnchantmentInfo(Enchantment.DIG_SPEED).key("efficiency").defaultName("Efficiency")
				.defaultDescription("Increases mining speed.");

		getEnchantmentInfo(Enchantment.SILK_TOUCH).key("silk_touch").defaultName("Silk Touch")
				.defaultDescription("Mined blocks drop themselves, with a few exceptions.");

		getEnchantmentInfo(Enchantment.DURABILITY).key("unbreaking").defaultName("Unbreaking")
				.defaultDescription("Increases item durability. Higher levels increase durability further.");

		getEnchantmentInfo(Enchantment.LOOT_BONUS_BLOCKS).key("fortune").defaultName("Fortune")
				.defaultDescription("Increases certain block drops. Higher levels increase chances.");

		getEnchantmentInfo(Enchantment.ARROW_DAMAGE).key("power").defaultName("Power")
				.defaultDescription("Increases arrow damage.");

		getEnchantmentInfo(Enchantment.ARROW_KNOCKBACK).key("punch").defaultName("Punch")
				.defaultDescription("Increases arrow knockback.");

		getEnchantmentInfo(Enchantment.ARROW_FIRE).key("flame").defaultName("Flame")
				.defaultDescription("Arrows set the target on fire, and ignite TNT if hit.");

		getEnchantmentInfo(Enchantment.ARROW_INFINITE).key("infinity").defaultName("Infinity").defaultDescription(
				"Shooting consumes no regular arrows. Does not include Tipped Arrows or Spectral Arrows.");

		getEnchantmentInfo(Enchantment.LUCK).key("luck_of_the_sea").defaultName("Luck of the Sea").defaultDescription(
				"Increases rate of good loot (enchanting books, etc.). Higher levels increase chance.");

		getEnchantmentInfo(Enchantment.LURE).key("lure").defaultName("Lure").defaultDescription(
				"Decreases wait time until fish/junk/loot \\\"bites\\\". Higher Levels increase speed.");

		if (Sekai.getMCVersion() > 8)
		{
			getEnchantmentInfo(Enchantment.FROST_WALKER).key("frost_walker").defaultName("Frost Walker")
					.defaultDescription(
							"Turns water beneath the player into frosted ice and prevents the damage the player would take from standing on magma blocks.");

			getEnchantmentInfo(Enchantment.MENDING).key("mending").defaultName("Mending")
					.defaultDescription("Repair the item while gaining XP orbs.");
		}

		if (Sekai.getMCVersion() > 10)
		{
			getEnchantmentInfo(Enchantment.BINDING_CURSE).key("curse_of_binding").defaultName("Curse of Binding")
					.defaultDescription("Items cannot be removed from armor slots, except due to death or breaking.");

			getEnchantmentInfo(Enchantment.VANISHING_CURSE).key("curse_of_vanishing").defaultName("Curse of Vanishing")
					.defaultDescription("Item is destroyed on death.");

			EnchantmentInfo sweepingEdgeInfo = getEnchantmentInfo(Enchantment.getByName("SWEEPING_EDGE"));
			if (sweepingEdgeInfo != null)
				sweepingEdgeInfo.key("sweeping_edge").defaultName("Sweeping Edge")
						.defaultDescription("Increases sweeping attack damage.");

		}

		if (Sekai.getMCVersion() > 12)
		{
			getEnchantmentInfo(Enchantment.CHANNELING).key("channeling").defaultName("Channeling").defaultDescription(
					"Trident \"channels\" a bolt of lightning toward a hit entity. Functions only during thunderstorms and if target is unobstructed with opaque blocks.");

			getEnchantmentInfo(Enchantment.IMPALING).key("impaling").defaultName("Impaling")
					.defaultDescription("Trident deals additional damage to mobs that spawn naturally in the ocean.");

			getEnchantmentInfo(Enchantment.LOYALTY).key("loyalty").defaultName("Loyalty")
					.defaultDescription("Trident returns after being thrown. Higher levels reduce return time.");

			getEnchantmentInfo(Enchantment.RIPTIDE).key("riptide").defaultName("Riptide").defaultDescription(
					"Trident launches player with itself when thrown. Functions only in water or rain.");
		}

		if (Sekai.getMCVersion() > 13)
		{
			getEnchantmentInfo(Enchantment.MULTISHOT).key("multishot").defaultName("Multishot")
					.defaultDescription("Shoot 3 arrows at the cost of one; only one arrow can be recovered.");

			getEnchantmentInfo(Enchantment.PIERCING).key("piercing").defaultName("Piercing")
					.defaultDescription("Arrows pass through multiple entities. Only available to the Crossbow.");

			getEnchantmentInfo(Enchantment.QUICK_CHARGE).key("quick_charge").defaultName("Quick Charge")
					.defaultDescription("Decreases crossbow charging time.");
		}

		if (Sekai.getMCVersion() > 15)
		{
			getEnchantmentInfo(Enchantment.SOUL_SPEED).key("soul_speed").defaultName("Soul Speed").defaultDescription(
					"Increases walking speed on Soul Sand or Soul Soil, but damages the boots over time.");
		}

	}

	private EnchantmentInfo(Enchantment enchantment)
	{
		this.enchantment = enchantment;
		key = Sekai.getMCVersion() < 13 ? enchantment.getName().toLowerCase() : enchantment.getKey().getKey();
		defaultName = WordUtils.capitalizeFully(key.replaceAll("_", " "));
		defaultDescription = "<No description>";
		enchantmentInfos.add(this);
	}

	public EnchantmentInfo key(String key)
	{
		this.key = key;
		return this;
	}

	public EnchantmentInfo name(String name)
	{
		this.name = name;
		return this;
	}

	public EnchantmentInfo description(String description)
	{
		this.description = description;
		return this;
	}

	public EnchantmentInfo defaultName(String defaultName)
	{
		this.defaultName = defaultName;
		return this;
	}

	public EnchantmentInfo defaultDescription(String defaultDescription)
	{
		this.defaultDescription = defaultDescription;
		return this;
	}
	
	public String getName()
	{
		return name == null ? defaultName : name;
	}
	
	public String getDescription()
	{
		return description == null ? defaultDescription : description;
	}

	public static EnchantmentInfo getEnchantmentInfo(Enchantment enchantment)
	{
		for (EnchantmentInfo enchantmentInfo : enchantmentInfos)
			if (enchantmentInfo.enchantment.equals(enchantment))
				return enchantmentInfo;
		return new EnchantmentInfo(enchantment);
	}

	public static String getKey(Enchantment enchantment)
	{
		return getEnchantmentInfo(enchantment).key;
	}

	public static String getName(Enchantment enchantment)
	{
		return getEnchantmentInfo(enchantment).getName();
	}

	public static String getDescription(Enchantment enchantment)
	{
		return getEnchantmentInfo(enchantment).getDescription();
	}

	/**
	 * Finds the requested {@code Enchantment} by the specified key.
	 * 
	 * @param key Key to search with
	 * @return Requested {@code Enchantment}
	 */
	public static Enchantment getEnchantByKey(String key)
	{
		for (EnchantmentInfo enchantmentInfo : enchantmentInfos)
			if (enchantmentInfo.key.equalsIgnoreCase(key))
				return enchantmentInfo.enchantment;
		return null;
	}

	/**
	 * Finds the requested {@code Enchantment} by the specified name.
	 * 
	 * @param name Name to search with
	 * @return Requested {@code Enchantment}
	 */
	public static Enchantment getEnchantByName(String name)
	{
		for (EnchantmentInfo enchantmentInfo : enchantmentInfos)
			if (enchantmentInfo.defaultName.equalsIgnoreCase(name)
					|| (enchantmentInfo.name != null && enchantmentInfo.name.equalsIgnoreCase(name)))
				return enchantmentInfo.enchantment;
		return null;
	}

}
