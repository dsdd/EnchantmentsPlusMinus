package org.vivi.eps.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.vivi.eps.EPS;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.yaml.YamlFile;

/**
 * An implementation of {@link Configuration}. Meant for use for easy writing
 * and reading of Enchantments+- configuration files.
 * 
 * @author vivisan
 *
 */
public class EnchantFile extends YamlFile<YamlConfiguration>
{
	private static final long serialVersionUID = 112837910008494619L;
	private Map<String, Map<Integer, Double>> cachedAutofillsMap = new HashMap<String, Map<Integer, Double>>();
	private int maxLevel = 0;
	private int scrapValue = 0;
	private Material upgradeIcon = Material.getMaterial("BOOK");
	private String enchantDescription = "Blank description";
	private String costExpression = "0";
	private Map<Integer, Double> cachedCosts = new HashMap<Integer, Double>();

	public EnchantFile(File parent, String child)
	{
		super(parent, child);
	}

	@Override
	public void loadYaml(YamlConfiguration configurationToLoad)
	{
		super.loadYaml(configurationToLoad);
		maxLevel = getInt("maxlevel", 0);
		scrapValue = getInt("scrapvalue", 0);
		upgradeIcon = getMaterialBySekai("upgradeicon");
		enchantDescription = getString("upgradedesc", "Blank description");
		costExpression = getString("cost", "0");
		Bukkit.getScheduler().runTaskAsynchronously(EPS.plugin, new Runnable() {
			@Override
			public void run()
			{
				for (int i = 1; i < 100; i++)
					cachedCosts.put(i, getCost(i));
			}
		});

	}

	public int getMaxLevel()
	{
		return maxLevel;
	}

	public double getScrapValue()
	{
		return scrapValue;
	}

	public Material getUpgradeIcon()
	{
		return upgradeIcon;
	}

	public String getUpgradeDescription()
	{
		return enchantDescription;
	}

	public double getCost(int enchantmentLevel)
	{
		Double cost = cachedCosts.get(enchantmentLevel);
		if (cost == null)
		{
			cost = Sekai.evaluateExpression(costExpression.replaceAll("%lvl%", Integer.toString(enchantmentLevel)));
			cachedCosts.put(enchantmentLevel, cost);
		}
		return cost;
	}

	public String getCostExpression()
	{
		return costExpression;
	}

	@Override
	public void set(String path, Object value)
	{
		super.set(path, value);
		// no way
		if (path == "maxlevel")
			maxLevel = (int) value;
		else if (path == "scrapvalue")
			scrapValue = (int) value;
		else if (path == "upgradeicon")
			upgradeIcon = value instanceof Material ? (Material) value : Material.matchMaterial((String) value);
		else if (path == "upgradedesc")
			enchantDescription = (String) value;
		else if (path == "cost")
		{
			costExpression = (String) value;
			cachedCosts.clear();
		}

	}

	@Override
	public void saveYaml(boolean isAsync)
	{
		set("maxlevel", maxLevel);
		set("scrapvalue", scrapValue);
		setBySekai("upgradeicon", upgradeIcon);
		set("upgradedesc", enchantDescription);
		set("cost", costExpression);
		super.saveYaml(isAsync);
	}

	/**
	 * Gets the requested Double by path by first replacing any %lvl% found in the
	 * string to the specified enchantmentLevel and then solving the expression.
	 * 
	 *
	 * @param enchantmentLevel What %lvl% should be replaced with
	 * @param path             Path of the String to get.
	 * @return Requested Double.
	 */
	public double getAutofilledDouble(int enchantmentLevel, String path)
	{
		String expression = getString(path).replaceAll("%lvl%", Integer.toString(enchantmentLevel));
		Map<Integer, Double> map = cachedAutofillsMap.get(path);
		if (map == null)
		{
			Map<Integer, Double> newMap = new HashMap<Integer, Double>();
			cachedAutofillsMap.put(path, newMap);
			map = newMap;
		}
		Double d = map.get(enchantmentLevel);
		double eval = Sekai.evaluateExpression(expression);
		if (d == null)
		{
			map.put(enchantmentLevel, eval);
			d = eval;
		}
		return d;
	}

	/**
	 * Gets the requested Integer by path by first replacing any %lvl% found in the
	 * string to the specified enchantmentLevel and then solving the expression.
	 * 
	 *
	 * @param enchantmentLevel What %lvl% should be replaced with
	 * @param path             Path of the String to get.
	 * @return Requested Integer.
	 */
	public int getAutofilledInt(Integer enchlvl, String path)
	{
		return (int) getAutofilledDouble(enchlvl, path);
	}

	/**
	 * Fills the max level, scrap value, upgrade icon and cost using provided values
	 * if they do not exist.
	 * 
	 * @param maxLevel           The max level of this enchant
	 * @param scrapValue         How much each level of this enchant is worth when
	 *                           scrapped
	 * @param upgradeIcon        The material shown in the enchant GUI representing
	 *                           this enchant
	 * @param enchantDescription The description for this enchant shown in item lore
	 *                           and in the Enchant GUI
	 * @param cost               The cost of this enchant unevaluated. (e.g.
	 *                           %lvl%*200 makes this enchant cost 400 for level 2)
	 */
	public void fillEnchantConfig(int maxLevel, int scrapValue, Material upgradeIcon, String enchantDescription,
			String cost)
	{
		fill("maxlevel", maxLevel);
		fill("scrapvalue", scrapValue);
		fill("upgradeicon",
				((upgradeIcon == null || upgradeIcon == Material.AIR) ? Material.BOOK : upgradeIcon).name());
		fill("upgradedesc", enchantDescription == null ? "Blank description" : enchantDescription);
		fill("cost", cost == null ? "9999999" : cost);
		saveYaml();
	}

	/**
	 * Fills the max level, scrap value, upgrade icon and cost using provided values
	 * if they do not exist.
	 * 
	 * @param maxLevel    The max level of this enchant
	 * @param scrapValue  How much each level of this enchant is worth when scrapped
	 * @param upgradeIcon The material shown in the enchant GUI representing this
	 *                    enchant
	 * @param description The description for this enchant shown in the enchant GUI
	 * @param cost        The cost of this enchant unevaluated. (e.g. %lvl%*200
	 *                    makes this enchant cost 400 for level 2)
	 * @param params      Extra parameters to be added.
	 */
	public void fillEnchantConfig(int maxLevel, int scrapValue, Material upgradeIcon, String description, String cost,
			Parameter... params)
	{
		fillEnchantConfig(maxLevel, scrapValue, upgradeIcon, description, cost);
		for (Parameter param : params)
			fill(param.key, param.value);
	}
	
	private void fill(String key, Object value)
	{
		if (!isSet(key))
			set(key, value);
	}

	public static class Parameter
	{
		public String key = null;
		public Object value = null;

		public Parameter(String key, Object value)
		{
			this.key = key;
			this.value = value;
		}

		public Parameter key(String key)
		{
			this.key = key;
			return this;
		}

		public Parameter value(Object value)
		{
			this.value = value;
			return this;
		}
	}
}
