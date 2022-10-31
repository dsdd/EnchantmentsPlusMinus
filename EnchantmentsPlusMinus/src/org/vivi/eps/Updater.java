package org.vivi.eps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.vivi.eps.util.ConfigSettings;

public class Updater {

	/** Makes updates between versions compatible.
	 */
	public void makeCompatible()
	{
		// If you want to migrate from 1.6r and below to the latest version, use a 1.9r-7 and below release as an intermediate.
		
		
		// START 1.9r-8 to 1.9r-9 conversion
		
		FileConfiguration configData = EPS.configData;
		
		if (configData.contains("show-enchant-lore"))
		{
			configData.set("show-enchants", configData.get("show-enchant-lore", true));
			configData.set("show-enchant-lore", null);
		}
		
		if (configData.contains("use-money-economy-instead-of-tokens"))
		{
			configData.set("use-vault-economy", configData.get("use-money-economy-instead-of-tokens", true));
			configData.set("use-money-economy-instead-of-tokens", null);
		}
		
		if (configData.contains("use-action-bar-instead-of-chat-inventory-full"))
		{
			configData.set("use-action-bar-instead-of-chat-when-inventory-full", configData.get("use-action-bar-instead-of-chat-inventory-full", true));
			configData.set("use-action-bar-instead-of-chat-inventory-full", null);
		}
		
		if (configData.contains("applyfortuneon"))
		{
			configData.set("apply-fortune-on", configData.get("applyfortuneon"));
			configData.set("applyfortuneon", null);
		}
		
		if (configData.contains("playerkilltokens"))
		{
			configData.set("player-kill-reward", configData.get("playerkilltokens"));
			configData.set("playerkilltokens", null);
		}
		
		if (configData.contains("mobkilltokens"))
		{
			configData.set("mob-kill-reward", configData.get("mobkilltokens"));
			configData.set("mobkilltokens", null);
		}
		
		if (configData.contains("miningtokens"))
		{
			configData.set("mining-reward", configData.get("miningtokens"));
			configData.set("miningtokens", null);
		}
		
		if (configData.contains("custom-lore-color"))
		{
			configData.set("enchant-specific-lore-color", configData.get("custom-lore-color"));
			configData.set("custom-lore-color", null);
		}
		
		if (configData.contains("global-cost-type"))
		{
			configData.set("global-cost-type", null);
			configData.set("global-cost.enabled", false);
			configData.set("global-cost.cost", "69420*%lvl%");
		}
		
		if (EPS.languageData.contains("upgradedpickaxe"))
		{
			EPS.languageData.set("upgraded-item", EPS.languageData.get("upgradedpickaxe"));
			EPS.languageData.set("upgradedpickaxe", null);
			try {
				EPS.languageData.save(EPS.languageFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		setDefault("abbreviate-large-numbers", true);
		configData.set("do-not-add-lore-to", new ArrayList<String>(Arrays.asList(new String[] {"an item e.g. BEDROCK that you do not want lore added to due to plugin interference"})));
		configData.set("use-custom-fortune", null);
		configData.set("show-vanilla-enchants-in-lore", null);
		
		for (String key : EPS.incompatibilitiesData.getKeys(false))
			if (EPS.incompatibilitiesData.contains(key+".items"))
				EPS.incompatibilitiesData.set(key+".items", null);
		
		
		
		// Convert enchant configs
		for (File file : (EPS.enchantsFolder.listFiles()))
		{
			FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
			String costType = configuration.getString("cost.type");
			if (costType == null)
				continue;
			if (costType.equalsIgnoreCase("linear"))
				configuration.set("cost", Double.toString(configuration.getDouble("cost.value"))+" * %lvl% + "+Double.toString(configuration.getDouble("cost.startvalue")));
			else if (costType.equalsIgnoreCase("exponential"))
				configuration.set("cost", Double.toString(configuration.getDouble("cost.startvalue"))+" * "+Double.toString(configuration.getDouble("cost.multi"))+"^(%lvl%-1)");
			else if (costType.equalsIgnoreCase("manual"))
				configuration.set("cost.type", null);
			try {
				configuration.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			configData.save(EPS.configFile);
			EPS.incompatibilitiesData.save(EPS.incompatibilitiesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// END
		
		
	}
	
	public static void setDefault(String path, Object value)
	{
		if (!EPS.configData.isSet(path))
		{
			EPS.configData.set(path, value);
		}
	}
	
	public void autoUpdate()
	{
        if (!ConfigSettings.isAutoUpdating()) return;
		File file = downloadFile(EPS.dataFolder.getPath()+"/version.txt", "https://raw.githubusercontent.com/dsdd/EnchantmentsPlusMinus/main/VERSION");
		String ver = readFile(file).substring(0, ((int)file.length())-1);
		Plugin pl = EPS.plugin;
		JavaPlugin plugin = (JavaPlugin) EPS.plugin;
		Method getFileMethod = null;
		try {
			getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();		} catch (SecurityException e) {
			e.printStackTrace();
		}
		getFileMethod.setAccessible(true);
		File file1 = null;
		try {
			file1 = (File) getFileMethod.invoke(plugin);
		} catch (IllegalAccessException e) {
			e.printStackTrace();	} catch (IllegalArgumentException e) {
			e.printStackTrace();		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "You are running EnchantmentsPlusMinus "+pl.getDescription().getVersion()+".");
		if (!pl.getDescription().getVersion().equalsIgnoreCase(ver))
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Downloading updated plugin JAR... ("+ver+")");
			downloadFile(file1.getPath(), "https://github.com/dsdd/EnchantmentsPlusMinus/releases/latest/download/Enchantments+-.jar");
			Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN + "Finished downloading!");
		}
		file.delete();
	}
	
	private File downloadFile(String localFileName, String fromUrl) { try {
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
	
	public String readFile(File file) {
		String str = null;
		try {
		str = new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) { e.printStackTrace(); }
	    return str;
	}
}
