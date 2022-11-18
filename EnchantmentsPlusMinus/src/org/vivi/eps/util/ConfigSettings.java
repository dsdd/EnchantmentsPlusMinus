package org.vivi.eps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.vivi.eps.EPS;
import org.vivi.eps.api.Reloadable;

public class ConfigSettings implements Reloadable
{

	private static boolean isAutoUpdating = false;
	private static boolean showEnchants = true;
	private static boolean showEnchantDescriptions = true;
	private static boolean abbreviateLargeNumbers = true;
	private static boolean useRomanNumerals = false;
	private static boolean anvilCombiningEnabled = true;
	private static boolean useVaultEconomy = true;
	private static boolean useActionBar = true;
	private static boolean globalCostEnabled = false;
	private static String globalCostExpression = "69420*%lvl%";
	private static String enchantLoreColor = ChatColor.translateAlternateColorCodes('&', "&9");
	private static List<Material> applyFortuneOn = new ArrayList<Material>();
	private static boolean playerKillRewardEnabled = true;
	private static double playerKillRewardMin = 25;
	private static double playerKillRewardMax = 50;
	private static boolean mobKillRewardEnabled = true;
	private static double mobKillRewardMin = 5;
	private static double mobKillRewardMax = 10;
	private static boolean miningRewardEnabled = true;
	private static double miningRewardMin = 25;
	private static double miningRewardMax = 50;
	private static int miningRewardBlocksToBreak = 1000;
	private static Map<String, String> enchantSpecificLoreColors = new HashMap<String, String>();
	private static List<Material> loreExemptions = new ArrayList<Material>();
	private static List<String> disabledEnchants = new ArrayList<String>();
	private static boolean enchantGuiOnRightClick = true;
	private static List<Material> enchantGuiDisableIfHolding = new ArrayList<Material>();

	@Override
	public void reload()
	{
		isAutoUpdating = EPS.configFile.getBoolean("auto-update");
		showEnchants = EPS.configFile.getBoolean("show-enchants");
		showEnchantDescriptions = EPS.configFile.getBoolean("show-enchant-descriptions");
		abbreviateLargeNumbers = EPS.configFile.getBoolean("abbreviate-large-numbers");
		useRomanNumerals = EPS.configFile.getBoolean("use-roman-numerals");
		anvilCombiningEnabled = EPS.configFile.getBoolean("anvil-combining-enabled");
		useVaultEconomy = EPS.configFile.getBoolean("use-vault-economyy");
		useActionBar = EPS.configFile.getBoolean("use-action-bar-instead-of-chat-when-inventory-full");
		globalCostEnabled = EPS.configFile.getBoolean("global-cost.enabled");
		globalCostExpression = EPS.configFile.getString("global-cost.cost");
		enchantLoreColor = ChatColor.translateAlternateColorCodes('&', EPS.configFile.getString("enchant-lore-color", ""));
		applyFortuneOn = EPS.configFile.getMaterialListBySekai("apply-fortune-on");
		playerKillRewardEnabled = EPS.configFile.getBoolean("player-kill-reward.enabled");
		playerKillRewardMin = EPS.configFile.getDouble("player-kill-reward.min");
		playerKillRewardMax = EPS.configFile.getDouble("player-kill-reward.max");
		mobKillRewardEnabled = EPS.configFile.getBoolean("mob-kill-reward.enabled");
		mobKillRewardMin = EPS.configFile.getDouble("mob-kill-reward.min");
		mobKillRewardMax = EPS.configFile.getDouble("mob-kill-reward.max");
		miningRewardEnabled = EPS.configFile.getBoolean("mining-reward.enabled");
		miningRewardMin = EPS.configFile.getDouble("mining-reward.min");
		miningRewardMax = EPS.configFile.getDouble("mining-reward.max");
		miningRewardBlocksToBreak = EPS.configFile.getInt("mining-reward.blockstobreak");
		enchantGuiOnRightClick = EPS.configFile.getBoolean("enchant-gui-shortcut.on-right-click");
		enchantGuiDisableIfHolding = EPS.configFile.getMaterialListBySekai("enchant-gui-shortcut.disable-if-holding");

		ConfigurationSection enchantSpecificLoreColors = EPS.configFile
				.getConfigurationSection("enchant-specific-lore-color");
		for (String key : enchantSpecificLoreColors.getKeys(false))
			getEnchantSpecificLoreColors().put(key, enchantSpecificLoreColors.getString(key));

		loreExemptions = EPS.configFile.getMaterialListBySekai("do-not-add-lore-to");
		disabledEnchants = EPS.configFile.getStringList("disabled-enchants");
	}

	public static boolean isAutoUpdating()
	{
		return isAutoUpdating;
	}

	public static boolean isShowEnchants()
	{
		return showEnchants;
	}

	public static boolean isShowEnchantDescriptions()
	{
		return showEnchantDescriptions;
	}

	public static boolean isAbbreviateLargeNumbers()
	{
		return abbreviateLargeNumbers;
	}

	public static boolean isUseRomanNumerals()
	{
		return useRomanNumerals;
	}

	public static boolean isAnvilCombiningEnabled()
	{
		return anvilCombiningEnabled;
	}

	public static boolean isUseVaultEconomy()
	{
		return useVaultEconomy;
	}

	public static boolean isUseActionBar()
	{
		return useActionBar;
	}

	public static boolean isGlobalCostEnabled()
	{
		return globalCostEnabled;
	}

	public static String getGlobalCostExpression()
	{
		return globalCostExpression;
	}

	public static String getEnchantLoreColor()
	{
		return enchantLoreColor;
	}

	public static boolean isPlayerKillRewardEnabled()
	{
		return playerKillRewardEnabled;
	}

	public static List<Material> getApplyFortuneOn()
	{
		return applyFortuneOn;
	}

	public static double getPlayerKillRewardMin()
	{
		return playerKillRewardMin;
	}

	public static double getPlayerKillRewardMax()
	{
		return playerKillRewardMax;
	}

	public static boolean isMobKillRewardEnabled()
	{
		return mobKillRewardEnabled;
	}

	public static double getMobKillRewardMin()
	{
		return mobKillRewardMin;
	}

	public static double getMobKillRewardMax()
	{
		return mobKillRewardMax;
	}

	public static boolean isMiningRewardEnabled()
	{
		return miningRewardEnabled;
	}

	public static double getMiningRewardMin()
	{
		return miningRewardMin;
	}

	public static double getMiningRewardMax()
	{
		return miningRewardMax;
	}

	public static int getMiningRewardBlocksToBreak()
	{
		return miningRewardBlocksToBreak;
	}

	public static Map<String, String> getEnchantSpecificLoreColors()
	{
		return enchantSpecificLoreColors;
	}
	
	public static List<Material> getLoreExemptions()
	{
		return loreExemptions;
	}

	public static List<String> getDisabledEnchants()
	{
		return disabledEnchants;
	}

	public static boolean isEnchantGuiOnRightClick()
	{
		return enchantGuiOnRightClick;
	}

	public static List<Material> getEnchantGuiDisableIfHolding()
	{
		return enchantGuiDisableIfHolding;
	}
}
