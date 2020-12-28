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
	
	public static FileConfiguration config;
	public static FileConfiguration lang;
	public static File configFile;
	public static File langFile;
	private JavaPlugin plugin;
	private ScriptEngineManager mgr = new ScriptEngineManager();
    private ScriptEngine engine = mgr.getEngineByName("JavaScript");
    private Plugin pl;
	
	public EnchantHandler(JavaPlugin plugin)
	{
        this.plugin = plugin;
        pl = Bukkit.getPluginManager().getPlugin("EnchantmentsPlusMinus");
	}
	
	public FileConfiguration getConfig()
	{
		return config;
	}
		
	public void setDefaultLangMessage(String msg, String text)
	{
		if (lang.get("packs."+plugin.getName().toLowerCase()+".messages."+msg) == null)
		{
			lang.set("packs."+plugin.getName().toLowerCase()+".messages."+msg, text);
			if (langFile.exists())
			{
				try {
					lang.save(langFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setLangMessage(String msg, String text)
	{
		lang.set("packs."+plugin.getName().toLowerCase()+".messages."+msg, text);
		if (langFile.exists())
		{
            try {
				lang.save(langFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
    public String getLangMessage(String msg)
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
    
    public Object get(Enchantment enchant, String toget)
    {
    	Object value = config.get("enchants."+enchant.getKey().getKey()+"."+toget);
        return value;
    }
    
    public Object getMiscValue(String path)
    {
    	Object value = config.get("misc."+path);
        return value;
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
    	for (String i : list)
    	fortuneapply.add(Material.getMaterial(i));
    	return fortuneapply;
    }
    
    public void setDefaultCostType(Enchantment enchant, String type)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost.type", type);
    }
    
    public void setDefaultCostStartValue(Enchantment enchant, Integer value)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost.startvalue", value);
    }
    
    public void setDefaultCostValue(Enchantment enchant, Integer value)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost.value", value);
    }
    
    public void setDefaultCostPrice(Enchantment enchant, Integer enchlvl, Integer price)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost."+enchlvl.toString(), price);
    }
    
    public void setDefaultCostMulti(Enchantment enchant, Double multi)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost.multi", multi);
    }
    
    public void setDefaultMaxLevel(Enchantment enchant, Integer level)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".maxlevel", level);
    }
    
    public void setDefaultScrapValue(Enchantment enchant, Integer value)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".scrapvalue", value);
    }
    
    public void setDefaultUpgradeIcon(Enchantment enchant, Material icon)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".upgradeicon", icon.getKey().getKey());
    }
    
    public void setDefaultUpgradeIcon(Enchantment enchant, String material)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".upgradeicon", material);
    }
    
    public void setDefaultUpgradeDesc(Enchantment enchant, String desc)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".upgradedesc", desc);
    }
    
    public void setDefaultEnchantChance(Enchantment enchant, String chance)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".chance", chance);
    }
    
    public void setDefaultEnchantValue(Enchantment enchant, String toset, Object value)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+"."+toset, value);
    }
    
    public void setDefaultMiscValue(String path, Object value)
    {
    	setConfigValue("misc."+path, value);
    }
    
    public void autoFillEnchantConfig(Enchantment enchant, String desc, Integer cost)
    {
    	setDefaultMaxLevel(enchant, 10);
    	setDefaultScrapValue(enchant, cost/2);
    	setDefaultUpgradeIcon(enchant, Material.BOOK);
    	setDefaultUpgradeDesc(enchant, desc);
    	setDefaultCostType(enchant, "linear");
    	setDefaultCostStartValue(enchant, cost);
    	setDefaultCostValue(enchant, cost);
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
    
    private void setConfigValue(String path, Object replace)
    {
		if (config.get(path) == null)
		{
			config.set(path, replace);
			if (configFile.exists())
			{
				try {
					config.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
