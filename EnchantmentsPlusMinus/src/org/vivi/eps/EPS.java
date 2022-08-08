package org.vivi.eps;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
import org.vivi.eps.libs.FileUtils;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Dictionary;
import org.vivi.eps.util.Events;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.Wrapper;
import org.vivi.eps.util.economy.Economy;
import org.vivi.eps.util.economy.TokenEconomy;
import org.vivi.eps.util.economy.VaultEconomy;
import org.vivi.eps.visual.EnchantGUI;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.epsbuiltin.enchants.BuiltInEnchantsLoader;
import org.whyisthisnecessary.legacywrapper.LegacyWrapper;

import net.md_5.bungee.api.ChatColor;

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
	
	private static Dictionary dictionary = new Dictionary.Defaults();
	private static boolean legacy = Material.getMaterial("BLACK_STAINED_GLASS_PANE") == null;
	private static Economy economy = null;
	private static Updater updater = new Updater();
	private static ArrayList<Enchantment> registeredEnchants = new ArrayList<Enchantment>(Arrays.asList());
	private static final Enchantment NULL_ENCHANT = EPS.newEnchant("null", "null");
	
	@Override
	public void onEnable()
	{
		long startTime = System.currentTimeMillis();
		
		plugin = this;
		saveDefaultConfig();
		// This is for debugging
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getName().equals("vivisan"))
			{
				debug = true;
				getLogger().log(Level.INFO, "Debugging is enabled.");
			}
		
		configFile = new File(getDataFolder(), "config.yml");
		if (configFile.exists() && debug == true)
			configFile.delete();
		FileUtils.saveDefaultFile("/config.yml", configFile);
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
		FileUtils.saveDefaultFile("/lang.yml", languageFile);
		languageData = YamlConfiguration.loadConfiguration(languageFile);
		
		// Create GUIs File
		guisFile = new File(getDataFolder(), "guis.yml");
		if (guisFile.exists() && debug == true)
			guisFile.delete();
		FileUtils.saveDefaultFile("/guis.yml", guisFile);
		guisData = YamlConfiguration.loadConfiguration(guisFile);
		
		// Create Incompatibilities File
		incompatibilitiesFile = new File(getDataFolder(), "incompatibilities.yml");
		if (incompatibilitiesFile.exists() && debug == true)
			incompatibilitiesFile.delete();
		FileUtils.saveDefaultFile("/incompatibilities.yml", incompatibilitiesFile);
		incompatibilitiesData = YamlConfiguration.loadConfiguration(incompatibilitiesFile);
		
		// Create Gui Lore File
		guiLoreFile = new File(getDataFolder(), "gui_lore.yml");
		if (guiLoreFile.exists() && debug == true)
			guiLoreFile.delete();
		FileUtils.saveDefaultFile("/gui_lore.yml", guiLoreFile);
		guiLoreData = FileUtils.loadUTF8Configuration(guiLoreFile);
		
		// Create Data Files
		uuidDataStore = new File(dataFolder, "usernamestore.yml");
	    if (!uuidDataStore.exists())
	        FileUtils.createNewFile(uuidDataStore);	    	
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
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		
		
		// Initialize legacy support
		File file = new File(getDataFolder().getParentFile(), "LegacyWrapper.jar");
		if (EPS.onLegacy() && !file.exists()) try {
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(FileUtils.downloadFile(getDataFolder().getParentFile().getPath()+"/LegacyWrapper.jar", "https://github.com/dsdd/EnchantmentsPlusMinus/raw/main/Packs/LegacyWrapper.jar"))); } catch (Exception e) {}
		
		if (EPS.onLegacy() && !Bukkit.getPluginManager().isPluginEnabled("LegacyWrapper")) 
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Sorry, but it seems that there was an error downloading LegacyWrapper. "
					+ "To prevent data corruption, Enchantments+- will be forcefully disabled."
					+ "If this is unintentional, please report this to TreuGames for further investigation.");
			Bukkit.getPluginManager().disablePlugin(plugin);
		}
		
		EPS.registerReloader(this); // This has to be reloaded first.
		EPS.registerReloader(enchantMetaWriter);
		EPS.registerReloader(new EnchantGUI());
		EPS.registerReloader((new EPSConfiguration()).new EPSConfigReloader());
				
		// Finalize loading
		try 
		{
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		}
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
		}
		for (Enchantment enchant : Enchantment.values())
			EnchantMetaWriter.init(enchant);
		EPSConfiguration.reloadConfigurations();
		try {
			configData.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		EnchantGUI.setupInCPTS();
		for (Player p : Bukkit.getOnlinePlayers())
			EnchantGUI.setupGUI(p);
		
		// And load in the built-in enchants.
		
		new BuiltInEnchantsLoader().onEnable();
		        
        getLogger().log(Level.INFO, "Preload time: "+Long.toString(System.currentTimeMillis()-startTime)+" ms (rough approx.)");
		Language.sendMessage(Bukkit.getConsoleSender(), "startup-message");
	}
	
	@Override
	public void onDisable()
	{
		updater.autoUpdate();
	}
	
	/** Gets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static Economy getEconomy()
	{
		return economy;
	}
	
	/** Sets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static void setEconomy(Economy economy)
	{
		EPS.economy = economy;
	}
	
	/** Gets the plugin folder (/plugins/EnchantmentsPlusMinus)
	 * 
	 * @return The plugin folder
	 */
	public static File getPluginFolder()
	{
		return EPS.plugin.getDataFolder();
	}
	
	/** Gets the enchants folder (/plugins/EnchantmentsPlusMinus/enchants)
	 * 
	 * @return The enchants folder
	 */
	public static File getEnchantsFolder()
	{
		return EPS.enchantsFolder;
	}
	
	/** Gets the main dictionary of EPS.
	 * 
	 * @return The main dictionary of EPS.
	 */
	public static Dictionary getDictionary()
	{
		return dictionary;
	}
	
	/** Sets the main dictionary of EPS.
	 * 
	 * @return The main dictionary of EPS.
	 */
	public static void setDictionary(Dictionary dictionary)
	{
		EPS.dictionary = dictionary;
	}
	
	/** Returns true if the MC server is 1.12 or below.
	 * 
	 * @return Returns true if the MC server is 1.12 or below.
	 */
	public static boolean onLegacy()
	{
		return legacy;
	}
	
	/**
	 *  Registers the specified Reloadable to be ready for listening.
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
		guiLoreData = FileUtils.loadUTF8Configuration(guiLoreFile);
	}

	/**Returns the cost of the next specified levels of an enchant
	 * 
	 * @param type The type of cost increase used
	 * @param enchant The enchantment to calculate
	 * @param currentLevel The current enchantment level
	 * @param levels The amount of levels to be increased by
	 * @return Returns the cost of the next specified levels of an enchant
	 */
	public static double getCost(Enchantment enchant, int currentLevel, int levels)
	{
		EPSConfiguration config = EPSConfiguration.getConfiguration(enchant, true);
		
		Object cost = ConfigSettings.isGlobalCostEnabled() ? ConfigSettings.getGlobalCost() : config.get("cost");
	
		if (cost instanceof ConfigurationSection)
		{
			ConfigurationSection manualCost = (ConfigurationSection)cost;
			double val = 0;
			for (int i=0;i<levels;i++)
				val = val + manualCost.getLong(Integer.toString(currentLevel+1+i));
			return val;
		}
		else if (cost instanceof String)
		{
			double val = 0;
			for (int i=0;i<levels;i++)
				val = val + EPSConfiguration.eval(((String)cost).replaceAll("%lvl%", Integer.toString(currentLevel+1+i)));
			return val;
		}
		else
		{
			return Double.MAX_VALUE;
		}
	}

	/**Registers an enchant for use.
	 * Without registering an enchant, the enchant will stay unusable.
	 * 
	 * @param enchant The enchant you want to register.
	 * @return Returns if the registering was successful.
	 */
	public static boolean registerEnchant(Enchantment enchant)
	{
		if (enchant == NULL_ENCHANT)
			return false;
		registeredEnchants.add(enchant);
		File enchantfile = new File(enchantsFolder, getDictionary().getName(enchant)+".yml");
		if (enchantfile.exists())
			EPSConfiguration.getEPSConfigurations().put(enchant, EPSConfiguration.loadConfiguration(enchantfile));
		if (!Arrays.asList(Enchantment.values()).contains(enchant))
		{
			try
			{
				Enchantment.registerEnchantment(enchant);
				EnchantMetaWriter.init(enchant);
				Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN+"Registered enchant "+getDictionary().getName(enchant).toUpperCase()+"!");
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		}
		else
		{
			EnchantMetaWriter.init(enchant);
			return false;
		}
	}
	
	/**Register enchants for use.
	 * Without registering an enchant, the enchant will stay unusable.
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

	/** Gets all registered enchants, not including vanilla minecraft enchants.
	 * This list is in-mutable.
	 * 
	 * @return List of all registered enchants, not including vanilla minecraft enchants
	 */
	@SuppressWarnings("unchecked")
	public static List<Enchantment> getRegisteredEnchants() 
	{
		return (List<Enchantment>) registeredEnchants.clone();
	}

	/**Creates a custom enchant with the specified namespace and name and returns it
	 * 
	 * @param namespace The hard-coded name of this enchant
	 * @param name The default display name of this enchant
	 * @return A custom enchant with the specified namespace and name
	 */
	public static Enchantment newEnchant(String namespace, String name)
	{
		List<String> disabledEnchants = ConfigSettings.getDisabledEnchants();
		if (disabledEnchants.contains(name))
			return NULL_ENCHANT;
		if (disabledEnchants.contains(namespace))
			return NULL_ENCHANT;
		return onLegacy() ? LegacyWrapper.newEnchant(namespace, name.replaceAll(" ", "_"), 32767) : new Wrapper(namespace, name.replaceAll(" ", "_"), 32767);
	}

	/** Checks if the specified player has ever joined before.
	 * 
	 * @param playername The player
	 * @return The player's existence on the server.
	 */
	public static boolean playerExists(String username)
	{
		File file = getUserDataFile(username);
		return file != null;
	}

	/** Gets the UUID of the player belonging to this username.
	 * Will return null if the player has never joined.
	 * 
	 * @param playername The player
	 * @return The UUID of the player
	 */
	public static UUID getUUID(String username)
	{
		String stringUUID = uuidDataStoreData.getString(username);
		if (stringUUID == null)
			return null;
		return UUID.fromString(stringUUID);
	}

	/** Gets the data file of the specified player.
	 * 
	 * @param player The player in question
	 * @return The data file of the player
	 */
	public static File getUserDataFile(Player player)
	{
		return getUserDataFile(player.getUniqueId());
	}

	/** Gets the data file of the specified player.
	 * 
	 * @param username The player in question
	 * @return The data file of the user
	 */
	public static File getUserDataFile(String username)
	{
		return getUserDataFile(getUUID(username));
	}
	
	/** Gets the data file of the specified player.
	 * 
	 * @param username The player in question
	 * @return The data file of the user
	 */
	public static File getUserDataFile(UUID uuid)
	{
		if (uuid == null)
			return null;
		File dataFile = new File(dataFolder, uuid.toString()+".yml");
		return dataFile;
	}
	
}
