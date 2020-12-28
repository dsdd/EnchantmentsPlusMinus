package org.whyisthisnecessary.eps.internal;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.whyisthisnecessary.eps.Main;

public class InternalTokenManager {

	private static Plugin plugin;
	
	public InternalTokenManager(Main plugin)
	{
		InternalTokenManager.plugin = plugin;
	}
	
	public static boolean PlayerExists(String p)
	{
		File DataFolder = new File(plugin.getDataFolder(), "data");
		File userstore = new File(DataFolder, "usernamestore.yml");
		FileConfiguration usconfig = YamlConfiguration.loadConfiguration(userstore);
		String uid = usconfig.getString(p);
		File datafile = new File(DataFolder, uid+".yml");
		if (datafile.exists()) 
		    return true;
		else
			return false;
	}
	
	public static void SetTokens(String p, Integer amount)
	{
		File DataFolder = new File(plugin.getDataFolder(), "data");
		File userstore = new File(DataFolder, "usernamestore.yml");
		FileConfiguration usconfig = YamlConfiguration.loadConfiguration(userstore);
		String uid = usconfig.getString(p);
		File datafile = new File(DataFolder, uid+".yml");
		if (datafile.exists()) 
	    {
	        FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(datafile);
	        	dataconfig.set("tokens", amount);
	        	try {
					dataconfig.save(datafile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
	    }
	}
	
	public static void ChangeTokens(String p, Integer amount)
	{
		File DataFolder = new File(plugin.getDataFolder(), "data");
		File userstore = new File(DataFolder, "usernamestore.yml");
		FileConfiguration usconfig = YamlConfiguration.loadConfiguration(userstore);
		String uid = usconfig.getString(p);
		File datafile = new File(DataFolder, uid+".yml");
		if (datafile.exists()) 
	    {
	        FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(datafile);
	        	dataconfig.set("tokens", dataconfig.getInt("tokens") + amount);
	        	try {
					dataconfig.save(datafile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
	    }
	}
	
	public static Integer GetTokens(String p)
	{
		File DataFolder = new File(plugin.getDataFolder(), "data");
		File userstore = new File(DataFolder, "usernamestore.yml");
		FileConfiguration usconfig = YamlConfiguration.loadConfiguration(userstore);
		String uid = usconfig.getString(p);
		File datafile = new File(DataFolder, uid+".yml");
		if (datafile.exists()) 
	    {
	        FileConfiguration dataconfig = YamlConfiguration.loadConfiguration(datafile);
	        return dataconfig.getInt("tokens");
			
	    }
	    else
	    {
	    	return 0;
	    }
	}
}
