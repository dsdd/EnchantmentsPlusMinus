package org.vivi.eps.util;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.EPS;

public interface Dictionary {

	/** Gets the default description from the Minecraft Wiki of the specified enchant.
	 * 
	 * @param enchant The enchant to get the default description from
	 * @return The default description from the Minecraft Wiki of the specified enchant.
	 */
	public String getDefaultDescription(Enchantment enchant);
	
	/** Gets the default cost of the specified enchant.
	 * 
	 * @param enchant The enchant to get the default cost from
	 * @return The default cost of the specified enchant.
	 */
	public int getDefaultCost(Enchantment enchant);
	
	/** Gets the name of the specified enchant (the namespacedkey name of the enchant).
	 * 
	 * @param enchant The enchant to get its name from
	 * @return The name of the specified enchant (the namespacedkey name of the enchant).
	 */
	public String getName(Enchantment enchant);
	
	/** Gets an Enchantment with the specified name.
	 * 
	 * @param enchant_name The enchant name to search the enchant with
	 * @return An Enchantment with the specified name.
	 */
	public Enchantment findEnchant(String enchant_name);
	
	@SuppressWarnings("deprecation")
	class Defaults implements Dictionary
	{

		/** Gets the default description from the Minecraft Wiki of the specified enchant.
		 * If there is no description for the specified enchant, return "null".
		 * 
		 * @param enchant The enchant to get the default description from
		 * @return The default description from the Minecraft Wiki of the specified enchant.
		 */
		@Override
		public String getDefaultDescription(Enchantment enchant) {
			String e = getName(enchant);
			switch (e.toLowerCase())
			{
				case "aqua_affinity":
					return "Increases underwater mining speed.";
				case "bane_of_arthropods":
					return "Increases damage and applies Slowness IV to arthropod mobs.";
				case "blast_protection":
					return "Reduces explosion damage and knockback.";
				case "channeling":
					return "Trident \"channels\" a bolt of lightning toward a hit entity. Functions only during thunderstorms and if target is unobstructed with opaque blocks.";
				case "cleaving":
					return "Increases damage and shield stunning.";
				case "curse_of_binding":
					return "Items cannot be removed from armor slots, except in Creative mode or due to death or breaking.";
				case "curse_of_vanishing":
					return "Item destroyed on death.";
				case "depth_strider":
					return "Increases underwater movement speed.";
				case "efficiency":
					return "Increases mining speed. When applied to an Axe it increases the chance that the axe may stun a shield, with the base chance being 25% and a 5% increase for each level of efficiency.";
				case "feather_falling":
					return "Reduces fall damage.";
				case "fire_aspect":
					return "Sets target on fire.";
				case "fire_protection":
					return "Reduces fire damage and burn time. Mutually exclusive with other protections.";
				case "flame":
					return "Arrows set the target on fire, and ignite TNT if hit.";
				case "fortune":
					return "Increases certain block drops. Higher levels increase chances.";
				case "frost_walker":
					return "Turns water beneath the player into frosted ice and prevents the damage the player would take from standing on magma blocks.";
				case "impaling":
					return "Trident deals additional damage to mobs that spawn naturally in the ocean. In Bedrock Edition, having impaling on a trident also deals extra damage to players or mobs in water or rain.";
				case "infinity":
					return "Shooting consumes no regular arrows. Does not include Tipped Arrows or Spectral Arrows.";
				case "knockback":
					return "Increases knockback.";
				case "looting":
					return "Increases mob loot. Higher levels increase loot dropped.";
				case "loyalty":
					return "Trident returns after being thrown. Higher levels reduce return time.";
				case "luck_of_the_sea":
					return "Increases rate of good loot (enchanting books, etc.). Higher Levels Increase chance.";
				case "lure":
					return "Decreases wait time until fish/junk/loot \"bites\". Higher Levels increase speed.";
				case "mending":
					return "Repair the item while gaining XP orbs.";
				case "multishot":
					return "Shoot 3 arrows at the cost of one; only one arrow can be recovered.";
				case "piercing":
					return "Arrows pass through multiple entities. Only available to the Crossbow.";
				case "power":
					return "Increases arrow damage.";
				case "projectile_protection":
					return "Reduces projectile damage such as damage from arrows, thrown tridents, ghast and blaze fireballs, etc.";
				case "protection":
					return "Reduces most types of damage by 4% for each level.";
				case "punch":
					return "Increases arrow knockback.";
				case "quick_charge":
					return "Decreases crossbow charging time.";
				case "respiration":
					return "Extends underwater breathing time. Stacks with a Turtle Shell";
				case "riptide":
					return "Trident launches player with itself when thrown. Functions only in water or rain.";
				case "sharpness":
					return "Increases damage for melee weapons.";
				case "silk_touch":
					return "Mined blocks drop themselves, with a few exceptions.";
				case "smite":
					return "Increases damage to undead mobs.";
				case "soul_speed":
					return "Increases walking speed on soul sand or Soul Soil, but damages the Boots overtime";
				case "sweeping_edge":
					return "Increases sweeping attack damage.";
				case "thorns":
					return "Reflects some of the damage taken when hit, at the cost of reducing durability with each proc.";
				case "unbreaking":
					return "Increases item durability. Higher levels increase durability further. Recommended for Gold and Iron equipment but not as much for Diamond or Netherite.";
				default:
					return "null";
			}
		}

		@Override
		public int getDefaultCost(Enchantment enchant) {
			return enchant.getMaxLevel() == 32767 ? 400 : 600/enchant.getMaxLevel();
		}

		@Override
		public String getName(Enchantment enchant)
		{
			if (Enchantment.PROTECTION_ENVIRONMENTAL == enchant)
				return "protection";
			else if (Enchantment.PROTECTION_FIRE == enchant)
				return "fire_protection";
			else if (Enchantment.PROTECTION_FALL == enchant)
				return "feather_falling";
			else if (Enchantment.PROTECTION_EXPLOSIONS == enchant)
				return "blast_protection";
			else if (Enchantment.PROTECTION_PROJECTILE == enchant)
				return "projectile_projection";
			else if (Enchantment.OXYGEN == enchant)
				return "respiration";
			else if (Enchantment.WATER_WORKER == enchant)
				return "aqua_affinity";
			else if (Enchantment.THORNS == enchant)
				return "thorns";
			else if (Enchantment.DEPTH_STRIDER == enchant)
				return "depth_strider";
			else if (Enchantment.FROST_WALKER == enchant)
				return "frost_walker";
			else if (Enchantment.DAMAGE_ALL == enchant)
				return "sharpness";
			else if (Enchantment.DAMAGE_UNDEAD == enchant)
				return "smite";
			else if (Enchantment.DAMAGE_ARTHROPODS == enchant)
				return "bane_of_arthropods";
			else if (Enchantment.KNOCKBACK == enchant)
				return "knockback";
			else if (Enchantment.FIRE_ASPECT == enchant)
				return "fire_aspect";
			else if (Enchantment.LOOT_BONUS_MOBS == enchant)
				return "looting";
			else if (Enchantment.DIG_SPEED == enchant)
				return "efficiency";
			else if (Enchantment.SILK_TOUCH == enchant)
				return "silk_touch";
			else if (Enchantment.DURABILITY == enchant)
				return "unbreaking";
			else if (Enchantment.LOOT_BONUS_BLOCKS == enchant)
				return "fortune";
			else if (Enchantment.ARROW_DAMAGE == enchant)
				return "power";
			else if (Enchantment.ARROW_KNOCKBACK == enchant)
				return "punch";
			else if (Enchantment.ARROW_FIRE == enchant)
				return "flame";
			else if (Enchantment.ARROW_INFINITE == enchant)
				return "infinity";
			else if (Enchantment.LUCK == enchant)
				return "luck_of_the_sea";
			else if (Enchantment.LURE == enchant)
				return "lure";
			else if (Enchantment.MENDING == enchant)
				return "mending";
			else
				return EPS.onLegacy() ? enchant.getName().toLowerCase() : enchant.getKey().getKey();
		}

		@Override
		public Enchantment findEnchant(String enchantName) {
			Enchantment enchant =  EPS.onLegacy() ? Enchantment.getByName(bukkitNaming(enchantName)) : Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
			return enchant;
		}	
		
		public String bukkitNaming(String minecraftNaming)
		{
			switch (minecraftNaming.toLowerCase())
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
			    	return minecraftNaming.toUpperCase();
			}
		}
	}
}