package org.vivi.eps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.eps.PackLoader;
import org.eps.autoupdater.AutoUpdate;
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
import org.vivi.eps.economy.Economy;
import org.vivi.eps.economy.TokenEconomy;
import org.vivi.eps.economy.VaultEconomy;
import org.vivi.eps.item.ItemEvents;
import org.vivi.eps.util.Dictionary;
import org.vivi.eps.util.Language;
import org.vivi.eps.visual.EnchantGUI;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.eps.workbench.AnvilUpdate;

import com.google.common.io.Files;

import net.md_5.bungee.api.ChatColor;

public class EPS extends JavaPlugin implements Listener, CommandExecutor, Reloadable {

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
	public static YamlConfiguration guiLoreData;
	public static EnchantsCommand enchantsCommand;
	public static EnchantMetaWriter enchantMetaWriter;
	public static boolean debug = false;
	
	private static Dictionary dictionary = new Dictionary.Defaults();
	private static boolean legacy = Material.getMaterial("BLACK_STAINED_GLASS_PANE") == null;
	private static boolean moneyOverrideTokens = false;
	private static Economy economy = null;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
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
		saveDefaultFile("/config.yml", configFile);
		configData = YamlConfiguration.loadConfiguration(configFile);
		moneyOverrideTokens = configData.getBoolean("use-money-economy-instead-of-tokens");
		economy = moneyOverrideTokens ? new VaultEconomy() : new TokenEconomy();
		
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
		saveDefaultFile("/lang.yml", languageFile);
		languageData = YamlConfiguration.loadConfiguration(languageFile);
		
		// Create GUIs File
		guisFile = new File(getDataFolder(), "guis.yml");
		if (guisFile.exists() && debug == true)
			guisFile.delete();
		saveDefaultFile("/guis.yml", guisFile);
		guisData = YamlConfiguration.loadConfiguration(guisFile);
		
		// Create Incompatibilities File
		incompatibilitiesFile = new File(getDataFolder(), "incompatibilities.yml");
		if (incompatibilitiesFile.exists() && debug == true)
			incompatibilitiesFile.delete();
		saveDefaultFile("/incompatibilities.yml", incompatibilitiesFile);
		incompatibilitiesData = YamlConfiguration.loadConfiguration(incompatibilitiesFile);
		
		// Create Gui Lore File
		guiLoreFile = new File(getDataFolder(), "gui_lore.yml");
		if (guiLoreFile.exists() && debug == true)
			guiLoreFile.delete();
		saveDefaultFile("/gui_lore.yml", guiLoreFile);
		guiLoreData = loadUTF8Configuration(guiLoreFile);
		
		// Create Data Files
		uuidDataStore = new File(dataFolder, "usernamestore.yml");
	    if (!uuidDataStore.exists())
	        createNewFile(uuidDataStore);	    	
	    uuidDataStoreData = YamlConfiguration.loadConfiguration(uuidDataStore);
	    
	    // Create Enchant Files	    
	    for (File file : getFiles("/enchants"))
	    {
	    	File file1 = new File(enchantsFolder, file.getName());
	    	if (!file1.exists())
	    	try {
				Files.move(file, file1);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    // Load Updater
	    Updater.makeCompatible();
	    
	    // Load dependencies
	    new Metrics(plugin, 9735);
	    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
	    new PlaceholderAPIHook();
		VaultHook.setupEconomy();
		
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
		Bukkit.getPluginManager().registerEvents(enchantMetaWriter, this);
		Bukkit.getPluginManager().registerEvents(new ItemEvents(), this);
		Bukkit.getPluginManager().registerEvents(new AnvilUpdate(), this);
		
		
		// Initialize legacy support
		File file = new File(getDataFolder().getParentFile(), "LegacyWrapper.jar");
		if (EPS.onLegacy() && !file.exists()) try {
			Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(downloadFile(getDataFolder().getParentFile().getPath()+"/LegacyWrapper.jar", "https://github.com/dsdd/EnchantmentsPlusMinus/raw/main/Packs/LegacyWrapper.jar"))); } catch (Exception e) {}
		
		if (EPS.onLegacy() && !Bukkit.getPluginManager().isPluginEnabled("LegacyWrapper")) 
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Sorry, but it seems that there was an error downloading LegacyWrapper. "
					+ "To prevent data corruption, Enchantments+- will be forcefully disabled."
					+ "If this is unintentional, please report this to TreuGames for further investigation.");
			Bukkit.getPluginManager().disablePlugin(plugin);
		}
				
		// Register reloadables
		EPS.registerReloader(this);
		EPS.registerReloader(enchantMetaWriter);
		EPS.registerReloader(Language.lang);
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
		new PackLoader(this);
		try {
			configData.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		EPSConfiguration.reloadConfigs();
		EnchantGUI.setupInCPTS();
		for (Player p : Bukkit.getOnlinePlayers())
			EnchantGUI.setupGUI(p);
		Language.sendMessage(Bukkit.getConsoleSender(), "startup-message");
	}
	
	@Override
	public void onDisable()
	{
		try {
			
		new AutoUpdate().onEnable();	
		
		}	catch (Exception e) {}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) throws IOException
	{
		Player p = e.getPlayer();
		File dataFile = new File(dataFolder, p.getUniqueId().toString()+".yml");
		uuidDataStoreData.set(p.getName(), p.getUniqueId().toString());
		try {
			uuidDataStoreData.save(uuidDataStore);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (!dataFile.exists())
		    createNewFile(dataFile);
		
		FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
		dataConfig.set("tokens", dataConfig.get("tokens", 0));
		dataConfig.save(dataFile);
		EnchantGUI.setupGUI(e.getPlayer());
	}
	
	/** Saves a file to the specified destination
	 * if it does not exist
	 * 
	 * @param resource The path to copy from
	 * @param dest The file you want to copy to
	 */
	public static void saveDefaultFile(String resource, File dest)
	{
        if (!dest.exists())
        {
            try {
				dest.createNewFile();
		        copyFile(resource, dest);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	/** Copies a file from the specified file path
	 * to the specified file
	 * 
	 * @param str The file path to copy from
	 * @param dest The file to paste into
	 */
	private static void copyFile(String str, File dest) {
		try {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = plugin.getClass().getResourceAsStream(str);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) != -1) {
	            os.write(buffer, 0, length);
	        }
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    finally {
	        is.close();
	        os.close();
	    }
	}
	catch(IOException e) {}
	}
	
	/** Creates a new file without having to add try/catch statement to reduce lines.
	 * For convenience and readability, really.
	 * 
	 * @param file
	 * @return Result of file creation
	 */
	public static boolean createNewFile(File file)
	{
		try {
			file.createNewFile();
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/** Downloads a file from the specified URL.
	 * 
	 * @param localFileName The name of the file
	 * @param fromUrl The URL to download from
	 * @return The file
	 */
	protected static File downloadFile(String localFileName, String fromUrl) { try {
	    File localFile = new File(localFileName);
	    if (!localFile.exists()) {
	    	localFile.createNewFile();
	    }
	    URL url = new URL(fromUrl);
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(localFileName));
	    URLConnection conn = url.openConnection();
	    ((HttpURLConnection) conn).setRequestMethod("GET"); 
	    conn.setRequestProperty("User-Agent", "  Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
	    conn.connect();
	    InputStream in = conn.getInputStream();
	    byte[] buffer = new byte[16384];

	    int numRead;
	    while ((numRead = in.read(buffer)) != -1) {
	        out.write(buffer, 0, numRead);
	    }
	    if (in != null) {
	        in.close();
	    }
	    if (out != null) {
	        out.close();
	    }
	    out.flush();
	    
	    return localFile;   } catch (Exception e){return null;}
	}
	
	/** Gets the JAR file of a plugin
	 * 
	 * @param pl The plugin
	 * @return The file
	 */
	private static File getJarFile(Plugin pl)
	{ 
		try 
		{
			Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
			getFileMethod.setAccessible(true);
			File file = (File) getFileMethod.invoke((JavaPlugin) pl);
			return file; 
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace();  
			return null; 
		}
	}
	
	/** Gets files from a folder
	 * 
	 * @param path The folder path
	 * @return The files inside the folder
	 */
	private List<File> getFiles(String path)
	{
		try {
		final File jarFile = getJarFile(plugin);
        List<File> files = new ArrayList<File>(Arrays.asList());
	    if(jarFile.isFile()) {  // Run with JAR file
	        JarFile jar = new JarFile(jarFile);
	        final Enumeration<JarEntry> entries = jar.entries();
	        while(entries.hasMoreElements()) {
	        	JarEntry entry = entries.nextElement();
	            final String name = entry.getName();
	            File f = new File(name);
	            if (name.startsWith((path + "/").substring(1))) { 
	                files.add(getTempFile(jar, entry, f.getName()));
	            }
	        }
				jar.close();
	    }
	    return files;
		}
	    catch (IOException e)
	    {
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	/** Creates a new file from the specified ZipEntry
	 * 
	 * @param file The ZipFile to look into
	 * @param entry The ZipEntry to copy from
	 * @param name The name to give the file
	 * @return The file
	 */
	private File getTempFile(ZipFile file, ZipEntry entry, String name)
	{
		File tempfolder = new File(EPS.dataFolder, "Temp");
		File temp = new File(tempfolder, name);
	    if (!tempfolder.exists()) 
	    	tempfolder.mkdirs();
	    if (!temp.exists()) 
	    	EPS.createNewFile(temp);
	    
		try 
		{
		    InputStream is = file.getInputStream(entry);
		    OutputStream os = null;
		    
		    try 
		    {
		        os = new FileOutputStream(tempfolder+"/"+name);
		        byte[] buffer = new byte[1024];
		        int length;
		        while ((length = is.read(buffer)) != -1)
		            os.write(buffer, 0, length);
		    }
		    catch (Exception e)
		    {
		    	e.printStackTrace();
		    }
		    finally 
		    {
		        is.close();
		        os.close();
		    }
		}
		catch (Exception e) 
		{ 
		    e.printStackTrace();
		}
		return (temp);
	}
	
	public static YamlConfiguration loadUTF8Configuration(File file)
	{
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config;
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
		moneyOverrideTokens = configData.getBoolean("use-money-economy-instead-of-tokens");
		guiLoreData = loadUTF8Configuration(guiLoreFile);
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
