package org.vivi.epsbuiltin.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EnchantFile;
import org.vivi.eps.api.EnchantFile.Parameter;
import org.vivi.sekai.enchantment.EnchantmentInfo;

public class CustomEnchants
{

	public static final Enchantment FLY = EPS.newEnchant("fly");
	public static final Enchantment SPEED = EPS.newEnchant("speed");
	public static final Enchantment JUMP_BOOST = EPS.newEnchant("jump_boost");
	public static final Enchantment REPAIR = EPS.newEnchant("repair");
	public static final Enchantment SOULBOUND = EPS.newEnchant("soulbound");
	public static final Enchantment EXPERIENCE = EPS.newEnchant("experience");
	public static final Enchantment NIGHT_VISION = EPS.newEnchant("night_vision");
	public static final Enchantment SOUL_DESTRUCTION = EPS.newEnchant("soul_destruction");
	public static final Enchantment JAGGED = EPS.newEnchant("jagged");
	public static final Enchantment RETALIATE = EPS.newEnchant("retaliate");
	public static final Enchantment LIFESTEAL = EPS.newEnchant("lifesteal");
	public static final Enchantment MOMENTUM = EPS.newEnchant("momentum");
	public static final Enchantment POISONOUS = EPS.newEnchant("poisonous");
	public static final Enchantment VOLCANIC = EPS.newEnchant("volcanic");
	public static final Enchantment SATURATED = EPS.newEnchant("saturated");
	public static final Enchantment INSATIABLE = EPS.newEnchant("insatiable");
	public static final Enchantment BEHEADING = EPS.newEnchant("beheading");
	public static final Enchantment STIFFEN = EPS.newEnchant("stiffen");
	public static final Enchantment LAST_RESORT = EPS.newEnchant("last_resort");
	public static final Enchantment POWERHOUSE = EPS.newEnchant("powerhouse");
	public static final Enchantment MELTING = EPS.newEnchant("melting");
	public static final Enchantment BACKUP_SPELLS = EPS.newEnchant("backup_spells");
	public static final Enchantment OVERHEALED = EPS.newEnchant("overhealed");
	public static final Enchantment EVADE = EPS.newEnchant("evade");
	public static final Enchantment ENDERBOW = EPS.newEnchant("enderbow");
	public static final Enchantment MACHINERY = EPS.newEnchant("machinery");
	public static final Enchantment THUNDERING_BLOW = EPS.newEnchant("thundering_blow");
	public static final Enchantment ENERGIZED = EPS.newEnchant("energized");
	public static final Enchantment SHOCKWAVE = EPS.newEnchant("shockwave");
	public static final Enchantment FIREWORKS = EPS.newEnchant("fireworks");
	public static final Enchantment FLAMMABLE = EPS.newEnchant("flammable");
	public static final Enchantment HASTE = EPS.newEnchant("haste");
	public static final Enchantment EXPLOSIVE = EPS.newEnchant("explosive");
	public static final Enchantment AUTOSMELT = EPS.newEnchant("autosmelt");
	public static final Enchantment TELEPATHY = EPS.newEnchant("telepathy");
	public static final Enchantment TOKENBLOCKS = EPS.newEnchant("tokenblocks");
	public static final Enchantment MONEYBLOCKS = EPS.newEnchant("moneyblocks");
	public static final Enchantment TOKENCHARITY = EPS.newEnchant("tokencharity");
	public static final Enchantment CHARITY = EPS.newEnchant("charity");
	public static final Enchantment EXCAVATE = EPS.newEnchant("excavate");
	public static final Enchantment DIAMOND = EPS.newEnchant("diamond");
	public static final Enchantment VEIN_MINER = EPS.newEnchant("vein_miner");
	public static final Enchantment BOOSTED = EPS.newEnchant("boosted");

	public static EnchantFile flyConfig = EPS.getEnchantFile(FLY);
	public static EnchantFile speedConfig = EPS.getEnchantFile(SPEED);
	public static EnchantFile jumpBoostConfig = EPS.getEnchantFile(JUMP_BOOST);
	public static EnchantFile repairConfig = EPS.getEnchantFile(REPAIR);
	public static EnchantFile soulboundConfig = EPS.getEnchantFile(SOULBOUND);
	public static EnchantFile experienceConfig = EPS.getEnchantFile(EXPERIENCE);
	public static EnchantFile nightvisionConfig = EPS.getEnchantFile(NIGHT_VISION);
	public static EnchantFile soulDestructionConfig = EPS.getEnchantFile(SOUL_DESTRUCTION);
	public static EnchantFile jaggedConfig = EPS.getEnchantFile(JAGGED);
	public static EnchantFile lifeStealConfig = EPS.getEnchantFile(LIFESTEAL);
	public static EnchantFile momentumConfig = EPS.getEnchantFile(MOMENTUM);
	public static EnchantFile poisonousConfig = EPS.getEnchantFile(POISONOUS);
	public static EnchantFile volcanicConfig = EPS.getEnchantFile(VOLCANIC);
	public static EnchantFile saturatedConfig = EPS.getEnchantFile(SATURATED);
	public static EnchantFile insatiableConfig = EPS.getEnchantFile(INSATIABLE);
	public static EnchantFile lastResortConfig = EPS.getEnchantFile(LAST_RESORT);
	public static EnchantFile retaliateConfig = EPS.getEnchantFile(RETALIATE);
	public static EnchantFile beheadingConfig = EPS.getEnchantFile(BEHEADING);
	public static EnchantFile stiffenConfig = EPS.getEnchantFile(STIFFEN);
	public static EnchantFile powerhouseConfig = EPS.getEnchantFile(POWERHOUSE);
	public static EnchantFile meltingConfig = EPS.getEnchantFile(MELTING);
	public static EnchantFile backupSpellsConfig = EPS.getEnchantFile(BACKUP_SPELLS);
	public static EnchantFile overhealedConfig = EPS.getEnchantFile(OVERHEALED);
	public static EnchantFile evadeConfig = EPS.getEnchantFile(EVADE);
	public static EnchantFile enderbowConfig = EPS.getEnchantFile(ENDERBOW);
	public static EnchantFile machineryConfig = EPS.getEnchantFile(MACHINERY);
	public static EnchantFile thunderingBlowConfig = EPS.getEnchantFile(THUNDERING_BLOW);
	public static EnchantFile energizedConfig = EPS.getEnchantFile(ENERGIZED);
	public static EnchantFile shockwaveConfig = EPS.getEnchantFile(SHOCKWAVE);
	public static EnchantFile fireworksConfig = EPS.getEnchantFile(FIREWORKS);
	public static EnchantFile flammableConfig = EPS.getEnchantFile(FLAMMABLE);
	public static EnchantFile hasteConfig = EPS.getEnchantFile(HASTE);
	public static EnchantFile tokenBlocksConfig = EPS.getEnchantFile(TOKENBLOCKS);
	public static EnchantFile moneyBlocksConfig = EPS.getEnchantFile(MONEYBLOCKS);
	public static EnchantFile charityConfig = EPS.getEnchantFile(CHARITY);
	public static EnchantFile tokenCharityConfig = EPS.getEnchantFile(TOKENCHARITY);
	public static EnchantFile explosiveConfig = EPS.getEnchantFile(EXPLOSIVE);
	public static EnchantFile excavateConfig = EPS.getEnchantFile(EXCAVATE);
	public static EnchantFile diamondConfig = EPS.getEnchantFile(DIAMOND);
	public static EnchantFile veinMinerConfig = EPS.getEnchantFile(VEIN_MINER);
	public static EnchantFile telepathyConfig = EPS.getEnchantFile(TELEPATHY);
	public static EnchantFile autosmeltConfig = EPS.getEnchantFile(AUTOSMELT);
	public static EnchantFile boostedConfig = EPS.getEnchantFile(BOOSTED);

	public static void register()
	{
		EPS.registerEnchant(new Fly());
		EPS.registerEnchant(SPEED);
		EPS.registerEnchant(JUMP_BOOST);
		EPS.registerEnchant(REPAIR);
		EPS.registerEnchant(SOULBOUND);
		EPS.registerEnchant(EXPERIENCE);
		EPS.registerEnchant(NIGHT_VISION);
		EPS.registerEnchant(SOUL_DESTRUCTION);
		EPS.registerEnchant(JAGGED);
		EPS.registerEnchant(RETALIATE);
		EPS.registerEnchant(LIFESTEAL);
		EPS.registerEnchant(MOMENTUM);
		EPS.registerEnchant(POISONOUS);
		EPS.registerEnchant(VOLCANIC);
		EPS.registerEnchant(SATURATED);
		EPS.registerEnchant(INSATIABLE);
		EPS.registerEnchant(BEHEADING);
		EPS.registerEnchant(STIFFEN);
		EPS.registerEnchant(LAST_RESORT);
		EPS.registerEnchant(POWERHOUSE);
		EPS.registerEnchant(MELTING);
		EPS.registerEnchant(BACKUP_SPELLS);
		EPS.registerEnchant(OVERHEALED);
		EPS.registerEnchant(EVADE);
		EPS.registerEnchant(ENDERBOW);
		EPS.registerEnchant(MACHINERY);
		EPS.registerEnchant(THUNDERING_BLOW);
		EPS.registerEnchant(ENERGIZED);
		EPS.registerEnchant(SHOCKWAVE);
		EPS.registerEnchant(FIREWORKS);
		EPS.registerEnchant(FLAMMABLE);
		EPS.registerEnchant(HASTE);
		EPS.registerEnchant(EXPLOSIVE);
		EPS.registerEnchant(AUTOSMELT);
		EPS.registerEnchant(TELEPATHY);
		EPS.registerEnchant(TOKENBLOCKS);
		EPS.registerEnchant(MONEYBLOCKS);
		EPS.registerEnchant(TOKENCHARITY);
		EPS.registerEnchant(CHARITY);
		EPS.registerEnchant(EXCAVATE);
		EPS.registerEnchant(DIAMOND);
		EPS.registerEnchant(VEIN_MINER);
		EPS.registerEnchant(BOOSTED);

		// Right now, there are only 40 custom enchants available.
		// There are no plans to add more enchants.

		// Hard-coding all the enchant configs... my last method of writing configs was
		// so very inefficient
		// Using matchMaterial so that future versions with different material enum
		// names won't be affected and will default to BOOK
		EPS.getEnchantFile(Enchantment.DIG_SPEED).fillEnchantConfig("Efficiency", 10, 30,
				Material.matchMaterial("DIAMOND_PICKAXE"), EnchantmentInfo.getDescription(Enchantment.DIG_SPEED),
				"30 * 1.4^%lvl%");
		EPS.getEnchantFile(Enchantment.DURABILITY).fillEnchantConfig("Unbreaking", 10, 10,
				Material.matchMaterial("BEDROCK"), EnchantmentInfo.getDescription(Enchantment.DURABILITY),
				"10 * 1.2^%lvl%");
		EPS.getEnchantFile(Enchantment.LOOT_BONUS_BLOCKS).fillEnchantConfig("Fortune", 10, 40,
				Material.matchMaterial("DIAMOND"), EnchantmentInfo.getDescription(Enchantment.LOOT_BONUS_BLOCKS),
				"40 * 1.5^%lvl%");
		EPS.getEnchantFile(Enchantment.MENDING).fillEnchantConfig("Mending", 1, 200,
				Material.matchMaterial("PRISMARINE_CRYSTALS"), EnchantmentInfo.getDescription(Enchantment.MENDING),
				"300");
		EPS.getEnchantFile(Enchantment.FIRE_ASPECT).fillEnchantConfig("Fire Aspect", 4, 65,
				Material.matchMaterial("FIRE_CHARGE"), EnchantmentInfo.getDescription(Enchantment.FIRE_ASPECT),
				"65 * 1.5^%lvl%");
		EPS.getEnchantFile(Enchantment.ARROW_INFINITE).fillEnchantConfig("Infinity", 1, 140,
				Material.matchMaterial("EYE_OF_ENDER"), EnchantmentInfo.getDescription(Enchantment.ARROW_INFINITE),
				"250");
		EPS.getEnchantFile(Enchantment.LUCK).fillEnchantConfig("Luck of the Sea", 5, 24,
				Material.matchMaterial("FISHING_ROD"), EnchantmentInfo.getDescription(Enchantment.LUCK),
				"24 + %lvl% * 24");
		EPS.getEnchantFile(Enchantment.LURE).fillEnchantConfig("Lure", 5, 24, Material.matchMaterial("PUFFERFISH"),
				EnchantmentInfo.getDescription(Enchantment.LURE), "24 + %lvl% * 24");
		EPS.getEnchantFile(Enchantment.ARROW_DAMAGE).fillEnchantConfig("Power", 10, 65, Material.matchMaterial("BOW"),
				EnchantmentInfo.getDescription(Enchantment.ARROW_DAMAGE), "65 * 1.4^%lvl%");
		EPS.getEnchantFile(Enchantment.PROTECTION_ENVIRONMENTAL).fillEnchantConfig("Protection", 10, 40,
				Material.matchMaterial("DIAMOND_CHESTPLATE"),
				EnchantmentInfo.getDescription(Enchantment.PROTECTION_ENVIRONMENTAL), "40 * 1.3^%lvl%");
		EPS.getEnchantFile(Enchantment.DAMAGE_ALL).fillEnchantConfig("Sharpness", 10, 60,
				Material.matchMaterial("DIAMOND_SWORD"), EnchantmentInfo.getDescription(Enchantment.DAMAGE_ALL),
				"60 * 1.4^%lvl%");
		EPS.getEnchantFile(Enchantment.THORNS).fillEnchantConfig("Thorns", 5, 16, Material.matchMaterial("CACTUS"),
				EnchantmentInfo.getDescription(Enchantment.THORNS), "32 * 1.3^%lvl%");

		flyConfig.fillEnchantConfig("Fly", 1, 6400, Material.matchMaterial("ELYTRA"),
				"Allows you to fly while holding this item.", "51200", new Parameter("enabled", true));
		speedConfig.fillEnchantConfig("Speed", 4, 500, Material.matchMaterial("FEATHER"),
				"Move faster while holding this item.", "500 * 2 ^ %lvl%");
		jumpBoostConfig.fillEnchantConfig("Jump Boost", 4, 400, Material.matchMaterial("EMERALD_BLOCK"),
				"Jump higher while holding this item.", "400 * 2 ^ %lvl%");
		repairConfig.fillEnchantConfig("Repair", 1, 2400, Material.matchMaterial("ANVIL"),
				"While moving, this item will be automatically repaired.", "12800");
		experienceConfig.fillEnchantConfig("Experience", 3, 750, Material.matchMaterial("EXPERIENCE_BOTTLE"),
				"Drops now give more experience.", "1500 * 1.5 ^ %lvl%");
		soulboundConfig.fillEnchantConfig("Soulbound", 1, 4000, Material.matchMaterial("SKELETON_SKULL"),
				"Has a moderate chance to keep your item on death.", "12800", new Parameter("chance", 40));
		nightvisionConfig.fillEnchantConfig("Night Vision", 1, 300, Material.matchMaterial("ENDER_PEARL"),
				"Gain Night Vision upon equipping this tool.", "1500");
		soulDestructionConfig.fillEnchantConfig("Soul Destruction", 1, 6500, Material.matchMaterial("SKELETON_SKULL"),
				"Has a chance to stop Soulbound from working for any players you kill.", "12800",
				new Parameter("chance", 10));
		jaggedConfig.fillEnchantConfig("Jagged", 2, 200, Material.matchMaterial("PRISMARINE_SHARD"),
				"The damage of this item is amplified when its durability is low.", "100 * 3^%lvl%",
				new Parameter("durabilitythresholdpercent", 30));
		retaliateConfig.fillEnchantConfig("Retaliate", 3, 400, Material.matchMaterial("IRON_AXE"),
				"Gives a very temporary strength boost when hit.", "500 * 1.5^%lvl%",
				new Parameter("duration-seconds", "1"));
		lifeStealConfig.fillEnchantConfig("Life Steal", 2, 600, Material.matchMaterial("WITHER_SKELETON_SKULL"),
				"Hitting mobs returns you health.", "600 * 1.5^%lvl%", new Parameter("hearts", "%lvl%*0.5"));
		momentumConfig.fillEnchantConfig("Momentum", 2, 300, Material.matchMaterial("LEATHER_BOOTS"),
				"Hitting mobs gives you a temporary speed boost.", "400 * 1.5^%lvl%",
				new Parameter("duration-seconds", "%lvl%*4"));
		poisonousConfig.fillEnchantConfig("Poisonous", 10, 175, Material.matchMaterial("BOOK"),
				"Poisons anyone who hits you.", "175 * 1.1^%lvl%", new Parameter("duration-seconds", "2*%lvl%"));
		volcanicConfig.fillEnchantConfig("Volcanic", 10, 350, Material.matchMaterial("LAVA_BUCKET"),
				"Burns anyone who hits you.", "350 * %lvl%", new Parameter("duration-seconds", "1.5*%lvl%"));
		saturatedConfig.fillEnchantConfig("Saturated", 10, 300, Material.matchMaterial("COOKED_BEEF"),
				"Gives you saturation when you are hit.", "400 * %lvl%", new Parameter("ticks", "30*%lvl%"));
		insatiableConfig.fillEnchantConfig("Insatiable", 10, 400, Material.matchMaterial("REDSTONE"),
				"The less health you have, the more damage you inflict.", "400 * 1.2^%lvl%",
				new Parameter("extradamage", "%lvl%"));
		beheadingConfig.fillEnchantConfig("Beheading", 5, 240, Material.matchMaterial("ZOMBIE_HEAD"),
				"Increases the chance of getting the head of a player or a mob.", "250 * 1.2^%lvl%",
				new Parameter("player-chance", "%lvl%"), new Parameter("mob-chance", "%lvl%*2"));
		stiffenConfig.fillEnchantConfig("Stiffen", 2, 300, Material.matchMaterial("IRON_ARMOR"),
				"Applies resistance on you when you are low on health.", "300 * 1.5^%lvl%",
				new Parameter("healththreshold", "5"), new Parameter("amplifier", "%lvl%"));
		lastResortConfig.fillEnchantConfig("Last Resort", 1, 300, Material.matchMaterial("REDSTONE_BLOCK"),
				"When at low health, your attacks will deal 3x as much damage.", "1500",
				new Parameter("healththreshold", "3"));
		powerhouseConfig.fillEnchantConfig("Powerhouse", 2, 200, Material.matchMaterial("DIAMOND_SWORD"),
				"Shift + right-click to activate a temporary but powerful strength boost.", "200 * 2.5^%lvl%",
				new Parameter("duration-seconds", "1"), new Parameter("amplifier", "%lvl%"),
				new Parameter("cooldown-seconds", "10"));
		meltingConfig.fillEnchantConfig("Melting", 2, 300, Material.matchMaterial("SNOWBALL"),
				"Sets your opponent on fire and slows them down. Completely freezes opponents every 10 hits.",
				"300 * 1.5^%lvl%", new Parameter("fire-ticks", "%lvl%*20"), new Parameter("slowness-ticks", "%lvl%*20"),
				new Parameter("slowness-level", "1"), new Parameter("freeze-ticks", "%lvl%*20"),
				new Parameter("freeze-hits", "10"));
		backupSpellsConfig.fillEnchantConfig("Backup Spells", 1, 240, Material.matchMaterial("GLASS_BOTTLE"),
				"Shift + right-click to throw a random harmful splash potion.", "480",
				new Parameter("cooldown-seconds", "15"));
		overhealedConfig.fillEnchantConfig("Overhealed", 4, 350, Material.matchMaterial("DIAMOND_CHESTPLATE"),
				"Gives more hearts.", "900 * 1.35^%lvl%");
		evadeConfig.fillEnchantConfig("Evade", 10, 400, Material.matchMaterial("GOLD_BOOTS"),
				"Completely avoids an attack every 10 hits.", "600 * 1.5^%lvl%", new Parameter("luckbased", false),
				new Parameter("chance", "5"), new Parameter("hits-to-activate", "10"));
		enderbowConfig.fillEnchantConfig("Enderbow", 1, 750, Material.matchMaterial("ENDER_PEARL"),
				"Allows you to teleport to where your arrow lands. You must be holding shift while shooting until you teleport.",
				"1780", new Parameter("radius", "5"), new Parameter("cooldown-seconds", "10"));
		machineryConfig.fillEnchantConfig("Machinery", 5, 750, Material.matchMaterial("PISTON"),
				"A barrage of arrows will fly down on your enemy every few shots!", "1250 * %lvl%",
				new Parameter("radius", "2"), new Parameter("arrows", "5*%lvl%"),
				new Parameter("shots-to-activate", "12 - %lvl%"));
		thunderingBlowConfig.fillEnchantConfig("Thundering Blow", 1, 400, Material.matchMaterial("ENCHANTED_BOOK"),
				"Smites any enemy you hit with thunder every few shots.", "600",
				new Parameter("shots-to-activate", "4"));
		energizedConfig.fillEnchantConfig("Energized", 1, 450, Material.matchMaterial("ARROW"),
				"Gain a speed boost and regeneration when you are hit by your arrow.", "900",
				new Parameter("speed-amplifier", "%lvl%"), new Parameter("speed-duration-seconds", "%lvl%*8"),
				new Parameter("regeneration-duration-seconds", "%lvl%*8"),
				new Parameter("regeneration-amplifier", "1"));
		shockwaveConfig.fillEnchantConfig("Shockwave", 3, 600, Material.matchMaterial("GLOWSTONE"),
				"Damages all enemies in a radius of where your arrow lands.", "600 * 1.4^%lvl%",
				new Parameter("damage", "%lvl%"), new Parameter("radius", "%lvl%*2"));
		fireworksConfig.fillEnchantConfig("Fireworks", 1, 450, Material.matchMaterial("FIREWORK_ROCKET"),
				"These aren't actually fireworks. They're explosions.", "900", new Parameter("damage", "2"));
		flammableConfig.fillEnchantConfig("Flammable", 1, 65, Material.matchMaterial("MAGMA_CUBE_SPAWN_EGG"),
				"Burns the nearby terrain of where your arrow lands.", "100");
		hasteConfig.fillEnchantConfig("Haste", 2, 75, Material.matchMaterial("GOLDEN_PICKAXE"),
				"May apply haste to you while mining.", "75 * 2^%lvl%", new Parameter("chance", "%lvl%*0.1"));
		explosiveConfig.fillEnchantConfig("Explosive", 10, 75, Material.matchMaterial("TNT"),
				"Has a chance to blow up blocks.", "100 * 1.35^%lvl%", new Parameter("chance", "%lvl%"));
		autosmeltConfig.fillEnchantConfig("Autosmelt", 10, 200, Material.matchMaterial("FURNACE"),
				"Has a chance to automatically smelt mined blocks.", "200 * 1.15^%lvl%",
				new Parameter("chance", "%lvl%*10"));
		telepathyConfig.fillEnchantConfig("Telepathy", 1, 800, Material.matchMaterial("ENDER_PEARL"),
				"Automatically transfers mined items into your inventory.", "1780", new Parameter("chance", "100"));
		tokenBlocksConfig.fillEnchantConfig("Token Blocks", 20, 100, Material.matchMaterial("GOLD_BLOCK"),
				"Has a chance to reward you tokens for mining.", "150 * 1.1^%lvl%",
				new Parameter("chance", "%lvl%*0.1"), new Parameter("tokens", "%lvl%*5"),
				new Parameter("random-range", "%lvl%"));
		moneyBlocksConfig.fillEnchantConfig("Money Blocks", 10, 100, Material.matchMaterial("BOOK"),
				"Has a chance to reward you money for mining.", "150 * 1.1^%lvl%", new Parameter("chance", "%lvl%*0.1"),
				new Parameter("money", "%lvl%*350"), new Parameter("random-range", "%lvl%*15"));
		tokenCharityConfig.fillEnchantConfig("Token Charity", 10, 100, Material.matchMaterial("BOOK"),
				"Has a chance to reward everyone tokens for your mining.", "150 * 1.1^%lvl%",
				new Parameter("chance", "%lvl%*0.02"), new Parameter("tokens", "%lvl%*5"),
				new Parameter("random-range", "%lvl%*2"));
		charityConfig.fillEnchantConfig("Charity", 10, 100, Material.matchMaterial("BOOK"),
				"Has a chance to reward everyone money for your mining.", "150 * 1.1^%lvl%",
				new Parameter("chance", "%lvl%*0.02"), new Parameter("money", "%lvl%*350"),
				new Parameter("random-range", "%lvl%*15"));
		excavateConfig.fillEnchantConfig("Excavate", 10, 75, Material.matchMaterial("IRON_PICKAXE"),
				"Has a chance to destroy a cube of blocks at once.", "100 * 1.35^%lvl%",
				new Parameter("chance", "%lvl%"));
		diamondConfig.fillEnchantConfig("Diamond", 10, 75, Material.matchMaterial("IRON_PICKAXE"),
				"Has a chance to destroy blocks in a diamond shape.", "100 * 1.35^%lvl%",
				new Parameter("chance", "%lvl%"));
		veinMinerConfig.fillEnchantConfig("Vein Miner", 10, 50, Material.matchMaterial("VINE"),
				"Has a chance to break all blocks in a vein of ores.", "75 * 1.2^%lvl%",
				new Parameter("chance", "%lvl%*4"));
		boostedConfig.fillEnchantConfig("Boosted", 3, 100, Material.matchMaterial("DIAMOND_BLOCK"),
				"Shift + right-click to gain a temporary Fortune boost.", "200 * %lvl%",
				new Parameter("cooldown-seconds", "40 - 5 * %lvl%"), new Parameter("duration-seconds", "4"));
	}
}
