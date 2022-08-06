package org.eps.autoupdater;

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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.vivi.eps.Main;

public class AutoUpdate {

	public void onEnable()
	{
        if (Main.Config.getBoolean("auto-update") == false) return;
		File file = downloadFile(Main.DataFolder.getPath()+"/version.txt", "https://raw.githubusercontent.com/dsdd/EnchantmentsPlusMinus/main/VERSION");
		String ver = readFile(file).substring(0, ((int)file.length())-1);
		Plugin pl = Main.plugin;
		JavaPlugin plugin = (JavaPlugin) Main.plugin;
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
