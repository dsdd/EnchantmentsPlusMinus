package org.vivi.epsbuiltin.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.api.EPSConfiguration.EPSParam;

public class CustomEnchants {

	public static final Enchantment FLY = EPS.newEnchant("fly", "Fly");
	public static final Enchantment REPAIR = EPS.newEnchant("repair", "Repair");
	public static final Enchantment SOULBOUND = EPS.newEnchant("soulbound", "Soulbound");
	public static final Enchantment EXPERIENCE = EPS.newEnchant("experience", "Experience");
	public static final Enchantment SOUL_DESTRUCTION = EPS.newEnchant("soul_destruction", "Soul Destruction");
	public static final Enchantment JAGGED = EPS.newEnchant("jagged", "Jagged");
	public static final Enchantment RETALIATE = EPS.newEnchant("retaliate", "Retaliate");
	public static final Enchantment LIFESTEAL = EPS.newEnchant("lifesteal", "Lifesteal");
	public static final Enchantment MOMENTUM = EPS.newEnchant("momentum", "Momentum");
	public static final Enchantment POISONOUS = EPS.newEnchant("poisonous", "Poisonous");
	public static final Enchantment VOLCANIC = EPS.newEnchant("volcanic", "Volcanic");
	public static final Enchantment SATURATED = EPS.newEnchant("saturated", "Saturated");
	public static final Enchantment INSATIABLE = EPS.newEnchant("insatiable", "Insatiable");
	public static final Enchantment BEHEADING = EPS.newEnchant("beheading", "Beheading");
	public static final Enchantment STIFFEN = EPS.newEnchant("stiffen", "Stiffen");
	public static final Enchantment LAST_RESORT = EPS.newEnchant("last_resort", "Last Resort");
	public static final Enchantment POWERHOUSE = EPS.newEnchant("powerhouse", "Powerhouse");
	public static final Enchantment MELTING = EPS.newEnchant("melting", "Melting");
	public static final Enchantment BACKUP_SPELLS = EPS.newEnchant("backup_spells", "Backup Spells");
	public static final Enchantment OVERHEALED = EPS.newEnchant("overhealed", "Overhealed");
	public static final Enchantment EVADE = EPS.newEnchant("evade", "Evade");
	public static final Enchantment ENDERBOW = EPS.newEnchant("enderbow", "Enderbow");
	public static final Enchantment MACHINERY = EPS.newEnchant("machinery", "Machinery");
	public static final Enchantment THUNDERING_BLOW = EPS.newEnchant("thundering_blow", "Thundering_Blow");
	public static final Enchantment ENERGIZED = EPS.newEnchant("energized", "Energized");
	public static final Enchantment SHOCKWAVE = EPS.newEnchant("shockwave", "Shockwave");
	public static final Enchantment FIREWORKS = EPS.newEnchant("fireworks", "Fireworks");
	public static final Enchantment FLAMMABLE = EPS.newEnchant("flammable", "Flammable");
	public static final Enchantment HASTE = EPS.newEnchant("haste", "Haste");
	public static final Enchantment EXPLOSIVE = EPS.newEnchant("explosive", "Explosive");
	public static final Enchantment AUTOSMELT = EPS.newEnchant("autosmelt", "Autosmelt");
	public static final Enchantment TELEPATHY = EPS.newEnchant("telepathy", "Telepathy");
	public static final Enchantment TOKENBLOCKS = EPS.newEnchant("tokenblocks", "TokenBlocks");
	public static final Enchantment MONEYBLOCKS = EPS.newEnchant("moneyblocks", "MoneyBlocks");
	public static final Enchantment TOKENCHARITY = EPS.newEnchant("tokencharity", "TokenCharity");
	public static final Enchantment CHARITY = EPS.newEnchant("charity", "Charity");
	public static final Enchantment EXCAVATE = EPS.newEnchant("excavate", "Excavate");
	public static final Enchantment DIAMOND = EPS.newEnchant("diamond", "Diamond");
	public static final Enchantment VEIN_MINER = EPS.newEnchant("vein_miner", "Vein Miner");
	public static final Enchantment BOOSTED = EPS.newEnchant("boosted", "Boosted");
	
	public static EPSConfiguration flyConfig = EPSConfiguration.getConfiguration(FLY);
	public static EPSConfiguration repairConfig = EPSConfiguration.getConfiguration(REPAIR);
	public static EPSConfiguration soulboundConfig = EPSConfiguration.getConfiguration(SOULBOUND);
	public static EPSConfiguration experienceConfig = EPSConfiguration.getConfiguration(EXPERIENCE);
	public static EPSConfiguration soulDestructionConfig = EPSConfiguration.getConfiguration(SOUL_DESTRUCTION);
	public static EPSConfiguration jaggedConfig = EPSConfiguration.getConfiguration(JAGGED);
	public static EPSConfiguration lifeStealConfig = EPSConfiguration.getConfiguration(LIFESTEAL);
	public static EPSConfiguration momentumConfig = EPSConfiguration.getConfiguration(MOMENTUM);
	public static EPSConfiguration poisonousConfig = EPSConfiguration.getConfiguration(POISONOUS);
	public static EPSConfiguration volcanicConfig = EPSConfiguration.getConfiguration(VOLCANIC);
	public static EPSConfiguration saturatedConfig = EPSConfiguration.getConfiguration(SATURATED);
	public static EPSConfiguration insatiableConfig = EPSConfiguration.getConfiguration(INSATIABLE);
	public static EPSConfiguration lastResortConfig = EPSConfiguration.getConfiguration(LAST_RESORT);
	public static EPSConfiguration retaliateConfig = EPSConfiguration.getConfiguration(RETALIATE);
	public static EPSConfiguration beheadingConfig = EPSConfiguration.getConfiguration(BEHEADING);
	public static EPSConfiguration stiffenConfig = EPSConfiguration.getConfiguration(STIFFEN);
	public static EPSConfiguration powerhouseConfig = EPSConfiguration.getConfiguration(POWERHOUSE);
	public static EPSConfiguration meltingConfig = EPSConfiguration.getConfiguration(MELTING);
	public static EPSConfiguration backupSpellsConfig = EPSConfiguration.getConfiguration(BACKUP_SPELLS);
	public static EPSConfiguration overhealedConfig = EPSConfiguration.getConfiguration(OVERHEALED);
	public static EPSConfiguration evadeConfig = EPSConfiguration.getConfiguration(EVADE);
	public static EPSConfiguration enderbowConfig = EPSConfiguration.getConfiguration(ENDERBOW);
	public static EPSConfiguration machineryConfig = EPSConfiguration.getConfiguration(MACHINERY);
	public static EPSConfiguration thunderingBlowConfig = EPSConfiguration.getConfiguration(THUNDERING_BLOW);
	public static EPSConfiguration energizedConfig = EPSConfiguration.getConfiguration(ENERGIZED);
	public static EPSConfiguration shockwaveConfig = EPSConfiguration.getConfiguration(SHOCKWAVE);
	public static EPSConfiguration fireworksConfig = EPSConfiguration.getConfiguration(FIREWORKS);
	public static EPSConfiguration flammableConfig = EPSConfiguration.getConfiguration(FLAMMABLE);
	public static EPSConfiguration hasteConfig = EPSConfiguration.getConfiguration(HASTE);
	public static EPSConfiguration tokenBlocksConfig = EPSConfiguration.getConfiguration(TOKENBLOCKS);
	public static EPSConfiguration moneyBlocksConfig = EPSConfiguration.getConfiguration(MONEYBLOCKS);
	public static EPSConfiguration charityConfig = EPSConfiguration.getConfiguration(CHARITY);
	public static EPSConfiguration tokenCharityConfig = EPSConfiguration.getConfiguration(TOKENCHARITY);
	public static EPSConfiguration explosiveConfig = EPSConfiguration.getConfiguration(EXPLOSIVE);
	public static EPSConfiguration excavateConfig = EPSConfiguration.getConfiguration(EXCAVATE);
	public static EPSConfiguration diamondConfig = EPSConfiguration.getConfiguration(DIAMOND);
	public static EPSConfiguration veinMinerConfig = EPSConfiguration.getConfiguration(VEIN_MINER);
	public static EPSConfiguration telepathyConfig = EPSConfiguration.getConfiguration(TELEPATHY);
	public static EPSConfiguration autosmeltConfig = EPSConfiguration.getConfiguration(AUTOSMELT);
	public static EPSConfiguration boostedConfig = EPSConfiguration.getConfiguration(BOOSTED);
	
	public static void register()
	{
		EPS.registerEnchant(FLY);
		EPS.registerEnchant(REPAIR);
		EPS.registerEnchant(SOULBOUND);
		EPS.registerEnchant(EXPERIENCE);
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
		
		// Hard-coding all the enchant configs... my last method of writing configs was so very inefficient
		// Using matchMaterial so that future versions with different material enum names won't be affected and will default to BOOK
		EPSConfiguration.getConfiguration(Enchantment.DIG_SPEED).fillEnchantConfig(10, 30, Material.matchMaterial("DIAMOND_PICKAXE"), "Increases your mining speed.", "30 * 1.4^%lvl%");
		EPSConfiguration.getConfiguration(Enchantment.DURABILITY).fillEnchantConfig(10, 10, Material.matchMaterial("UNBREAKING"), "Increases the durability of items.", "10 * 1.2^%lvl%");
		EPSConfiguration.getConfiguration(Enchantment.LOOT_BONUS_BLOCKS).fillEnchantConfig(10, 40, Material.matchMaterial("DIAMOND"), "Increases your mining speed.", "40 * 1.5^%lvl%");
		EPSConfiguration.getConfiguration(Enchantment.MENDING).fillEnchantConfig(1, 200, Material.matchMaterial("PRISMARINE_CRYSTALS"), "Automatically repairs a tool or armor with XP.", "300");
		EPSConfiguration.getConfiguration(Enchantment.FIRE_ASPECT).fillEnchantConfig(4, 65, Material.matchMaterial("FIRE_CHARGE"), "Burns anyone who gets hit by a weapon.", "65 * 1.5^%lvl%");
		EPSConfiguration.getConfiguration(Enchantment.ARROW_INFINITE).fillEnchantConfig(1, 140, Material.matchMaterial("EYE_OF_ENDER"), "Never run out of arrows again!", "250");
		EPSConfiguration.getConfiguration(Enchantment.LUCK).fillEnchantConfig(5, 24, Material.matchMaterial("FISHING_ROD"), "Increases the chance to get luckier drops from fishing.", "24 + %lvl% * 24");
		EPSConfiguration.getConfiguration(Enchantment.LURE).fillEnchantConfig(5, 24, Material.matchMaterial("PUFFERFISH"), "Increases the catch rate of a fishing rod.", "24 + %lvl% * 24");
		EPSConfiguration.getConfiguration(Enchantment.ARROW_DAMAGE).fillEnchantConfig(10, 65, Material.matchMaterial("BOW"), "Increases the damage of your bow.", "65 * 1.4^%lvl%");
		EPSConfiguration.getConfiguration(Enchantment.PROTECTION_ENVIRONMENTAL).fillEnchantConfig(10, 40, Material.matchMaterial("DIAMOND_CHESTPLATE"), "Increases the amount of damage blocked by your armor.", "40 * 1.3^%lvl%");
		EPSConfiguration.getConfiguration(Enchantment.DAMAGE_ALL).fillEnchantConfig(10, 60, Material.matchMaterial("DIAMOND_SWORD"), "Increases the attack damage of a weapon.", "60 * 1.4^%lvl%");
		EPSConfiguration.getConfiguration(Enchantment.THORNS).fillEnchantConfig(5, 16, Material.matchMaterial("CACTUS"), "Has a chance to inflict damage on your attacker while being attacked.", "32 * 1.3^%lvl%");
		
		
		
		flyConfig.fillEnchantConfig(1, 6400, Material.matchMaterial("ELYTRA"), "Allows you to fly while holding this item.", "51200", new EPSParam("enabled", true));
		repairConfig.fillEnchantConfig(1, 2400, Material.matchMaterial("ANVIL"), "While moving, this item will be automatically repaired.", "12800");
		experienceConfig.fillEnchantConfig(3, 750, Material.matchMaterial("EXPERIENCE_BOTTLE"), "Drops now give more experience.", "1500 * 1.5 ^ %lvl%");
		soulboundConfig.fillEnchantConfig(1, 4000, Material.matchMaterial("SKELETON_SKULL"), "Has a moderate chance to keep your item on death.", "12800", new EPSParam("chance", 40));
		soulDestructionConfig.fillEnchantConfig(1, 6500, Material.matchMaterial("SKELETON_SKULL"), "Has a chance to stop Soulbound from working for any players you kill.", "12800", new EPSParam("chance", 10));
		jaggedConfig.fillEnchantConfig(2, 200, Material.matchMaterial("PRISMARINE_SHARD"), "The damage of this item is amplified when its durability is low.", "100 * 3^%lvl%", new EPSParam("durabilitythresholdpercent", 30));
		retaliateConfig.fillEnchantConfig(3, 400, Material.matchMaterial("IRON_AXE"), "Gives a very temporary strength boost when hit.", "500 * 1.5^%lvl%", new EPSParam("duration-seconds", "1"));
		lifeStealConfig.fillEnchantConfig(2, 600, Material.matchMaterial("WITHER_SKELETON_SKULL"), "Hitting mobs returns you health.", "600 * 1.5^%lvl%", new EPSParam("hearts", "%lvl%*0.5"));
		momentumConfig.fillEnchantConfig(2, 300, Material.matchMaterial("LEATHER_BOOTS"), "Hitting mobs gives you a temporary speed boost.", "400 * 1.5^%lvl%", new EPSParam("duration-seconds", "%lvl%*4"));
		poisonousConfig.fillEnchantConfig(10, 175, Material.matchMaterial("BOOK"), "Poisons anyone who hits you.", "175 * 1.1^%lvl%", new EPSParam("duration-seconds", "2*%lvl%"));
		volcanicConfig.fillEnchantConfig(10, 350, Material.matchMaterial("LAVA_BUCKET"), "Burns anyone who hits you.", "350 * %lvl%", new EPSParam("duration-seconds", "1.5*%lvl%"));
		saturatedConfig.fillEnchantConfig(10, 300, Material.matchMaterial("COOKED_BEEF"), "Gives you saturation when you are hit.", "400 * %lvl%", new EPSParam("ticks", "30*%lvl%"));
		insatiableConfig.fillEnchantConfig(10, 400, Material.matchMaterial("REDSTONE"), "The less health you have, the more damage you inflict.", "400 * 1.2^%lvl%", new EPSParam("extradamage", "%lvl%"));
		beheadingConfig.fillEnchantConfig(5, 240, Material.matchMaterial("ZOMBIE_HEAD"), "Increases the chance of getting the head of a player or a mob.", "250 * 1.2^%lvl%", new EPSParam("player-chance", "%lvl%"), new EPSParam("mob-chance", "%lvl%*2"));
		stiffenConfig.fillEnchantConfig(2, 300, Material.matchMaterial("IRON_ARMOR"), "Applies resistance on you when you are low on health.", "300 * 1.5^%lvl%", new EPSParam("healththreshold", "5"), new EPSParam("amplifier", "%lvl%"));
		lastResortConfig.fillEnchantConfig(1, 300, Material.matchMaterial("REDSTONE_BLOCK"), "When at low health, your attacks will deal 3x as much damage.", "1500", new EPSParam("healththreshold", "3"));
		powerhouseConfig.fillEnchantConfig(2, 200, Material.matchMaterial("DIAMOND_SWORD"), "Shift + right-click to activate a temporary but powerful strength boost.", "200 * 2.5^%lvl%", new EPSParam("duration-seconds", "1"), new EPSParam("amplifier", "%lvl%"), new EPSParam("cooldown-seconds", "10"));
		meltingConfig.fillEnchantConfig(2, 300, Material.matchMaterial("SNOWBALL"), "Sets your opponent on fire and slows them down. Completely freezes opponents every 10 hits.", "300 * 1.5^%lvl%", new EPSParam("fire-ticks", "%lvl%*20"), new EPSParam("slowness-ticks", "%lvl%*20"), new EPSParam("slowness-level", "1"), new EPSParam("freeze-ticks", "%lvl%*20"), new EPSParam("freeze-hits", "10"));
		backupSpellsConfig.fillEnchantConfig(1, 240, Material.matchMaterial("GLASS_BOTTLE"), "Shift + right-click to throw a random harmful splash potion.", "480", new EPSParam("cooldown-seconds", "15"));
		overhealedConfig.fillEnchantConfig(4, 350, Material.matchMaterial("DIAMOND_CHESTPLATE"), "Gives more hearts.", "900 * 1.35^%lvl%");
		evadeConfig.fillEnchantConfig(10, 400, Material.matchMaterial("GOLD_BOOTS"), "Completely avoids an attack every 10 hits.", "600 * 1.5^%lvl%", new EPSParam("luckbased", false), new EPSParam("chance", "5"), new EPSParam("hits-to-activate", "10"));
		enderbowConfig.fillEnchantConfig(1, 750, Material.matchMaterial("ENDER_PEARL"), "Allows you to teleport to where your arrow lands. You must be holding shift while shooting until you teleport.", "1780", new EPSParam("radius", "5"), new EPSParam("cooldown-seconds", "10"));
		machineryConfig.fillEnchantConfig(5, 750, Material.matchMaterial("PISTON"), "A barrage of arrows will fly down on your enemy every few shots!", "1250 * %lvl%", new EPSParam("radius", "2"), new EPSParam("arrows", "5*%lvl%"), new EPSParam("shots-to-activate", "12 - %lvl%"));
		thunderingBlowConfig.fillEnchantConfig(1, 400, Material.matchMaterial("ENCHANTED_BOOK"), "Smites any enemy you hit with thunder every few shots.", "600", new EPSParam("shots-to-activate", "4"));
		energizedConfig.fillEnchantConfig(1, 450, Material.matchMaterial("ARROW"), "Gain a speed boost and regeneration when you are hit by your arrow.", "900", new EPSParam("speed-amplifier", "%lvl%"), new EPSParam("speed-duration-seconds", "%lvl%*8"), new EPSParam("regeneration-duration-seconds", "%lvl%*8"), new EPSParam("regeneration-amplifier", "1"));
		shockwaveConfig.fillEnchantConfig(3, 600, Material.matchMaterial("GLOWSTONE"), "Damages all enemies in a radius of where your arrow lands.", "600 * 1.4^%lvl%", new EPSParam("damage", "%lvl%"), new EPSParam("radius", "%lvl%*2"));
		fireworksConfig.fillEnchantConfig(1, 450, Material.matchMaterial("FIREWORK_ROCKET"), "These aren't actually fireworks. They're explosions.", "900", new EPSParam("damage", "2"));
		flammableConfig.fillEnchantConfig(1, 65, Material.matchMaterial("MAGMA_CUBE_SPAWN_EGG"), "Burns the nearby terrain of where your arrow lands.", "100");
		hasteConfig.fillEnchantConfig(2, 75, Material.matchMaterial("GOLDEN_PICKAXE"), "May apply haste to you while mining.", "75 * 2^%lvl%", new EPSParam("chance", "%lvl%*0.1"));
		explosiveConfig.fillEnchantConfig(10, 75, Material.matchMaterial("TNT"), "Has a chance to blow up blocks.", "100 * 1.35^%lvl%", new EPSParam("chance", "%lvl%"));
		autosmeltConfig.fillEnchantConfig(10, 200, Material.matchMaterial("FURNACE"), "Has a chance to automatically smelt mined blocks.", "200 * 1.15^%lvl%", new EPSParam("chance", "%lvl%*10"));
		telepathyConfig.fillEnchantConfig(1, 800, Material.matchMaterial("ENDER_PEARL"), "Automatically transfers mined items into your inventory.", "1780", new EPSParam("chance", "100"));
		tokenBlocksConfig.fillEnchantConfig(20, 100, Material.matchMaterial("GOLD_BLOCK"), "Has a chance to reward you tokens for mining.", "150 * 1.1^%lvl%", new EPSParam("chance", "%lvl%*0.1"), new EPSParam("tokens", "%lvl%*5"), new EPSParam("random-range", "%lvl%"));
		moneyBlocksConfig.fillEnchantConfig(10, 100, Material.matchMaterial("BOOK"), "Has a chance to reward you money for mining.", "150 * 1.1^%lvl%", new EPSParam("chance", "%lvl%*0.1"), new EPSParam("money", "%lvl%*350"), new EPSParam("random-range", "%lvl%*15"));
		tokenCharityConfig.fillEnchantConfig(10, 100, Material.matchMaterial("BOOK"), "Has a chance to reward everyone tokens for your mining.", "150 * 1.1^%lvl%", new EPSParam("chance", "%lvl%*0.02"), new EPSParam("tokens", "%lvl%*5"), new EPSParam("random-range", "%lvl%*2"));
		charityConfig.fillEnchantConfig(10, 100, Material.matchMaterial("BOOK"), "Has a chance to reward everyone money for your mining.", "150 * 1.1^%lvl%", new EPSParam("chance", "%lvl%*0.02"), new EPSParam("money", "%lvl%*350"), new EPSParam("random-range", "%lvl%*15"));
		excavateConfig.fillEnchantConfig(10, 75, Material.matchMaterial("IRON_PICKAXE"), "Has a chance to destroy a cube of blocks at once.", "100 * 1.35^%lvl%", new EPSParam("chance", "%lvl%"));
		diamondConfig.fillEnchantConfig(10, 75, Material.matchMaterial("IRON_PICKAXE"), "Has a chance to destroy blocks in a diamond shape.", "100 * 1.35^%lvl%", new EPSParam("chance", "%lvl%"));
		veinMinerConfig.fillEnchantConfig(10, 50, Material.matchMaterial("VINES"), "Has a chance to break all blocks in a vein of ores.", "75 * 1.2^%lvl%", new EPSParam("chance", "%lvl%*4"));
		boostedConfig.fillEnchantConfig(3, 100, Material.matchMaterial("DIAMOND_BLOCK"), "Shift + right-click to gain a temporary Fortune boost.", "200 * %lvl%", new EPSParam("cooldown-seconds", "40 - 5 * %lvl%"), new EPSParam("duration-seconds", "4"));
	}
}
