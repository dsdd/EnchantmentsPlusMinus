package org.vivi.eps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.vivi.eps.EPS;
import org.vivi.eps.api.Reloadable;

public class ConfigSettings implements Reloadable {

	private static boolean isAutoUpdating = false;
	private static boolean showEnchants = true;
	private static boolean showEnchantDescriptions = true;
	private static boolean useRomanNumerals = false;
	private static boolean openEnchantGuiOnRightClick = true;
	private static boolean anvilCombiningEnabled = true;
	private static boolean useVaultEconomy = true;
	private static boolean useActionBar = true;
	private static boolean globalCostTypeEnabled = false;
	private static String globalCostType = "exponential";
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
	private static List<String> disabledEnchants = new ArrayList<String>();
	
	@Override
	public void reload() 
	{
		isAutoUpdating = EPS.configData.getBoolean("auto-update");
		showEnchants = EPS.configData.getBoolean("show-enchants");
		showEnchantDescriptions = EPS.configData.getBoolean("show-enchant-descriptions");
		useRomanNumerals = EPS.configData.getBoolean("use-roman-numerals");
		openEnchantGuiOnRightClick = EPS.configData.getBoolean("open-enchant-gui-on-right-click");
		anvilCombiningEnabled = EPS.configData.getBoolean("anvil-combining-enabled");
		useVaultEconomy = EPS.configData.getBoolean("use-vault-economyy");
		useActionBar = EPS.configData.getBoolean("use-action-bar-instead-of-chat-when-inventory-full");
		globalCostTypeEnabled = EPS.configData.getBoolean("global-cost-type.enabled");
		globalCostType = EPS.configData.getString("global-cost-type.type");
		enchantLoreColor = EPS.configData.getString("enchant-lore-color");
		
		getApplyFortuneOn().clear();
		List<String> applyFortuneOnStringList = EPS.configData.getStringList("apply-fortune-on");
		for (String name : applyFortuneOnStringList)
		{
			Material material = Material.matchMaterial(name);
			if (material == null)
				material = Material.matchMaterial(name, true);
			if (material != null)
				getApplyFortuneOn().add(material);
		}
		
		playerKillRewardEnabled = EPS.configData.getBoolean("player-kill-reward.enabled");
		playerKillRewardMin = EPS.configData.getInt("player-kill-reward.min");
		playerKillRewardMax = EPS.configData.getInt("player-kill-reward.max");
		mobKillRewardEnabled = EPS.configData.getBoolean("mob-kill-reward.enabled");
		mobKillRewardMin = EPS.configData.getInt("mob-kill-reward.min");
		mobKillRewardMax = EPS.configData.getInt("mob-kill-reward.max");
		miningRewardEnabled = EPS.configData.getBoolean("mining-reward.enabled");
		miningRewardMin = EPS.configData.getInt("mining-reward.min");
		miningRewardBlocksToBreak = EPS.configData.getInt("mining-reward.blockstobreak");
		
		ConfigurationSection enchantSpecificLoreColors = EPS.configData.getConfigurationSection("enchant-specific-lore-color");
		for (String key : enchantSpecificLoreColors.getKeys(false))
			getEnchantSpecificLoreColors().put(key, enchantSpecificLoreColors.getString(key));
		
		disabledEnchants = EPS.configData.getStringList("disabled-enchants");
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

	public static boolean isGlobalCostTypeEnabled() 
	{
		return globalCostTypeEnabled;
	}

	public static String getGlobalCostType() 
	{
		return globalCostType;
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

}
