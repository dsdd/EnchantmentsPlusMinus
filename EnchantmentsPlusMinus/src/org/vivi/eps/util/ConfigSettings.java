package org.vivi.eps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.vivi.eps.EPS;
import org.vivi.eps.api.Reloadable;
import org.vivi.sekai.Sekai;

public class ConfigSettings implements Reloadable
{

	private static boolean isAutoUpdating = false;
	private static boolean showEnchants = true;
	private static boolean showEnchantDescriptions = true;
	private static boolean abbreviateLargeNumbers = true;
	private static boolean useRomanNumerals = false;
	private static boolean openEnchantGuiOnRightClick = true;
	private static boolean anvilCombiningEnabled = true;
	private static boolean useVaultEconomy = true;
	private static boolean useActionBar = true;
	private static boolean globalCostEnabled = false;
	private static String globalCostExpression = "69420*%lvl%";
	private static String enchantLoreColor = "&9";
	private static List<Material> applyFortuneOn = new ArrayList<Material>();
	private static boolean playerKillRewardEnabled = true;
	private static int playerKillRewardMin = 25;
	private static int playerKillRewardMax = 50;
	private static boolean mobKillRewardEnabled = true;
	private static int mobKillRewardMin = 5;
	private static int mobKillRewardMax = 10;
	private static boolean miningRewardEnabled = true;
	private static int miningRewardMin = 25;
	private static int miningRewardMax = 50;
	private static int miningRewardBlocksToBreak = 1000;
	private static Map<String, String> enchantSpecificLoreColors = new HashMap<String, String>();
	private static List<String> loreExemptions = new ArrayList<String>();
	private static List<String> disabledEnchants = new ArrayList<String>();

	@Override
	public void reload()
	{
		isAutoUpdating = EPS.configFile.getBoolean("auto-update");
		showEnchants = EPS.configFile.getBoolean("show-enchants");
		showEnchantDescriptions = EPS.configFile.getBoolean("show-enchant-descriptions");
		abbreviateLargeNumbers = EPS.configFile.getBoolean("abbreviate-large-numbers");
		useRomanNumerals = EPS.configFile.getBoolean("use-roman-numerals");
		openEnchantGuiOnRightClick = EPS.configFile.getBoolean("open-enchant-gui-on-right-click");
		anvilCombiningEnabled = EPS.configFile.getBoolean("anvil-combining-enabled");
		useVaultEconomy = EPS.configFile.getBoolean("use-vault-economyy");
		useActionBar = EPS.configFile.getBoolean("use-action-bar-instead-of-chat-when-inventory-full");
		globalCostEnabled = EPS.configFile.getBoolean("global-cost.enabled");
		globalCostExpression = EPS.configFile.getString("global-cost.cost");
		enchantLoreColor = EPS.configFile.getString("enchant-lore-color");

		getApplyFortuneOn().clear();
		List<String> applyFortuneOnStringList = EPS.configFile.getStringList("apply-fortune-on");
		for (String name : applyFortuneOnStringList)
		{
			Material material = Material.matchMaterial(name);
			if (material == null && Sekai.getMCVersion() > 12)
				material = Material.matchMaterial(name, true);
			if (material != null)
				getApplyFortuneOn().add(material);
		}

		playerKillRewardEnabled = EPS.configFile.getBoolean("player-kill-reward.enabled");
		playerKillRewardMin = EPS.configFile.getInt("player-kill-reward.min");
		playerKillRewardMax = EPS.configFile.getInt("player-kill-reward.max");
		mobKillRewardEnabled = EPS.configFile.getBoolean("mob-kill-reward.enabled");
		mobKillRewardMin = EPS.configFile.getInt("mob-kill-reward.min");
		mobKillRewardMax = EPS.configFile.getInt("mob-kill-reward.max");
		miningRewardEnabled = EPS.configFile.getBoolean("mining-reward.enabled");
		miningRewardMin = EPS.configFile.getInt("mining-reward.min");
		miningRewardBlocksToBreak = EPS.configFile.getInt("mining-reward.blockstobreak");

		ConfigurationSection enchantSpecificLoreColors = EPS.configFile
				.getConfigurationSection("enchant-specific-lore-color");
		for (String key : enchantSpecificLoreColors.getKeys(false))
			getEnchantSpecificLoreColors().put(key, enchantSpecificLoreColors.getString(key));

		loreExemptions = EPS.configFile.getStringList("do-not-add-lore-to");
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

	public static boolean isOpenEnchantGuiOnRightClick()
	{
		return openEnchantGuiOnRightClick;
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

	public static int getPlayerKillRewardMin()
	{
		return playerKillRewardMin;
	}

	public static int getPlayerKillRewardMax()
	{
		return playerKillRewardMax;
	}

	public static boolean isMobKillRewardEnabled()
	{
		return mobKillRewardEnabled;
	}

	public static int getMobKillRewardMin()
	{
		return mobKillRewardMin;
	}

	public static int getMobKillRewardMax()
	{
		return mobKillRewardMax;
	}

	public static boolean isMiningRewardEnabled()
	{
		return miningRewardEnabled;
	}

	public static int getMiningRewardMin()
	{
		return miningRewardMin;
	}

	public static int getMiningRewardMax()
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

	public static List<String> getDisabledEnchants()
	{
		return disabledEnchants;
	}

	public static List<String> getLoreExemptions()
	{
		return loreExemptions;
	}

}
