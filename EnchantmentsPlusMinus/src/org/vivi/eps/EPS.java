package org.vivi.eps;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.command.EPSCommand;
import org.vivi.eps.command.EnchantsCommand;
import org.vivi.eps.command.PayTokensCommand;
import org.vivi.eps.command.ScrapCommand;
import org.vivi.eps.command.TokensCommand;
import org.vivi.eps.dependencies.Metrics;
import org.vivi.eps.dependencies.PlaceholderAPIHook;
import org.vivi.eps.dependencies.VaultHook;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.EnchantDictionary;
import org.vivi.eps.util.Events;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.Wrapper;
import org.vivi.eps.util.economy.Economy;
import org.vivi.eps.util.economy.TokenEconomy;
import org.vivi.eps.util.economy.VaultEconomy;
import org.vivi.eps.visual.EnchantGUI;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.epsbuiltin.enchants.BuiltInEnchantsLoader;
import org.vivi.sekai.Sekai;

public class EPS extends JavaPlugin implements Reloadable {

	public static EPS plugin;
	public static File dataFolder;
	public static File enchantsFolder;
	public static File configFile;
	public static File languageFile;
	public static File incompatibilitiesFile;
	public static File guisFile;
	public static File guiLoreFile;
	public static File uuidDataStore;
	public static FileConfiguration configData;
	public static FileConfiguration languageData;
	public static FileConfiguration uuidDataStoreData;
	public static FileConfiguration incompatibilitiesData;
	public static FileConfiguration guisData;
	public static FileConfiguration guiLoreData;
	public static EnchantsCommand enchantsCommand;
	public static EnchantMetaWriter enchantMetaWriter;
	public static boolean debug = false;

	private static EnchantDictionary dictionary = new EnchantDictionary.Defaults();
	private final static int version = (Bukkit.getVersion().contains("1.8")) ? 8
			: ((Bukkit.getVersion().contains("1.9")) ? 9
					: (Bukkit.getVersion().contains("1.10")) ? 10
							: (Bukkit.getVersion().contains("1.11") ? 11
									: (Bukkit.getVersion().contains("1.12") ? 12
											: (Bukkit.getVersion().contains("1.13") ? 13
													: (Bukkit.getVersion().contains("1.14") ? 14
															: (Bukkit.getVersion().contains("1.15") ? 15
																	: (Bukkit.getVersion().contains("1.16") ? 16
																			: (Bukkit.getVersion().contains("1.17") ? 17
																					: (Bukkit.getVersion()
																							.contains("1.18")
																									? 18
																									: (Bukkit
																											.getVersion()
																											.contains(
																													"1.19") ? 19
																															: 20))))))))));

	private static Economy economy = null;
	private static Updater updater = new Updater();
	private static Events epsEvents = new Events();
	private static ArrayList<Enchantment> registeredEnchants = new ArrayList<Enchantment>(Arrays.asList());
	private static HashMap<Enchantment, HashMap<Integer, Double>> cachedCosts = new HashMap<Enchantment, HashMap<Integer, Double>>();
	private static Enchantment NULL_ENCHANT = null;

	@Override
	public void onEnable()
	{
		long startTime = System.currentTimeMillis();
		plugin = this;
		
		
		// This is for debugging
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().equals("vivisan"))
			{
				debug = true;
				getLogger().log(Level.INFO, "Debugging is enabled.");
			}
		
		saveDefaultConfig();
		configFile = new File(getDataFolder(), "config.yml");
		
		if (configFile.exists() && debug == true)
			configFile.delete();
		Sekai.saveDefaultFile(EPS.plugin, "/config.yml", configFile);
		configData = YamlConfiguration.loadConfiguration(configFile);

		// Create Data Folder
		dataFolder = new File(getDataFolder(), "data");
		if (!dataFolder.exists())
			dataFolder.mkdirs();

		// Create Enchant Folder
		enchantsFolder = new File(getDataFolder(), "enchants");
		if (!enchantsFolder.exists())
			enchantsFolder.mkdirs();

		// Create Language File
		languageFile = new File(getDataFolder(), "lang.yml");
		if (languageFile.exists() && debug == true)
			languageFile.delete();
		Sekai.saveDefaultFile(EPS.plugin, "/lang.yml", languageFile);
		languageData = YamlConfiguration.loadConfiguration(languageFile);

		// Create GUIs File
		guisFile = new File(getDataFolder(), "guis.yml");
		if (guisFile.exists() && debug == true)
			guisFile.delete();
		Sekai.saveDefaultFile(EPS.plugin, "/guis.yml", guisFile);
		guisData = YamlConfiguration.loadConfiguration(guisFile);

		// Create Incompatibilities File
		incompatibilitiesFile = new File(getDataFolder(), "incompatibilities.yml");
		if (incompatibilitiesFile.exists() && debug == true)
			incompatibilitiesFile.delete();
		Sekai.saveDefaultFile(EPS.plugin, "/incompatibilities.yml", incompatibilitiesFile);
		incompatibilitiesData = YamlConfiguration.loadConfiguration(incompatibilitiesFile);

		// Create Gui Lore File
		guiLoreFile = new File(getDataFolder(), "gui_lore.yml");
		if (guiLoreFile.exists() && debug == true)
			guiLoreFile.delete();
		Sekai.saveDefaultFile(EPS.plugin, "/gui_lore.yml", guiLoreFile);
		guiLoreData = Sekai.loadUTF8Configuration(guiLoreFile);

		// Create Data Files
		uuidDataStore = new File(dataFolder, "usernamestore.yml");
		if (!uuidDataStore.exists())
			Sekai.createNewFile(uuidDataStore);
		uuidDataStoreData = YamlConfiguration.loadConfiguration(uuidDataStore);

		// Load Updater
		updater.makeCompatible();

		// Load Configuration Files
		ConfigSettings configSettings = new ConfigSettings();
		configSettings.reload();
		registerReloader(configSettings);
		EPS.registerReloader(Language.lang);

		// Load dependencies
		new Metrics(plugin, 9735);
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
			new PlaceholderAPIHook();

		VaultHook.setupEconomy();
		economy = ConfigSettings.isUseVaultEconomy() ? new VaultEconomy() : new TokenEconomy();

		// Load commands
		enchantsCommand = new EnchantsCommand();
		Bukkit.getPluginCommand("eps").setExecutor(new EPSCommand());
		Bukkit.getPluginCommand("enchants").setExecutor(enchantsCommand);
		Bukkit.getPluginCommand("paytokens").setExecutor(new PayTokensCommand());
		Bukkit.getPluginCommand("scrap").setExecutor(new ScrapCommand());
		Bukkit.getPluginCommand("tokens").setExecutor(new TokensCommand());
		EnchantsCommand.setupGUIs();

		// Load events
		enchantMetaWriter = new EnchantMetaWriter();
		Bukkit.getPluginManager().registerEvents(new EnchantGUI(), this);
		Bukkit.getPluginManager().registerEvents(epsEvents, this);

		EPS.registerReloader(this); // This has to be reloaded first.
		EPS.registerReloader(enchantMetaWriter);
		EPS.registerReloader(new EnchantGUI());

		// Finalize loading
		try
		{
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		for (Enchantment enchant : Enchantment.values())
			EnchantMetaWriter.init(enchant);
		try
		{
			configData.save(configFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		EnchantGUI.setupInCPTS();
		epsEvents.reload();
		for (Player player : Bukkit.getOnlinePlayers())
		{
			try
			{
				epsEvents.onJoin(new PlayerJoinEvent(player, null));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			EnchantGUI.setupGUI(player);
		}

		// And load in the built-in enchants.

		NULL_ENCHANT = EPS.newEnchant("null", "null");
		new BuiltInEnchantsLoader().onEnable();

		getLogger().log(Level.INFO,
				"Preload time: " + Long.toString(System.currentTimeMillis() - startTime) + " ms (rough approx.)");
		Language.sendMessage(Bukkit.getConsoleSender(), "startup-message");
		
		EPS.reloadConfigs(); // hotfix for startup
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
	 * Gets the main dictionary of EPS.
	 * 
	 * @return The main dictionary of EPS.
	 */
	public static EnchantDictionary getDictionary()
	{
		return dictionary;
	}

	/**
	 * Sets the main dictionary of EPS.
	 * 
	 * @return The main dictionary of EPS.
	 */
	public static void setDictionary(EnchantDictionary dictionary)
	{
		EPS.dictionary = dictionary;
	}

	/**
	 * Returns the version of the Minecraft server in numerical form. e.g. 1.8.6,
	 * 1.8.2 and 1.8.8 will all return 8. 1.16, 1.14.2 and 1.19.84 will return 16,
	 * 14 and 19 respectively.
	 * 
	 * @return Returns the version of the Minecraft server in numerical form.
	 */
	public static int getMCVersion()
	{
		return version;
	}

	/**
	 * Registers the specified Reloadable to be ready for listening.
	 */
	public static void registerReloader(Reloadable r)
	{
		Reloadable.addReloader(r);
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
		plugin.reloadConfig();
		configData = plugin.getConfig();
		uuidDataStoreData = YamlConfiguration.loadConfiguration(uuidDataStore);
		languageData = YamlConfiguration.loadConfiguration(languageFile);
		incompatibilitiesData = YamlConfiguration.loadConfiguration(incompatibilitiesFile);
		guisData = YamlConfiguration.loadConfiguration(guisFile);
		guiLoreData = Sekai.loadUTF8Configuration(guiLoreFile);
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
		EPSConfiguration config = EPSConfiguration.getConfiguration(enchant);

		Object cost = ConfigSettings.isGlobalCostEnabled() ? ConfigSettings.getGlobalCost() : config.get("cost");

		if (!(cost instanceof ConfigurationSection) && !(cost instanceof String))
			return Double.MAX_VALUE;

		double val = 0;
		for (int i = 0; i < levels; i++)
			val = val + getCost(enchant, currentLevel + 1 + i, cost);

		return val;
	}

	/**
	 * Tries accessing cached costs of the specified level of the enchant If there
	 * is no existing cached cost, calculate it manually using provided cost object.
	 * Cost object should either be a ConfigurationSection where costs are manually
	 * stated or a String equation.
	 * 
	 * @param enchant The enchantment to calculate
	 * @param level   The level of the enchant to calculate
	 * @param cost    Cost equation to use if there is no cached cost.
	 * @return Returns the cost of the next specified levels of an enchant
	 */
	private static double getCost(Enchantment enchant, int level, Object cost)
	{
		if (!cachedCosts.containsKey(enchant))
			cachedCosts.put(enchant, new HashMap<Integer, Double>());

		Double cachedCost = cachedCosts.get(enchant).get(level);
		if (cachedCost != null)
			return cachedCost;

		double val = Double.MAX_VALUE;
		if (cost instanceof ConfigurationSection)
			val = ((ConfigurationSection) cost).getLong(Integer.toString(level));
		else if (cost instanceof String)
			val = EPSConfiguration.eval(((String) cost).replaceAll("%lvl%", Integer.toString(level)));

		cachedCosts.get(enchant).put(level, val);
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
		registeredEnchants.add(enchant);
		File enchantfile = new File(enchantsFolder, getDictionary().getName(enchant) + ".yml");
		if (enchantfile.exists())
			EPSConfiguration.loadConfiguration(enchantfile, enchant);
		if (!Arrays.asList(Enchantment.values()).contains(enchant))
		{
			try
			{
				Enchantment.registerEnchantment(enchant);
				EnchantMetaWriter.init(enchant);
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Registered enchant "
						+ getDictionary().getName(enchant).toUpperCase() + "!");
				return true;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		} else
		{
			EnchantMetaWriter.init(enchant);
			return false;
		}
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
	 * Gets all registered enchants, not including vanilla minecraft enchants. This
	 * list is in-mutable.
	 * 
	 * @return List of all registered enchants, not including vanilla minecraft
	 *         enchants
	 */
	@SuppressWarnings("unchecked")
	public static List<Enchantment> getRegisteredEnchants()
	{
		return (List<Enchantment>) registeredEnchants.clone();
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
		return getMCVersion() < 13 ? new Wrapper.LegacyWrapper(namespace, name.replaceAll(" ", "_"))
				: new Wrapper(namespace, name.replaceAll(" ", "_"));
	}

}
