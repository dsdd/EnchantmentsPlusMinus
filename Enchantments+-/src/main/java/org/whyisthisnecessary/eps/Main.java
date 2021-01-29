package org.whyisthisnecessary.eps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.eps.BuiltInPackParser;
import org.eps.autoupdater.AutoUpdate;
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.command.EPSCommand;
import org.whyisthisnecessary.eps.command.EnchantsCommand;
import org.whyisthisnecessary.eps.command.PayTokensCommand;
import org.whyisthisnecessary.eps.command.ScrapCommand;
import org.whyisthisnecessary.eps.command.TokensCommand;
import org.whyisthisnecessary.eps.dependencies.Metrics;
import org.whyisthisnecessary.eps.dependencies.PlaceholderAPIHook;
import org.whyisthisnecessary.eps.dependencies.VaultHook;
import org.whyisthisnecessary.eps.item.ItemEvents;
import org.whyisthisnecessary.eps.legacy.LegacyUtil;
import org.whyisthisnecessary.eps.util.DataUtil;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.visual.EnchantGUI;
import org.whyisthisnecessary.eps.visual.EnchantMetaWriter;
import org.whyisthisnecessary.eps.workbench.AnvilUpdate;

import com.google.common.io.Files;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

	public static Main plugin;
	public static File DataFolder;
	public static File EnchantsFolder;
	public static File ConfigFile;
	public static File LangFile;
	public static File InCPTFile;
	public static File GuisFile;
	public static File UUIDDataStore;
	public static FileConfiguration Config;
	public static FileConfiguration LangConfig;
	public static FileConfiguration UUIDDataStoreConfig;
	public static FileConfiguration InCPTConfig;	
	public static FileConfiguration GuisConfig;
	public static EnchantsCommand EnchantsCMD;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
		Config = this.getConfig();
		ConfigFile = new File(getDataFolder(), "config.yml");
		
		// Create Data Folder
		DataFolder = new File(getDataFolder(), "data");
        if (!DataFolder.exists())
            DataFolder.mkdirs();	
		
		// Create Enchant Folder
		EnchantsFolder = new File(getDataFolder(), "enchants");
		if (!EnchantsFolder.exists())
			EnchantsFolder.mkdirs();

		// Create Language File
		LangFile = new File(getDataFolder(), "lang.yml");
		saveDefaultFile("/lang.yml", LangFile);
		LangConfig = YamlConfiguration.loadConfiguration(LangFile);
		
		// Create GUIs File
		GuisFile = new File(getDataFolder(), "guis.yml");
		saveDefaultFile("/guis.yml", GuisFile);
		GuisConfig = YamlConfiguration.loadConfiguration(GuisFile);
		
		// Create Incompatibilities File
		InCPTFile = new File(getDataFolder(), "incompatibilities.yml");
		saveDefaultFile("/incompatibilities.yml", InCPTFile);
		InCPTConfig = YamlConfiguration.loadConfiguration(InCPTFile);
		
		// Create Data Files
		UUIDDataStore = new File(DataFolder, "usernamestore.yml");
	    if (!UUIDDataStore.exists())
	        createNewFile(UUIDDataStore);
	    UUIDDataStoreConfig = YamlConfiguration.loadConfiguration(UUIDDataStore);
	    
	    // Create Enchant Files	    
	    for (File file : getFiles("/enchants"))
	    {
	    	File file1 = new File(EnchantsFolder, file.getName());
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
		EnchantsCMD = new EnchantsCommand();
		Bukkit.getPluginCommand("eps").setExecutor(new EPSCommand());
		Bukkit.getPluginCommand("enchants").setExecutor(EnchantsCMD);
		Bukkit.getPluginCommand("paytokens").setExecutor(new PayTokensCommand());
		Bukkit.getPluginCommand("scrap").setExecutor(new ScrapCommand());
		Bukkit.getPluginCommand("tokens").setExecutor(new TokensCommand());
		EnchantsCommand.setupGUIs();
		
		// Load events
		Bukkit.getPluginManager().registerEvents(new EnchantGUI(), this);
		Bukkit.getPluginManager().registerEvents(new EnchantMetaWriter(), this);
		Bukkit.getPluginManager().registerEvents(new ItemEvents(), this);
		Bukkit.getPluginManager().registerEvents(new AnvilUpdate(), this);
		
		
		// Initialize legacy support
		LegacyUtil.checkLegacy();
		File file = new File(getDataFolder().getParentFile(), "LegacyWrapper.jar");
		if (LegacyUtil.isLegacy() && !file.exists())
		{
			File lw = downloadFile(getDataFolder().getParentFile().getPath()+"/LegacyWrapper.jar", "https://github.com/dsdd/EnchantmentsPlusMinus/raw/main/Packs/LegacyWrapper.jar"); try{        
			Plugin plugin1 = Bukkit.getPluginManager().loadPlugin(lw);
			Bukkit.getPluginManager().enablePlugin(plugin1); } catch (Exception e) {}
		}
		LegacyUtil.initialize(this);
		
		// Finalize loading
		new BuiltInPackParser(this);
		for (Player p : Bukkit.getOnlinePlayers())
		{
			EnchantGUI.setupGUI(p);
		}
		if (new File(getDataFolder(), "packs").exists())
		{
			new File(getDataFolder(), "packs").delete();
		}
	
		EnchantMetaWriter.registerEnchantNames();
		DataUtil.saveConfig(Main.Config, Main.ConfigFile);
		ConfigUtil.reloadConfigs();
		Bukkit.getConsoleSender().sendMessage(LangUtil.getLangMessage("startup-message"));
	}
	
	@Override
	public void onDisable()
	{
		try {
			
		new AutoUpdate().onEnable();	
		
		}	catch (Exception e) {}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player p = e.getPlayer();
		File DataFile = new File(DataFolder, p.getUniqueId().toString()+".yml");
		UUIDDataStoreConfig.set(p.getName(), p.getUniqueId().toString());
		DataUtil.saveConfig(UUIDDataStoreConfig, UUIDDataStore);
		
		if (!DataFile.exists())
		    createNewFile(DataFile);
		
		FileConfiguration DataConfig = YamlConfiguration.loadConfiguration(DataFile);
		addDefault(DataConfig, "tokens", 0);
		DataUtil.saveConfig(DataConfig, DataFile);
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
	
	/**Adds a value into the specified path from the specified configuration
	 * if one does not already exist
	 * 
	 * @param config The FileConfiguration to use
	 * @param path The path to set
	 * @param value The value
	 */
	private void addDefault(FileConfiguration config, String path, Object value)
	{
		if (config.get(path) == null)
		{
			config.set(path, value);
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
		File tempfolder = new File(Main.DataFolder, "Temp");
		File temp = new File(tempfolder, name);
	    if (!tempfolder.exists()) 
	    	tempfolder.mkdirs();
	    if (!temp.exists()) 
	    	Main.createNewFile(temp);
	    
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
}
