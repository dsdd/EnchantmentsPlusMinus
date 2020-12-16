package org.whyisthisnecessary.eps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantHandler {
	
	private FileConfiguration config;
	private JavaPlugin plugin;
	private FileConfiguration lang;
	private ScriptEngineManager mgr = new ScriptEngineManager();
    private ScriptEngine engine = mgr.getEngineByName("JavaScript");
    private Plugin pl;
	
	public EnchantHandler(JavaPlugin plugin)
	{
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder().getParentFile().getParentFile(), "config.yml");
        if (file.exists()) {
        config = YamlConfiguration.loadConfiguration(file); }
        else
        {
        	Bukkit.broadcastMessage(ChatColor.RED + "Critical error: Could not find config file for EPS!");
        	Bukkit.getPluginManager().disablePlugin(plugin);
        }
        File file1 = new File(plugin.getDataFolder().getParentFile().getParentFile(), "lang.yml");
        if (file1.exists()) {
        lang = YamlConfiguration.loadConfiguration(file1); }
        else
        {
        	Bukkit.broadcastMessage(ChatColor.RED + "Critical error: Could not find lang file for EPS!");
        	Bukkit.getPluginManager().disablePlugin(plugin);
        }
        pl = Bukkit.getPluginManager().getPlugin("EnchantmentsPlusMinus");
	}
	
	public FileConfiguration getConfig()
	{
		return config;
	}
	
	public void setDefaultConfigMessage(String msg, String text)
	{
		if (lang.get("packs."+plugin.getName().toLowerCase()+".messages."+msg) == null)
		{
			lang.set("packs."+plugin.getName().toLowerCase()+".messages."+msg, text);
			File langfile = new File(plugin.getDataFolder().getParentFile().getParentFile(), "lang.yml");
			if (langfile.exists())
			{
				try {
					lang.save(langfile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setConfigMessage(String msg, String text)
	{
		lang.set("packs."+plugin.getName().toLowerCase()+".messages."+msg, text);
		File langfile = new File(plugin.getDataFolder().getParentFile().getParentFile(), "config.yml");
		if (langfile.exists())
		{
            try {
				lang.save(langfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    public String getConfigMessage(String msg)
    {
    	return ChatColor.translateAlternateColorCodes('&',lang.getString("prefix")) + ChatColor.translateAlternateColorCodes('&', lang.getString("packs."+plugin.getName().toLowerCase()+".messages."+msg));
    }
    
    public Double getValue(Enchantment enchant, Integer enchlvl, String custom)
    {
    	String value = config.getString("enchants."+enchant.getKey().getKey()+"."+custom);
    	value = value.replaceAll("%lvl%", enchlvl.toString());
    	Object num = 0;
	    try {
		num = engine.eval(value); } 
	    catch (ScriptException e1) {e1.printStackTrace();}
	    if (num instanceof Double)
	    {
	        return (Double) num;
	    }
	    else
	    {
	    	return Double.valueOf((Integer)num);
	    }
    }
    
    public Double getChance(Enchantment enchant, Integer enchlvl)
    {
    	Object num = 0;
    	String chance = config.getString("enchants."+enchant.getKey().getKey()+".chance");
    	chance = chance.replaceAll("%lvl%", enchlvl.toString());
	    try {
		num = engine.eval(chance); } 
	    catch (ScriptException e1) {e1.printStackTrace();}
	    if (num instanceof Double)
	    {
	        return (Double) num;
	    }
	    else
	    {
	    	return Double.valueOf((Integer)num);
	    }
    }
    
    public List<Material> getFortuneDrops()
    {
    	List<Material> fortuneapply = new ArrayList<Material>(Arrays.asList());
    	List<String> list;
    	list = config.getStringList("misc.applyfortuneon");
    	for (int i=0;i<list.size();i++)
    	fortuneapply.add(Material.getMaterial(list.get(i)));
    	return fortuneapply;
    }
    
    public void ChangeTokens(Player p, Integer amount)
    {
    	File DataFolder = new File(pl.getDataFolder(), "data");
		File userstore = new File(DataFolder, "usernamestore.yml");
		FileConfiguration usconfig = YamlConfiguration.loadConfiguration(userstore);
		String uid = usconfig.getString(p.getName());
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
    
    public void SetTokens(Player p, Integer amount)
    {
    	File DataFolder = new File(pl.getDataFolder(), "data");
		File userstore = new File(DataFolder, "usernamestore.yml");
		FileConfiguration usconfig = YamlConfiguration.loadConfiguration(userstore);
		String uid = usconfig.getString(p.getName());
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
    
    public Integer GetTokens(Player p)
    {
    	File DataFolder = new File(pl.getDataFolder(), "data");
		File userstore = new File(DataFolder, "usernamestore.yml");
		FileConfiguration usconfig = YamlConfiguration.loadConfiguration(userstore);
		String uid = usconfig.getString(p.getName());
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
