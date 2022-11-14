package org.vivi.eps;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.vivi.eps.api.EnchantFile;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.command.EPSCommand;
import org.vivi.eps.command.EnchantsCommand;
import org.vivi.eps.command.PayTokensCommand;
import org.vivi.eps.command.ScrapCommand;
import org.vivi.eps.command.TokensCommand;
import org.vivi.eps.dependencies.PlaceholderAPIHook;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Events;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.EnchantWrapper;
import org.vivi.eps.util.economy.Economy;
import org.vivi.eps.util.economy.TokenEconomy;
import org.vivi.eps.util.economy.VaultEconomy;
import org.vivi.eps.visual.EnchantGUI;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.epsbuiltin.enchants.BuiltInEnchantsLoader;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.dependencies.Metrics;
import org.vivi.sekai.dependencies.VaultHook;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.yaml.YamlFile;

public class EPS extends JavaPlugin implements Reloadable
{

	public static EPS plugin;
	public static File dataFolder;
	public static File enchantsFolder;
	public static YamlFile<YamlConfiguration> configFile;
	public static YamlFile<YamlConfiguration> languageFile;
	public static YamlFile<YamlConfiguration> incompatibilitiesFile;
	public static YamlFile<YamlConfiguration> guisFile;
	public static YamlFile<YamlConfiguration> uuidDataStore;
	public static Map<Enchantment, EnchantFile> enchantmentFilesMap = new HashMap<Enchantment, EnchantFile>();
	public static Set<Set<Enchantment>> incompatibilities = new HashSet<Set<Enchantment>>();
	public static Map<List<Material>, String> guis = new HashMap<List<Material>, String>();
	public static Map<Integer, Double> globalCostsMap = new HashMap<Integer, Double>();
	public static EnchantsCommand enchantsCommand;
	public static EnchantMetaWriter enchantMetaWriter;
	public static Logger logger;

	private static Economy economy = null;
	private static Updater updater = new Updater();
	private static Events epsEvents = new Events();
	private static Enchantment NULL_ENCHANT = null;

	@Override
	public void onEnable()
	{
		try
		{
			long startTime = System.currentTimeMillis();
			plugin = this;
			logger = getLogger();

			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
			logger.log(Level.FINE, "Enabled acceptance of new Enchantment registrations");

			reload();

			updater.makeCompatible();

			ConfigSettings configSettings = new ConfigSettings();
			configSettings.reload();
			registerReloadable(configSettings);
			EPS.registerReloadable(Language.lang);

			// Load dependencies
			new Metrics(plugin, 9735);
			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
				new PlaceholderAPIHook();

			if (VaultHook.hook())
				logger.log(Level.INFO, "Successfully hooked into Vault.");
			economy = ConfigSettings.isUseVaultEconomy() ? new VaultEconomy() : new TokenEconomy();

			// Load commands
			enchantsCommand = new EnchantsCommand();
			enchantMetaWriter = new EnchantMetaWriter();
			Bukkit.getPluginCommand("eps").setExecutor(new EPSCommand());
			Bukkit.getPluginCommand("enchants").setExecutor(enchantsCommand);
			Bukkit.getPluginCommand("paytokens").setExecutor(new PayTokensCommand());
			Bukkit.getPluginCommand("scrap").setExecutor(new ScrapCommand());
			Bukkit.getPluginCommand("tokens").setExecutor(new TokensCommand());

			// Load events
			Bukkit.getPluginManager().registerEvents(new EnchantGUI(), this);
			Bukkit.getPluginManager().registerEvents(epsEvents, this);

			EPS.registerReloadable(this); // This has to be reloaded first.
			EPS.registerReloadable(enchantMetaWriter);
			EPS.registerReloadable(new EnchantGUI());

			enchantMetaWriter.reload();

			configFile.saveYaml();
			epsEvents.reload();
			for (Player player : Bukkit.getOnlinePlayers())
			{
				epsEvents.onJoin(new PlayerJoinEvent(player, null));
				EnchantGUI.setupGUI(player);
			}

			// And load in the built-in enchants.

			NULL_ENCHANT = EPS.newEnchant("null", "null");
			new BuiltInEnchantsLoader().onEnable();

			logger.log(Level.INFO,
					"Load time: " + Long.toString(System.currentTimeMillis() - startTime) + " ms (rough approx.)");

			EPS.reloadConfigs(); // hotfix for startup
		} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		updater.autoUpdate();
	}

	/**
	 * Gets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static Economy getEconomy()
	{
		return economy;
	}

	/**
	 * Sets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static void setEconomy(Economy economy)
	{
		EPS.economy = economy;
	}

	/**
	 * Gets the plugin folder (/plugins/EnchantmentsPlusMinus)
	 * 
	 * @return The plugin folder
	 */
	public static File getPluginFolder()
	{
		return EPS.plugin.getDataFolder();
	}

	/**
	 * Gets the enchants folder (/plugins/EnchantmentsPlusMinus/enchants)
	 * 
	 * @return The enchants folder
	 */
	public static File getEnchantsFolder()
	{
		return EPS.enchantsFolder;
	}

	/**
	 * Registers the specified Reloadable to be ready for listening.
	 */
	public static void registerReloadable(Reloadable r)
	{
		Reloadable.addReloadable(r);
	}

	/**
	 * Fires reload() in all registered Reloadable classes.
	 */
	public static void reloadConfigs()
	{
		for (Reloadable r : Reloadable.CLASSES)
			r.reload();
	}

	@Override
	public void reload()
	{
		if (configFile == null)
			configFile = new YamlFile<YamlConfiguration>(getDataFolder(), "config.yml");
		if (languageFile == null)
			languageFile = new YamlFile<YamlConfiguration>(getDataFolder(), "lang.yml", Charset.forName("UTF-8"));
		if (guisFile == null)
			guisFile = new YamlFile<YamlConfiguration>(getDataFolder(), "guis.yml");
		if (incompatibilitiesFile == null)
			incompatibilitiesFile = new YamlFile<YamlConfiguration>(getDataFolder(), "incompatibilities.yml");
		if (uuidDataStore == null)
			uuidDataStore = new YamlFile<YamlConfiguration>(dataFolder, "usernamestore.yml");
		if (dataFolder == null)
			dataFolder = new File(getDataFolder(), "data");
		if (enchantsFolder == null)
			enchantsFolder = new File(getDataFolder(), "enchants");

		if (!dataFolder.exists())
			dataFolder.mkdirs();

		if (!enchantsFolder.exists())
			enchantsFolder.mkdirs();

		Sekai.saveDefaultFile(EPS.plugin, "/config.yml", configFile);
		Sekai.saveDefaultFile(EPS.plugin, "/lang.yml", languageFile);
		Sekai.saveDefaultFile(EPS.plugin, "/guis.yml", guisFile);
		Sekai.saveDefaultFile(EPS.plugin, "/incompatibilities.yml", incompatibilitiesFile);

		if (!uuidDataStore.exists())
			try
			{
				uuidDataStore.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		configFile.loadYaml(new YamlConfiguration());
		uuidDataStore.loadYaml(new YamlConfiguration());
		languageFile.loadYaml(new YamlConfiguration());
		incompatibilitiesFile.loadYaml(new YamlConfiguration());
		guisFile.loadYaml(new YamlConfiguration());

		Bukkit.getScheduler().runTask(plugin, new Runnable() {

			@Override
			public void run()
			{
				ConfigurationSection configurationSection = incompatibilitiesFile
						.getConfigurationSection("incompatibilities");
				incompatibilities.clear();

				if (configurationSection == null)
				{
					logger.log(Level.WARNING,
							"Invalid incompatibilities.yml file! Delete the current one to generate a new one.");
					return;
				}
				for (Map.Entry<String, Object> entry : configurationSection.getValues(false).entrySet())
				{
					if (entry.getValue() instanceof List)
					{
						List<?> written = (List<?>) entry.getValue();
						Set<Enchantment> incompatibleEnchants = new HashSet<Enchantment>();

						logger.log(Level.FINE, "Setting incompatibilities " + entry.getKey());

						for (Object enchantKey : written)
							if (enchantKey instanceof String)
							{
								incompatibleEnchants.add(EnchantmentInfo.findEnchantByKey((String) enchantKey));
								logger.log(Level.FINER, "Added " + enchantKey);
							}

						incompatibilities.add(incompatibleEnchants);
					}
				}

			}

		});

		Bukkit.getScheduler().runTask(plugin, new Runnable() {

			@Override
			public void run()
			{
				for (Map.Entry<String, Object> entry : guisFile.getConfigurationSection("guis").getValues(false).entrySet())
					if (entry.getValue() instanceof ConfigurationSection)
					{
						ConfigurationSection configurationSection = (ConfigurationSection) entry.getValue();

						List<Material> materials = new ArrayList<Material>();
						for (String materialName : configurationSection.getStringList("items"))
							if (Material.matchMaterial(materialName) != null)
								materials.add(Material.matchMaterial(materialName));

						if (!materials.isEmpty())
							guis.put(materials, entry.getKey());
					}

			}
		});
	}

	/**
	 * Returns the cost of the next specified levels of an enchant
	 * 
	 * @param type         The type of cost increase used
	 * @param enchant      The enchantment to calculate
	 * @param currentLevel The current enchantment level
	 * @param levels       The amount of levels to be increased by
	 * @return Returns the cost of the next specified levels of an enchant
	 */
	public static double getCost(Enchantment enchant, int currentLevel, int levels)
	{
		EnchantFile enchantFile = getEnchantFile(enchant);

		if (ConfigSettings.isGlobalCostEnabled())
		{
			String globalCostExpression = ConfigSettings.getGlobalCostExpression();
			double val = 0;
			for (int i = 0; i < levels; i++)
			{
				int level = currentLevel + 1 + i;
				Double cost = globalCostsMap.get(level);
				if (cost == null)
				{
					cost = Sekai.evaluateExpression(globalCostExpression.replaceAll("%lvl%", Integer.toString(level)));
					globalCostsMap.put(level, cost);
				}
				val += cost;
			}
			return val;

		}

		double val = 0;
		for (int i = 0; i < levels; i++)
			val += enchantFile.getCost(currentLevel + 1 + i);

		return val;
	}

	/**
	 * Registers an enchant for use. Without registering an enchant, the enchant
	 * will stay unusable.
	 * 
	 * @param enchant The enchant you want to register.
	 * @return Returns if the registering was successful.
	 */
	public static boolean registerEnchant(Enchantment enchant)
	{
		if (enchant == NULL_ENCHANT)
			return false;

		getEnchantFile(enchant);

		if (!Arrays.asList(Enchantment.values()).contains(enchant))
		{
			try
			{
				Enchantment.registerEnchantment(enchant);
				EnchantMetaWriter.prepareLore(enchant);
				logger.log(Level.FINE, "Registered " + EnchantmentInfo.getKey(enchant).toUpperCase());
				return true;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		} else
		{
			EnchantMetaWriter.prepareLore(enchant);
			return false;
		}
	}

	public static EnchantFile getEnchantFile(Enchantment enchant)
	{
		return getEnchantFile(enchant, true);
	}

	public static EnchantFile getEnchantFile(Enchantment enchant, boolean autoCreateIfNull)
	{
		EnchantFile enchantFile = enchantmentFilesMap.get(enchant);

		if ((enchantFile == null || !enchantFile.exists()))
		{
			enchantFile = new EnchantFile(enchantsFolder, EnchantmentInfo.getKey(enchant) + ".yml");
			
			if (autoCreateIfNull)
				try
				{
					enchantFile.createNewFile();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			
			if (enchantFile.exists())
			{
				enchantFile.loadYaml(new YamlConfiguration());
				enchantmentFilesMap.put(enchant, enchantFile);
			}
		}
		return enchantFile;
	}

	/**
	 * Register enchants for use. Without registering an enchant, the enchant will
	 * stay unusable.
	 * 
	 * @param enchants The enchants you want to register.
	 * @return Returns if the all enchantments were successfully registered.
	 */
	public static boolean registerEnchants(Enchantment... enchants)
	{
		boolean success = true;
		for (Enchantment enchant : enchants)
			if (registerEnchant(enchant) == false)
				success = false;
		return success;
	}

	/**
	 * Creates a custom enchant with the specified namespace and name and returns it
	 * 
	 * @param namespace The hard-coded name of this enchant
	 * @param name      The default display name of this enchant
	 * @return A custom enchant with the specified namespace and name
	 */
	public static Enchantment newEnchant(String namespace, String name)
	{
		List<String> disabledEnchants = ConfigSettings.getDisabledEnchants();
		if (disabledEnchants.contains(name))
			return NULL_ENCHANT;
		if (disabledEnchants.contains(namespace))
			return NULL_ENCHANT;
		return Sekai.getMCVersion() < 13 ? new EnchantWrapper.Legacy(namespace, name.replaceAll(" ", "_"))
				: new EnchantWrapper(namespace, name.replaceAll(" ", "_"));
	}

}
